<script setup lang="ts">
import axios from 'axios'
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { AlertTriangle, Trash2 } from 'lucide-vue-next'
import { toast } from 'vue-sonner'
import AppLayout from '@/components/layout/AppLayout.vue'
import Button from '@/components/ui/button/Button.vue'
import AlertDialog from '@/components/ui/alert-dialog/AlertDialog.vue'
import AlertDialogAction from '@/components/ui/alert-dialog/AlertDialogAction.vue'
import AlertDialogCancel from '@/components/ui/alert-dialog/AlertDialogCancel.vue'
import AlertDialogContent from '@/components/ui/alert-dialog/AlertDialogContent.vue'
import AlertDialogDescription from '@/components/ui/alert-dialog/AlertDialogDescription.vue'
import AlertDialogFooter from '@/components/ui/alert-dialog/AlertDialogFooter.vue'
import AlertDialogHeader from '@/components/ui/alert-dialog/AlertDialogHeader.vue'
import AlertDialogTitle from '@/components/ui/alert-dialog/AlertDialogTitle.vue'
import AlertDialogTrigger from '@/components/ui/alert-dialog/AlertDialogTrigger.vue'
import { Separator } from '@/components/ui/separator'
import { SidebarTrigger } from '@/components/ui/sidebar'
import Select from '@/components/ui/select/Select.vue'
import SelectContent from '@/components/ui/select/SelectContent.vue'
import SelectItem from '@/components/ui/select/SelectItem.vue'
import SelectTrigger from '@/components/ui/select/SelectTrigger.vue'
import SelectValue from '@/components/ui/select/SelectValue.vue'
import DeviationListCard from '@/components/deviations/DeviationListCard.vue'
import DeviationFormDialog from '@/components/deviations/DeviationFormDialog.vue'
import { useAuthStore } from '@/stores/auth'
import {
  useCreateDeviationMutation,
  useDeviationsQuery,
  useDeleteDeviationMutation,
  useOrganizationMembersQuery,
  useUpdateDeviationMutation,
  useUpdateDeviationStatusMutation,
} from '@/composables/useDeviations'
import type {
  CreateDeviationRequest,
  Deviation,
  DeviationModule,
  DeviationStatus,
  UpdateDeviationRequest,
} from '@/types/deviation'

type ModuleFilter = 'ALL' | DeviationModule
type StatusFilter = 'ACTIVE' | 'IN_PROGRESS' | 'SOLVED'
type SortOrder = 'NEWEST_FIRST' | 'OLDEST_FIRST'

const router = useRouter()
const auth = useAuthStore()
const deviationsQuery = useDeviationsQuery()
const createDeviation = useCreateDeviationMutation()
const updateDeviation = useUpdateDeviationMutation()
const updateDeviationStatus = useUpdateDeviationStatusMutation()
const deleteDeviation = useDeleteDeviationMutation()

const activeModuleFilter = ref<ModuleFilter>('ALL')
const activeStatusFilter = ref<StatusFilter>('ACTIVE')
const sortOrder = ref<SortOrder>('NEWEST_FIRST')
const createDialogOpen = ref(false)
const editDialogOpen = ref(false)
const activeDeviation = ref<Deviation | null>(null)
const transitioningResolvedIds = ref<number[]>([])
const statusOverrides = ref<Record<number, DeviationStatus>>({})

const deviations = computed(() => deviationsQuery.data.value ?? [])
const canManage = computed(() => auth.role === 'ADMIN' || auth.role === 'MANAGER')
const membersQuery = useOrganizationMembersQuery(canManage)

watch(deviations, (items) => {
  const nextOverrides: Record<number, DeviationStatus> = {}
  const statusById = new Map(items.map((item) => [item.id, item.status]))

  for (const [idKey, overriddenStatus] of Object.entries(statusOverrides.value)) {
    const id = Number(idKey)
    const serverStatus = statusById.get(id)
    if (serverStatus !== undefined && serverStatus !== overriddenStatus) {
      nextOverrides[id] = overriddenStatus
    }
  }

  statusOverrides.value = nextOverrides
})

