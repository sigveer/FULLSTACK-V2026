<script setup lang="ts">
import axios from 'axios'
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import {AlertTriangle, Plus, Trash2} from 'lucide-vue-next'
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
import FoodDeviationListCard from '@/components/deviations/FoodDeviationListCard.vue'
import AlcoholDeviationListCard from '@/components/deviations/AlcoholDeviationListCard.vue'
import FoodDeviationFormDialog from '@/components/deviations/FoodDeviationFormDialog.vue'
import AlcoholDeviationFormDialog from '@/components/deviations/AlcoholDeviationFormDialog.vue'
import PenaltyPointsStatus from '@/components/deviations/PenaltyPointsStatus.vue'
import PenaltyPointsGuide from '@/components/deviations/PenaltyPointsGuide.vue'
import { useAuthStore } from '@/stores/auth'
import { useMembersQuery } from '@/composables/useMembers'
import {
  useFoodDeviationsQuery,
  useCreateFoodDeviationMutation,
  useUpdateFoodDeviationMutation,
  useDeleteFoodDeviationMutation,
} from '@/composables/useFoodDeviations'
import {
  useAlcoholDeviationsQuery,
  useCreateAlcoholDeviationMutation,
  useUpdateAlcoholDeviationMutation,
  useDeleteAlcoholDeviationMutation,
} from '@/composables/useAlcoholDeviations'
import { usePenaltyPointsQuery } from '@/composables/usePenaltyPoints'
import Dialog from '@/components/ui/dialog/Dialog.vue'
import DialogContent from '@/components/ui/dialog/DialogContent.vue'
import type {
  CombinedDeviation,
  DeviationModule,
  FoodDeviation,
  FoodDeviationStatus,
  AlcoholDeviation,
  AlcoholDeviationStatus,
  CreateFoodDeviationRequest,
  CreateAlcoholDeviationRequest,
  UpdateFoodDeviationRequest,
  UpdateAlcoholDeviationRequest,
} from '@/types/deviation'

type ModuleFilter = 'ALL' | DeviationModule
type StatusFilter = 'OPEN' | 'UNDER_TREATMENT' | 'CLOSED'
type SortOrder = 'NEWEST_FIRST' | 'OLDEST_FIRST'

const router = useRouter()
const auth = useAuthStore()
const canManage = computed(() => auth.role === 'ADMIN' || auth.role === 'MANAGER')
const membersQuery = useMembersQuery(canManage)

// Queries
const foodQuery = useFoodDeviationsQuery()
const alcoholQuery = useAlcoholDeviationsQuery()
const penaltyQuery = usePenaltyPointsQuery()

// Mutations
const createFood = useCreateFoodDeviationMutation()
const updateFood = useUpdateFoodDeviationMutation()
const deleteFood = useDeleteFoodDeviationMutation()
const createAlcohol = useCreateAlcoholDeviationMutation()
const updateAlcohol = useUpdateAlcoholDeviationMutation()
const deleteAlcohol = useDeleteAlcoholDeviationMutation()

// Filters
const activeModuleFilter = ref<ModuleFilter>('ALL')
const activeStatusFilter = ref<StatusFilter>('OPEN')
const sortOrder = ref<SortOrder>('NEWEST_FIRST')

// Dialogs
const createDialogOpen = ref(false)
const createTab = ref<DeviationModule>('IK_MAT')
const editFoodOpen = ref(false)
const editAlcoholOpen = ref(false)
const activeFoodDeviation = ref<FoodDeviation | null>(null)
const activeAlcoholDeviation = ref<AlcoholDeviation | null>(null)

// Member options for dropdowns
const memberOptions = computed(() => {
  if (canManage.value) {
    return (membersQuery.data.value ?? []).map((m) => ({
      userId: m.userId,
      label: `${m.userFullName} (${m.role})`,
    }))
  }
  if (!auth.user) return []
  return [{ userId: auth.user.id, label: `${auth.user.fullName} (Deg)` }]
})

// Combined list
const isLoading = computed(() => foodQuery.isLoading.value || alcoholQuery.isLoading.value)
const isError = computed(() => foodQuery.isError.value || alcoholQuery.isError.value)

