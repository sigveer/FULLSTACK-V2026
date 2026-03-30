<script setup lang="ts">
import axios from 'axios'
import { computed, ref } from 'vue'
import { AlertTriangle } from 'lucide-vue-next'
import { toast } from 'vue-sonner'
import AppLayout from '@/components/layout/AppLayout.vue'
import Button from '@/components/ui/button/Button.vue'
import { Separator } from '@/components/ui/separator'
import { SidebarTrigger } from '@/components/ui/sidebar'
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
type SortOrder = 'NEWEST_FIRST' | 'OLDEST_FIRST'

const auth = useAuthStore()
const deviationsQuery = useDeviationsQuery()
const createDeviation = useCreateDeviationMutation()
const updateDeviation = useUpdateDeviationMutation()
const updateDeviationStatus = useUpdateDeviationStatusMutation()
const deleteDeviation = useDeleteDeviationMutation()

const activeModuleFilter = ref<ModuleFilter>('ALL')
const sortOrder = ref<SortOrder>('NEWEST_FIRST')
const createDialogOpen = ref(false)
const editDialogOpen = ref(false)
const activeDeviation = ref<Deviation | null>(null)

const deviations = computed(() => deviationsQuery.data.value ?? [])
const canManage = computed(() => auth.role === 'ADMIN' || auth.role === 'MANAGER')
const membersQuery = useOrganizationMembersQuery(canManage)

const assigneeOptions = computed(() => {
  if (canManage.value) {
    return (membersQuery.data.value ?? []).map((member) => ({
      userId: member.userId,
      label: `${member.userFullName} (${member.role})`,
    }))
  }

  if (!auth.user) {
    return []
  }

  return [
    {
      userId: auth.user.id,
      label: `${auth.user.fullName} (Deg)`,
    },
  ]
})

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
    key: 'RESOLVED',
    label: 'Løst',
    count: countByStatus(deviations.value, 'RESOLVED'),
    className: 'status-card--resolved',
  },
  {
    key: 'CLOSED',
    label: 'Lukket',
    count: countByStatus(deviations.value, 'CLOSED'),
    className: 'status-card--closed',
  },
])

const moduleFilters = computed(() => [
  { label: `Alle (${deviations.value.length})`, value: 'ALL' as const },
  { label: `IK-Mat (${countByModule(deviations.value, 'IK_MAT')})`, value: 'IK_MAT' as const },
  {
    label: `IK-Alkohol (${countByModule(deviations.value, 'IK_ALKOHOL')})`,
    value: 'IK_ALKOHOL' as const,
  },
])

const filteredAndSortedDeviations = computed(() => {
  const filtered =
    activeModuleFilter.value === 'ALL'
      ? deviations.value
      : deviations.value.filter((item) => item.module === activeModuleFilter.value)

  return [...filtered].sort((a, b) => {
    const aTime = new Date(a.reportedAt).getTime()
    const bTime = new Date(b.reportedAt).getTime()
    return sortOrder.value === 'NEWEST_FIRST' ? bTime - aTime : aTime - bTime
  })
})

function countByStatus(items: Deviation[], status: DeviationStatus): number {
  return items.filter((item) => item.status === status).length
}

function countByModule(items: Deviation[], module: DeviationModule): number {
  return items.filter((item) => item.module === module).length
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

async function handleDelete(id: number) {
  try {
    await deleteDeviation.mutateAsync(id)
    toast.success('Avvik slettet')
  } catch (error) {
    handleMutationError(error, 'Kunne ikke slette avvik')
  }
}

async function handleStatusUpdate(payload: { id: number; status: DeviationStatus }) {
  try {
    await updateDeviationStatus.mutateAsync({
      id: payload.id,
      payload: { status: payload.status },
    })
    toast.success('Status oppdatert')
  } catch (error) {
    handleMutationError(error, 'Kunne ikke oppdatere status')
  }
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

      <section class="status-grid" aria-label="Statusoversikt">
        <article v-for="card in statusCards" :key="card.key" :class="['status-card', card.className]">
          <span>{{ card.label }}</span>
          <strong>{{ card.count }}</strong>
        </article>
      </section>

      <section class="filters-row">
        <div class="module-filters">
          <button
            v-for="filter in moduleFilters"
            :key="filter.value"
            type="button"
            class="filter-button"
            :class="{ 'filter-button--active': activeModuleFilter === filter.value }"
            @click="activeModuleFilter = filter.value"
          >
            {{ filter.label }}
          </button>
        </div>

        <label class="sorter">
          <span>Sorter:</span>
          <select v-model="sortOrder">
            <option value="NEWEST_FIRST">Nyeste først</option>
            <option value="OLDEST_FIRST">Eldste først</option>
          </select>
        </label>
      </section>

      <section class="list-section">
        <p v-if="deviationsQuery.isLoading.value" class="state-line">Laster avvik...</p>

        <p v-else-if="deviationsQuery.isError.value" class="state-line state-line--danger">
          Kunne ikke hente avvik.
        </p>

        <div v-else-if="filteredAndSortedDeviations.length === 0" class="empty-state">
          <div class="empty-state-bg" />
          <div class="empty-state-inner">
            <div class="empty-state-icon">
              <AlertTriangle :stroke-width="1.5" />
            </div>
            <div class="empty-state-text">
              <h3>Ingen avvik ennå</h3>
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
            @edit="handleEdit"
            @delete="handleDelete"
            @update-status="handleStatusUpdate"
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
  margin-top: 5px;
  color: var(--text-secondary);
  font-size: 1.08rem;
}

.status-grid {
  display: grid;
  gap: 10px;
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.status-card {
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  background: var(--card-bg);
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.status-card span {
  font-size: 1rem;
}

.status-card strong {
  font-size: 2rem;
  letter-spacing: -0.02em;
}

.status-card--open {
  border-color: #e7bdbd;
}

.status-card--open strong {
  color: #a62929;
}

.status-card--in-progress {
  border-color: #eccf9f;
}

.status-card--in-progress strong {
  color: #946013;
}

.status-card--resolved {
  border-color: #b8d8b1;
}

.status-card--resolved strong {
  color: #3c8f2c;
}

.status-card--closed {
  border-color: #d8d8d8;
}

.filters-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.module-filters {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.filter-button {
  border: 1px solid #d2d2cf;
  background: #f4f4f2;
  color: #434a52;
  border-radius: var(--radius-md);
  padding: 8px 14px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 150ms ease;
}

.filter-button--active {
  background: var(--brand);
  color: #fff;
  border-color: var(--brand);
}

.sorter {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--text-secondary);
}

.sorter select {
  height: 2.25rem;
  border-radius: var(--radius-md);
  border: 1px solid #d2d2cf;
  padding: 0 10px;
  background: #fff;
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

@media (max-width: 980px) {
  .status-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 760px) {
  .header-row {
    flex-direction: column;
  }

  .status-grid {
    grid-template-columns: 1fr;
  }

  .filters-row {
    align-items: stretch;
  }

  .sorter {
    justify-content: space-between;
    width: 100%;
  }

  .sorter select {
    width: 100%;
    max-width: 220px;
  }
}
</style>