const assigneeOptions = computed(() => {
  if (canManage.value) {
    return (membersQuery.data.value ?? []).map((member) => ({
      userId: member.userId,
      label: `${member.userFullName} (${member.role})`,
    }))
  }

  if (!auth.user) return []

  return [{ userId: auth.user.id, label: `${auth.user.fullName} (Deg)` }]
})

const moduleCards = computed(() => [
  {
    key: 'IK_MAT',
    label: 'IK-Mat',
    count: countByModule(deviations.value, 'IK_MAT'),
    className: 'status-card--neutral',
  },
  {
    key: 'IK_ALKOHOL',
    label: 'IK-Alkohol',
    count: countByModule(deviations.value, 'IK_ALKOHOL'),
    className: 'status-card--neutral',
  },
])

const statusCards = computed(() => [
  {
    key: 'OPEN',
    label: 'Åpne',
    count: countByStatus(deviations.value, 'OPEN'),
    className: 'status-card--open',
  },
  {
    key: 'IN_PROGRESS',
    label: 'Under behandling',
    count: countByStatus(deviations.value, 'IN_PROGRESS'),
    className: 'status-card--in-progress',
  },
  {
    key: 'SOLVED',
    label: 'Løst',
    count: countSolved(deviations.value),
    className: 'status-card--resolved',
  },
])

const statusFilters: Array<{ label: string; value: StatusFilter }> = [
  { label: 'Aktive', value: 'ACTIVE' },
  { label: 'Under behandling', value: 'IN_PROGRESS' },
  { label: 'Løste', value: 'SOLVED' },
]

const moduleFilters: Array<{ label: string; value: ModuleFilter }> = [
  { label: 'Alle', value: 'ALL' },
  { label: 'IK-Mat', value: 'IK_MAT' },
  { label: 'IK-Alkohol', value: 'IK_ALKOHOL' },
]

const filteredAndSortedDeviations = computed(() => {
  const moduleFiltered =
    activeModuleFilter.value === 'ALL'
      ? deviations.value
      : deviations.value.filter((item) => item.module === activeModuleFilter.value)

  const statusFiltered = moduleFiltered.filter((item) => {
    const status = getEffectiveStatus(item)
    const isTransitioning = transitioningResolvedIds.value.includes(item.id)

    switch (activeStatusFilter.value) {
      case 'ACTIVE':
        return status === 'OPEN' || isTransitioning
      case 'IN_PROGRESS':
        return status === 'IN_PROGRESS'
      case 'SOLVED':
        return isSolved(status) && !isTransitioning
    }
  })

  return [...statusFiltered].sort((a, b) => {
    const aTime = new Date(a.reportedAt).getTime()
    const bTime = new Date(b.reportedAt).getTime()
    return sortOrder.value === 'NEWEST_FIRST' ? bTime - aTime : aTime - bTime
  })
})

function countByStatus(items: Deviation[], status: DeviationStatus): number {
  return items.filter((item) => getEffectiveStatus(item) === status).length
}

function countSolved(items: Deviation[]): number {
  return items.filter((item) => isSolved(getEffectiveStatus(item))).length
}

function countByModule(items: Deviation[], module: DeviationModule): number {
  return items.filter((item) => item.module === module).length
}

function getEffectiveStatus(item: Deviation): DeviationStatus {
  return statusOverrides.value[item.id] ?? item.status
}

function isSolved(status: DeviationStatus): boolean {
  return status === 'RESOLVED'
}

function handleOpenDetails(deviation: Deviation) {
  router.push(`/avvik/${deviation.id}`)
}

function handleEdit(deviation: Deviation) {
  activeDeviation.value = deviation
  editDialogOpen.value = true
}

function openCreateDialog() {
  createDialogOpen.value = true
}

async function handleCreate(payload: CreateDeviationRequest) {
  try {
    await createDeviation.mutateAsync(payload)
    createDialogOpen.value = false
    toast.success('Avvik registrert')
  } catch (error) {
    handleMutationError(error, 'Kunne ikke registrere avvik')
  }
}