const combinedDeviations = computed<CombinedDeviation[]>(() => {
  const food: CombinedDeviation[] = (foodQuery.data.value ?? []).map((d) => ({ _type: 'food' as const, data: d }))
  const alcohol: CombinedDeviation[] = (alcoholQuery.data.value ?? []).map((d) => ({ _type: 'alcohol' as const, data: d }))
  return [...food, ...alcohol]
})

const filteredAndSorted = computed(() => {
  let items = combinedDeviations.value

  // Module filter
  if (activeModuleFilter.value === 'IK_MAT') {
    items = items.filter((i) => i._type === 'food')
  } else if (activeModuleFilter.value === 'IK_ALKOHOL') {
    items = items.filter((i) => i._type === 'alcohol')
  }

  // Status filter
  items = items.filter((i) => i.data.status === activeStatusFilter.value)

  // Sort
  return [...items].sort((a, b) => {
    const aTime = new Date(a.data.reportedAt).getTime()
    const bTime = new Date(b.data.reportedAt).getTime()
    return sortOrder.value === 'NEWEST_FIRST' ? bTime - aTime : aTime - bTime
  })
})

// Counts
const foodCount = computed(() => (foodQuery.data.value ?? []).length)
const alcoholCount = computed(() => (alcoholQuery.data.value ?? []).length)
const openCount = computed(() => combinedDeviations.value.filter((i) => i.data.status === 'OPEN').length)
const underTreatmentCount = computed(() => combinedDeviations.value.filter((i) => i.data.status === 'UNDER_TREATMENT').length)
const closedCount = computed(() => combinedDeviations.value.filter((i) => i.data.status === 'CLOSED').length)

const moduleCards = computed(() => [
  { key: 'IK_MAT', label: 'IK-Mat', count: foodCount.value, className: 'status-card--neutral' },
  { key: 'IK_ALKOHOL', label: 'IK-Alkohol', count: alcoholCount.value, className: 'status-card--neutral' },
])

const statusCards = computed(() => [
  { key: 'OPEN', label: 'Åpne', count: openCount.value, className: 'status-card--open' },
  { key: 'UNDER_TREATMENT', label: 'Under behandling', count: underTreatmentCount.value, className: 'status-card--in-progress' },
  { key: 'CLOSED', label: 'Lukket', count: closedCount.value, className: 'status-card--resolved' },
])

const statusFilters: Array<{ label: string; value: StatusFilter }> = [
  { label: 'Åpne', value: 'OPEN' },
  { label: 'Under behandling', value: 'UNDER_TREATMENT' },
  { label: 'Lukket', value: 'CLOSED' },
]

const moduleFilters: Array<{ label: string; value: ModuleFilter }> = [
  { label: 'Alle', value: 'ALL' },
  { label: 'IK-Mat', value: 'IK_MAT' },
  { label: 'IK-Alkohol', value: 'IK_ALKOHOL' },
]

// --- Create flow ---
function openCreateDialog() {
  createTab.value = 'IK_MAT'
  createDialogOpen.value = true
}

async function handleCreateFood(payload: CreateFoodDeviationRequest) {
  try {
    await createFood.mutateAsync(payload)
    createDialogOpen.value = false
    toast.success('Matavvik registrert')
  } catch (err) { handleError(err, 'Kunne ikke registrere matavvik') }
}

async function handleCreateAlcohol(payload: CreateAlcoholDeviationRequest) {
  try {
    await createAlcohol.mutateAsync(payload)
    createDialogOpen.value = false
    toast.success('Alkoholavvik registrert')
  } catch (err) { handleError(err, 'Kunne ikke registrere alkoholavvik') }
}

// --- Edit flow ---
function handleEditFood(d: FoodDeviation) {
  activeFoodDeviation.value = d
  editFoodOpen.value = true
}

function handleEditAlcohol(d: AlcoholDeviation) {
  activeAlcoholDeviation.value = d
  editAlcoholOpen.value = true
}

async function handleUpdateFood(p: { id: number; data: UpdateFoodDeviationRequest }) {
  try {
    await updateFood.mutateAsync({ id: p.id, payload: p.data })
    editFoodOpen.value = false
    activeFoodDeviation.value = null
    toast.success('Matavvik oppdatert')
  } catch (err) { handleError(err, 'Kunne ikke oppdatere matavvik') }
}

async function handleUpdateAlcohol(p: { id: number; data: UpdateAlcoholDeviationRequest }) {
  try {
    await updateAlcohol.mutateAsync({ id: p.id, payload: p.data })
    editAlcoholOpen.value = false
    activeAlcoholDeviation.value = null
    toast.success('Alkoholavvik oppdatert')
  } catch (err) { handleError(err, 'Kunne ikke oppdatere alkoholavvik') }
}

// --- Delete ---
async function handleDeleteFood(id: number) {
  try {
    await deleteFood.mutateAsync(id)
    toast.success('Matavvik slettet')
  } catch (err) { handleError(err, 'Kunne ikke slette matavvik') }
}

async function handleDeleteAlcohol(id: number) {
  try {
    await deleteAlcohol.mutateAsync(id)
    toast.success('Alkoholavvik slettet')
  } catch (err) { handleError(err, 'Kunne ikke slette alkoholavvik') }
}

async function handleDeleteAllClosed() {
  const items = filteredAndSorted.value
  if (items.length === 0) return
  try {
    for (const item of items) {
      if (item._type === 'food') await deleteFood.mutateAsync(item.data.id)
      else await deleteAlcohol.mutateAsync(item.data.id)
    }
    toast.success(`Slettet ${items.length} lukkede avvik`)
  } catch (err) { handleError(err, 'Kunne ikke slette alle lukkede avvik') }
}

// --- Status change ---
async function handleFoodStatusChange(id: number, newStatus: FoodDeviationStatus) {
  try {
    await updateFood.mutateAsync({ id, payload: { status: newStatus } as UpdateFoodDeviationRequest })
    toast.success('Status oppdatert')
  } catch (err) { handleError(err, 'Kunne ikke oppdatere status') }
}

async function handleAlcoholStatusChange(id: number, newStatus: AlcoholDeviationStatus) {
  try {
    await updateAlcohol.mutateAsync({ id, payload: { status: newStatus } as UpdateAlcoholDeviationRequest })
    toast.success('Status oppdatert')
  } catch (err) { handleError(err, 'Kunne ikke oppdatere status') }
}

// --- Navigation ---
function handleOpenFood(d: FoodDeviation) {
  router.push(`/avvik/mat/${d.id}`)
}

function handleOpenAlcohol(d: AlcoholDeviation) {
  router.push(`/avvik/alkohol/${d.id}`)
}

function onSortChange(value: string) { sortOrder.value = value as SortOrder }