async function handleUpdate(payload: { id: number; data: UpdateDeviationRequest }) {
  try {
    await updateDeviation.mutateAsync({
      id: payload.id,
      payload: payload.data,
    })
    editDialogOpen.value = false
    activeDeviation.value = null
    toast.success('Avvik oppdatert')
  } catch (error) {
    handleMutationError(error, 'Kunne ikke oppdatere avvik')
  }
}

async function handleDeleteAllSolved() {
  const ids = filteredAndSortedDeviations.value.map((item) => item.id)
  if (ids.length === 0) return

  try {
    for (const id of ids) {
      await deleteDeviation.mutateAsync(id)
    }
    toast.success(`Slettet ${ids.length} løste avvik`)
  } catch (error) {
    handleMutationError(error, 'Kunne ikke slette alle løste avvik')
  }
}

async function handleStatusUpdate(payload: { id: number; status: DeviationStatus }) {
  const nextStatus = payload.status
  const previous = deviations.value.find((item) => item.id === payload.id)
  const previousStatus = previous ? getEffectiveStatus(previous) : null
  const shouldDelayMove = previousStatus ? isSolved(nextStatus) && !isSolved(previousStatus) : false

  statusOverrides.value = { ...statusOverrides.value, [payload.id]: nextStatus }

  if (shouldDelayMove && !transitioningResolvedIds.value.includes(payload.id)) {
    transitioningResolvedIds.value = [...transitioningResolvedIds.value, payload.id]
  }

  try {
    await updateDeviationStatus.mutateAsync({ id: payload.id, payload: { status: nextStatus } })

    if (shouldDelayMove) {
      setTimeout(() => {
        transitioningResolvedIds.value = transitioningResolvedIds.value.filter((id) => id !== payload.id)
      }, 1500)
    }

    toast.success('Status oppdatert')
  } catch (error) {
    const nextOverrides = { ...statusOverrides.value }
    delete nextOverrides[payload.id]
    statusOverrides.value = nextOverrides
    transitioningResolvedIds.value = transitioningResolvedIds.value.filter((id) => id !== payload.id)
    handleMutationError(error, 'Kunne ikke oppdatere status')
  }
}

async function handleDelete(id: number) {
  try {
    await deleteDeviation.mutateAsync(id)
    const nextOverrides = { ...statusOverrides.value }
    delete nextOverrides[id]
    statusOverrides.value = nextOverrides
    toast.success('Avvik slettet')
  } catch (error) {
    handleMutationError(error, 'Kunne ikke slette avvik')
  }
}

function onSortChange(value: string) {
  sortOrder.value = value as SortOrder
}

function handleMutationError(error: unknown, fallbackMessage: string) {
  if (axios.isAxiosError(error)) {
    const message = error.response?.data?.error?.message
    if (typeof message === 'string' && message.trim().length > 0) {
      toast.error(message)
      return
    }
  }
  toast.error(fallbackMessage)
}
</script>