function handleError(error: unknown, fallback: string) {
  if (axios.isAxiosError(error)) {
    const msg = error.response?.data?.error?.message
    if (typeof msg === 'string' && msg.trim().length > 0) { toast.error(msg); return }
  }
  toast.error(fallback)
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
          <Plus :size="16" />
          Registrer avvik
        </Button>
      </section>

      <!-- Info cards -->
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

      <!-- Penalty points cards -->
      <section class="penalty-section">
        <PenaltyPointsStatus :summary="penaltyQuery.data.value ?? null" />
        <PenaltyPointsGuide />
      </section>

      <!-- Filters -->
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

          <AlertDialog v-if="activeStatusFilter === 'CLOSED'">
            <AlertDialogTrigger>
              <Button variant="destructive" class="delete-all-btn" :disabled="filteredAndSorted.length === 0">
                <Trash2 />
                Slett alle lukkede
              </Button>
            </AlertDialogTrigger>
            <AlertDialogContent>
              <AlertDialogHeader>
                <AlertDialogTitle>Slette alle lukkede avvik?</AlertDialogTitle>
                <AlertDialogDescription>
                  Dette sletter alle lukkede avvik i valgt filtrering permanent.
                </AlertDialogDescription>
              </AlertDialogHeader>
              <AlertDialogFooter>
                <AlertDialogCancel>Avbryt</AlertDialogCancel>
                <AlertDialogAction variant="destructive" @click="handleDeleteAllClosed">
                  Slett alle
                </AlertDialogAction>
              </AlertDialogFooter>
            </AlertDialogContent>
          </AlertDialog>
        </div>
      </section>

      <!-- Deviation list -->
      <section class="list-section">
        <p v-if="isLoading" class="state-line">Laster avvik...</p>

        <div v-else-if="isError" class="empty-state">
          <div class="empty-state-bg" />
          <div class="empty-state-inner">
            <div class="empty-state-icon"><AlertTriangle :stroke-width="1.5" /></div>
            <div class="empty-state-text">
              <h3>Kunne ikke hente avvik</h3>
              <p>Noe gikk galt under lasting av avvik. Prøv igjen senere.</p>
            </div>
          </div>
        </div>

        <div v-else-if="filteredAndSorted.length === 0" class="empty-state">
          <div class="empty-state-bg" />
          <div class="empty-state-inner">
            <div class="empty-state-icon"><AlertTriangle :stroke-width="1.5" /></div>
            <div class="empty-state-text">
              <h3>Ingen avvik i valgt liste</h3>
              <p>Det finnes ingen registrerte avvik med valgte filtre.</p>
            </div>
          </div>
        </div>

        <div v-else class="list-wrap">
          <template v-for="item in filteredAndSorted" :key="`${item._type}-${item.data.id}`">
            <FoodDeviationListCard
              v-if="item._type === 'food'"
              :deviation="item.data"
              :can-manage="canManage"
              @edit="handleEditFood"
              @delete="handleDeleteFood"
              @update-status="handleFoodStatusChange"
              @open="handleOpenFood"
            />
            <AlcoholDeviationListCard
              v-else
              :deviation="item.data"
              :can-manage="canManage"
              @edit="handleEditAlcohol"
              @delete="handleDeleteAlcohol"
              @update-status="handleAlcoholStatusChange"
              @open="handleOpenAlcohol"
            />
          </template>
        </div>
      </section>
    </div>

    <!-- Create dialog with tabs -->
    <Dialog :open="createDialogOpen" @update:open="(v) => createDialogOpen = v">
      <DialogContent class="create-dialog">
        <h2 class="create-dialog-title">Registrer avvik</h2>
        <div class="create-tabs">
          <button
            type="button"
            class="create-tab"
            :class="{ 'create-tab--active': createTab === 'IK_MAT' }"
            @click="createTab = 'IK_MAT'"
          >
            IK-Mat
          </button>
          <button
            type="button"
            class="create-tab"
            :class="{ 'create-tab--active': createTab === 'IK_ALKOHOL' }"
            @click="createTab = 'IK_ALKOHOL'"
          >
            IK-Alkohol
          </button>
        </div>
        <div class="create-form-scroll">
          <FoodDeviationFormDialog
            v-if="createTab === 'IK_MAT'"
            :open="true"
            mode="create"
            :submitting="createFood.isPending.value"
            :members="memberOptions"
            inline
            @create="handleCreateFood"
            @update:open="(v) => { if (!v) createDialogOpen = false }"
          />
          <AlcoholDeviationFormDialog
            v-else
            :open="true"
            mode="create"
            :submitting="createAlcohol.isPending.value"
            :members="memberOptions"
            :penalty-summary="penaltyQuery.data.value ?? null"
            inline
            @create="handleCreateAlcohol"
            @update:open="(v) => { if (!v) createDialogOpen = false }"
          />
        </div>
      </DialogContent>
    </Dialog>

    <!-- Edit food dialog -->
    <FoodDeviationFormDialog
      v-model:open="editFoodOpen"
      mode="edit"
      :initial="activeFoodDeviation"
      :submitting="updateFood.isPending.value"
      :members="memberOptions"
      @update="handleUpdateFood"
    />

    <!-- Edit alcohol dialog -->
    <AlcoholDeviationFormDialog
      v-model:open="editAlcoholOpen"
      mode="edit"
      :initial="activeAlcoholDeviation"
      :submitting="updateAlcohol.isPending.value"
      :members="memberOptions"
      :penalty-summary="penaltyQuery.data.value ?? null"
      @update="handleUpdateAlcohol"
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

h1 { margin: 0; font-size: 2.4rem; letter-spacing: -0.02em; }
.header-row p { margin-top: 6px; color: var(--text-secondary); font-size: 1.08rem; }

.create-dialog {
  max-width: 48rem;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.create-dialog-title {
  margin: 0;
  font-size: 1.2rem;
  font-weight: 700;
  letter-spacing: -0.01em;
  flex-shrink: 0;
}

.create-tabs {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
  padding: 4px 0 12px;
}

.create-tab {
  padding: 8px 20px;
  font-size: 0.9rem;
  font-weight: 500;
  background: hsl(var(--secondary));
  border: 1px solid hsl(var(--border));
  border-radius: var(--radius-pill);
  cursor: pointer;
  color: hsl(var(--muted-foreground));
  transition: all 150ms ease;
}

.create-tab--active {
  color: hsl(var(--foreground));
  font-weight: 600;
  background: hsl(var(--accent));
  border-color: hsl(var(--primary));
}

.create-form-scroll {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
  padding: 2px 2px 0;
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

.status-card--open { background: #f5e8ea; border-color: #e0aeb5; }
.status-card--open strong { color: #a62929; }
.status-card--in-progress { background: #f1e7d6; border-color: #e0bf81; }
.status-card--in-progress strong { color: #946013; }
.status-card--resolved { background: #e4eddc; border-color: #b7d18e; }
.status-card--resolved strong { color: #3c8f2c; }
.status-card--neutral { background: #ffffff; border-color: #d1d5db; }
.status-card--neutral strong { color: #111827; }

.penalty-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.filters-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.filters-left { display: flex; gap: 24px; flex-wrap: wrap; align-items: center; }
.filter-group { display: flex; gap: 4px; flex-wrap: wrap; }
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
.filters-right { display: flex; align-items: center; gap: 10px; margin-left: auto; }
.sort-trigger { width: 10rem; }

.delete-all-btn { background-color: #fde8e8; color: #c62828; border: none; box-shadow: none; }
.delete-all-btn:hover { background-color: #fad4d4; }

.list-section { display: flex; flex-direction: column; gap: 10px; }
.list-wrap { display: flex; flex-direction: column; gap: 10px; }

.state-line {
  border-radius: var(--radius-md);
  border: 1px solid hsl(var(--border));
  background: hsl(var(--card));
  padding: 12px;
  color: var(--text-secondary);
}

.empty-state {
  position: relative; display: flex; min-height: 260px;
  flex-direction: column; align-items: center; justify-content: center;
  overflow: hidden; border-radius: 1rem;
  border: 2px dashed hsl(var(--muted-foreground) / 0.2);
  background: linear-gradient(to bottom right, hsl(var(--muted) / 0.4), hsl(var(--muted) / 0.2), hsl(var(--background)));
  padding: 2rem;
}
.empty-state-bg {
  position: absolute; inset: 0;
  background: radial-gradient(ellipse at center, hsl(var(--muted)) 0%, transparent 70%);
  opacity: 0.5;
}
.empty-state-inner { position: relative; display: flex; flex-direction: column; align-items: center; gap: 1rem; text-align: center; }
.empty-state-icon {
  display: flex; height: 5rem; width: 5rem; align-items: center; justify-content: center;
  border-radius: 1rem; background-color: hsl(var(--primary) / 0.1);
  box-shadow: 0 0 0 4px hsl(var(--primary) / 0.05);
}
.empty-state-icon :deep(svg) { width: 2.5rem; height: 2.5rem; color: hsl(var(--primary) / 0.7); }
.empty-state-text h3 { font-size: 1.125rem; font-weight: 600; letter-spacing: -0.01em; }
.empty-state-text p { max-width: 24rem; font-size: 0.875rem; color: hsl(var(--muted-foreground)); margin-top: 0.25rem; }

@media (max-width: 1100px) {
  .cards-section { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .penalty-section { grid-template-columns: 1fr; }
}

@media (max-width: 760px) {
  .header-row { flex-direction: column; }
  .create-actions { width: 100%; }
  .cards-section { grid-template-columns: 1fr; }
  .filters-row { align-items: stretch; }
  .filters-left { flex-direction: column; }
  .filters-right { width: 100%; justify-content: flex-end; }
}
</style>