<template>
  <AppLayout>
    <header class="page-header">
      <div class="page-header-inner">
        <SidebarTrigger />
        <Separator orientation="vertical" class="header-separator" />
        <span class="page-title">Avvik</span>
      </div>
    </header>

    <div class="page-content">
      <section class="header-row">
        <div>
          <h1>Avviksoversikt</h1>
          <p>Alle registrerte avvik for IK-Mat og IK-Alkohol</p>
        </div>

        <Button @click="openCreateDialog">
          + Rapporter avvik
        </Button>
      </section>

      <section class="cards-section" aria-label="Statusoversikt">
        <article v-for="card in moduleCards" :key="card.key" :class="['status-card', card.className]">
          <span>{{ card.label }}</span>
          <strong>{{ card.count }}</strong>
        </article>

        <article v-for="card in statusCards" :key="card.key" :class="['status-card', card.className]">
          <span>{{ card.label }}</span>
          <strong>{{ card.count }}</strong>
        </article>
      </section>

      <section class="filters-row">
        <div class="filters-left">
          <div class="filter-group">
            <button
              v-for="filter in statusFilters"
              :key="filter.value"
              class="filter-button"
              :class="{ 'filter-button--active': activeStatusFilter === filter.value }"
              type="button"
              @click="activeStatusFilter = filter.value"
            >
              {{ filter.label }}
            </button>
          </div>

          <div class="filter-group">
            <button
              v-for="filter in moduleFilters"
              :key="filter.value"
              class="filter-button"
              :class="{ 'filter-button--active': activeModuleFilter === filter.value }"
              type="button"
              @click="activeModuleFilter = filter.value"
            >
              {{ filter.label }}
            </button>
          </div>
        </div>

        <div class="filters-right">
          <Select :model-value="sortOrder" default-value="NEWEST_FIRST" @update:model-value="onSortChange">
            <SelectTrigger class="sort-trigger">
              <SelectValue placeholder="Nyeste først" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="NEWEST_FIRST">Nyeste først</SelectItem>
              <SelectItem value="OLDEST_FIRST">Eldste først</SelectItem>
            </SelectContent>
          </Select>

          <AlertDialog v-if="activeStatusFilter === 'SOLVED'">
            <AlertDialogTrigger>
              <Button variant="destructive" class="delete-all-btn" :disabled="filteredAndSortedDeviations.length === 0">
                <Trash2 />
                Slett alle løste
              </Button>
            </AlertDialogTrigger>
            <AlertDialogContent>
              <AlertDialogHeader>
                <AlertDialogTitle>Slette alle løste avvik?</AlertDialogTitle>
                <AlertDialogDescription>
                  Dette sletter alle løste avvik i valgt filtrering permanent.
                </AlertDialogDescription>
              </AlertDialogHeader>
              <AlertDialogFooter>
                <AlertDialogCancel>Avbryt</AlertDialogCancel>
                <AlertDialogAction variant="destructive" @click="handleDeleteAllSolved">
                  Slett alle
                </AlertDialogAction>
              </AlertDialogFooter>
            </AlertDialogContent>
          </AlertDialog>
        </div>
      </section>

      <section class="list-section">
        <p v-if="deviationsQuery.isLoading.value" class="state-line">Laster avvik...</p>

        <div v-else-if="deviationsQuery.isError.value" class="empty-state">
          <div class="empty-state-bg" />
          <div class="empty-state-inner">
            <div class="empty-state-icon">
              <AlertTriangle :stroke-width="1.5" />
            </div>
            <div class="empty-state-text">
              <h3>Kunne ikke hente avvik</h3>
              <p>Noe gikk galt under lasting av avvik. Prøv igjen senere.</p>
            </div>
          </div>
        </div>

        <div v-else-if="filteredAndSortedDeviations.length === 0" class="empty-state">
          <div class="empty-state-bg" />
          <div class="empty-state-inner">
            <div class="empty-state-icon">
              <AlertTriangle :stroke-width="1.5" />
            </div>
            <div class="empty-state-text">
              <h3>Ingen avvik i valgt liste</h3>
              <p>Det finnes ingen registrerte avvik med valgte filtre.</p>
            </div>
          </div>
        </div>

        <div v-else class="list-wrap">
          <DeviationListCard
            v-for="item in filteredAndSortedDeviations"
            :key="item.id"
            :deviation="item"
            :can-manage="canManage"
            @open="handleOpenDetails"
            @edit="handleEdit"
            @update-status="handleStatusUpdate"
            @delete="handleDelete"
          />
        </div>
      </section>
    </div>

    <DeviationFormDialog
      v-model:open="createDialogOpen"
      mode="create"
      :submitting="createDeviation.isPending.value"
      :assignees="assigneeOptions"
      @create="handleCreate"
    />

    <DeviationFormDialog
      v-model:open="editDialogOpen"
      mode="edit"
      :initial-deviation="activeDeviation"
      :submitting="updateDeviation.isPending.value"
      :assignees="assigneeOptions"
      @update="handleUpdate"
    />
  </AppLayout>
</template>

<style scoped>
.page-header { display: flex; height: 4rem; flex-shrink: 0; align-items: center; }
.page-header-inner { display: flex; align-items: center; gap: 0.5rem; padding: 0 1rem; }
.header-separator { height: 1rem !important; width: 1px !important; margin-right: 0.5rem; }
.page-title { font-weight: 500; color: hsl(var(--sidebar-primary, 245 43% 52%)); }
.page-content { display: flex; flex: 1; flex-direction: column; gap: 1rem; padding: 0 1rem 1rem; }

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

h1 {
  margin: 0;
  font-size: 2.4rem;
  letter-spacing: -0.02em;
}

.header-row p {
  margin-top: 6px;
  color: var(--text-secondary);
  font-size: 1.08rem;
}

.cards-section {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 10px;
}

.status-card {
  border: 2px solid #d1d5db;
  border-radius: var(--radius-md);
  padding: 12px;
  min-height: 6.25rem;
  display: flex;
  flex-direction: column;
  gap: 8px;
  background: var(--card-bg);
}

.status-card span { font-size: 1rem; }
.status-card strong { font-size: 2rem; letter-spacing: -0.02em; }

.status-card--open {
  background: #f5e8ea;
  border-color: #e0aeb5;
}
.status-card--open strong { color: #a62929; }

.status-card--in-progress {
  background: #f1e7d6;
  border-color: #e0bf81;
}
.status-card--in-progress strong { color: #946013; }

.status-card--resolved {
  background: #e4eddc;
  border-color: #b7d18e;
}
.status-card--resolved strong { color: #3c8f2c; }

.status-card--neutral {
  background: #ffffff;
  border-color: #d1d5db;
}

.status-card--neutral strong {
  color: #111827;
}

.filters-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.filters-left {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
  align-items: center;
}

.filter-group {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.filter-button {
  border: 1px solid #cfcfc9;
  border-radius: var(--radius-pill);
  background: #f3f3f2;
  padding: 8px 16px;
  cursor: pointer;
  font-size: 0.95rem;
  color: #32363d;
}

.filter-button--active {
  border-color: #4f4bcf;
  background: #eeedff;
  color: #403db1;
  font-weight: 600;
}

.filters-right {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: auto;
}

.sort-trigger {
  width: 10rem;
}

.delete-all-btn {
  background-color: #fde8e8;
  color: #c62828;
  border: none;
  box-shadow: none;
}
.delete-all-btn:hover {
  background-color: #fad4d4;
}
.delete-all-btn:active {
  background-color: #f5c2c2;
}

.list-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.list-wrap {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.state-line {
  border-radius: var(--radius-md);
  border: 1px solid hsl(var(--border));
  background: hsl(var(--card));
  padding: 12px;
  color: var(--text-secondary);
}

.state-line--danger {
  border-color: hsl(var(--destructive) / 0.35);
  color: hsl(var(--destructive));
}

.empty-state {
  position: relative;
  display: flex;
  min-height: 260px;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border-radius: 1rem;
  border: 2px dashed hsl(var(--muted-foreground) / 0.2);
  background: linear-gradient(to bottom right, hsl(var(--muted) / 0.4), hsl(var(--muted) / 0.2), hsl(var(--background)));
  padding: 2rem;
}

.empty-state-bg {
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse at center, hsl(var(--muted)) 0%, transparent 70%);
  opacity: 0.5;
}

.empty-state-inner {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  text-align: center;
}

.empty-state-icon {
  display: flex;
  height: 5rem;
  width: 5rem;
  align-items: center;
  justify-content: center;
  border-radius: 1rem;
  background-color: hsl(var(--primary) / 0.1);
  box-shadow: 0 0 0 4px hsl(var(--primary) / 0.05);
}

.empty-state-icon :deep(svg) {
  width: 2.5rem;
  height: 2.5rem;
  color: hsl(var(--primary) / 0.7);
}

.empty-state-text h3 {
  font-size: 1.125rem;
  font-weight: 600;
  letter-spacing: -0.01em;
}

.empty-state-text p {
  max-width: 24rem;
  font-size: 0.875rem;
  color: hsl(var(--muted-foreground));
  margin-top: 0.25rem;
}

@media (max-width: 1100px) {
  .cards-section { grid-template-columns: repeat(2, minmax(0, 1fr)); }
}

@media (max-width: 760px) {
  .header-row { flex-direction: column; }
  .cards-section { grid-template-columns: 1fr; }
  .filters-row { align-items: stretch; }
  .filters-left { flex-direction: column; }
  .filters-right { width: 100%; justify-content: flex-end; }
}
</style>
