<script setup lang="ts">
import axios from 'axios'
import { ClipboardCheck } from 'lucide-vue-next'
import { computed, ref } from 'vue'
import { toast } from 'vue-sonner'
import AppLayout from '@/components/layout/AppLayout.vue'
import Button from '@/components/ui/button/Button.vue'
import { Separator } from '@/components/ui/separator'
import { SidebarTrigger } from '@/components/ui/sidebar'
import ChecklistCard from '@/components/checklists/ChecklistCard.vue'
import ChecklistFormDialog from '@/components/checklists/ChecklistFormDialog.vue'
import ChecklistItemFormDialog from '@/components/checklists/ChecklistItemFormDialog.vue'
import {
  useChecklistsQuery,
  useCreateChecklistItemMutation,
  useCreateChecklistMutation,
  useDeleteChecklistItemMutation,
  useDeleteChecklistMutation,
  useSetChecklistCompletionMutation,
  useUpdateChecklistItemMutation,
  useUpdateChecklistMutation,
} from '@/composables/useChecklists'
import { useAuthStore } from '@/stores/auth'
import type {
  Checklist,
  ChecklistFrequency,
  ChecklistItem,
  CreateChecklistItemRequest,
  CreateChecklistRequest,
  UpdateChecklistItemRequest,
  UpdateChecklistRequest,
} from '@/types/checklist'

type FrequencyFilter = 'ALL' | ChecklistFrequency

const auth = useAuthStore()

const checklistQuery = useChecklistsQuery()
const createChecklist = useCreateChecklistMutation()
const updateChecklist = useUpdateChecklistMutation()
const deleteChecklist = useDeleteChecklistMutation()
const createItem = useCreateChecklistItemMutation()
const updateItem = useUpdateChecklistItemMutation()
const deleteItem = useDeleteChecklistItemMutation()
const setChecklistCompletion = useSetChecklistCompletionMutation()

const activeFilter = ref<FrequencyFilter>('ALL')

const checklistDialogOpen = ref(false)
const checklistDialogMode = ref<'create' | 'edit'>('create')
const activeChecklist = ref<Checklist | null>(null)

const itemDialogOpen = ref(false)
const itemDialogMode = ref<'create' | 'edit'>('create')
const activeItemChecklist = ref<Checklist | null>(null)
const activeItem = ref<ChecklistItem | null>(null)

const canManage = computed(() => auth.role === 'ADMIN' || auth.role === 'MANAGER')
const canComplete = computed(() => !!auth.role)

const checklists = computed(() => checklistQuery.data.value ?? [])

const frequencyOrder: ChecklistFrequency[] = ['DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY']

const filters: Array<{ label: string; value: FrequencyFilter }> = [
  { label: 'Alle', value: 'ALL' },
  { label: 'Daglig', value: 'DAILY' },
  { label: 'Ukentlig', value: 'WEEKLY' },
  { label: 'Månedlig', value: 'MONTHLY' },
  { label: 'Årlig', value: 'YEARLY' },
]

const groupedChecklists = computed(() => {
  const source =
    activeFilter.value === 'ALL'
      ? checklists.value
      : checklists.value.filter((entry) => entry.frequency === activeFilter.value)

  return frequencyOrder
    .map((frequency) => {
      const items = source.filter((entry) => entry.frequency === frequency)
      return {
        frequency,
        label: sectionHeadingLabel(frequency),
        items,
      }
    })
    .filter((section) => section.items.length > 0)
})

function sectionHeadingLabel(frequency: ChecklistFrequency): string {
  const now = new Date()
  const dateLabel = new Intl.DateTimeFormat('nb-NO', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  }).format(now)
  const monthLabel = new Intl.DateTimeFormat('nb-NO', {
    month: 'long',
    year: 'numeric',
  }).format(now)

  switch (frequency) {
    case 'DAILY':
      return `Daglige sjekklister - ${dateLabel}`
    case 'WEEKLY':
      return `Ukentlige sjekklister - uke ${getIsoWeek(now)}`
    case 'MONTHLY':
      return `Månedlige sjekklister - ${monthLabel}`
    case 'YEARLY':
      return `Årlige sjekklister - ${now.getFullYear()}`
    default:
      return 'Sjekklister'
  }
}

function getIsoWeek(date: Date): number {
  const target = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()))
  const day = target.getUTCDay() || 7
  target.setUTCDate(target.getUTCDate() + 4 - day)
  const yearStart = new Date(Date.UTC(target.getUTCFullYear(), 0, 1))
  return Math.ceil((((target.getTime() - yearStart.getTime()) / 86400000) + 1) / 7)
}

function openCreateChecklistDialog() {
  checklistDialogMode.value = 'create'
  activeChecklist.value = null
  checklistDialogOpen.value = true
}

function openEditChecklistDialog(checklist: Checklist) {
  checklistDialogMode.value = 'edit'
  activeChecklist.value = checklist
  checklistDialogOpen.value = true
}

async function handleCreateChecklist(payload: CreateChecklistRequest) {
  try {
    await createChecklist.mutateAsync(payload)
    checklistDialogOpen.value = false
    toast.success('Sjekkliste opprettet')
  } catch (error) {
    handleMutationError(error, 'Kunne ikke opprette sjekkliste')
  }
}

async function handleUpdateChecklist(payload: { checklistId: number; data: UpdateChecklistRequest }) {
  try {
    await updateChecklist.mutateAsync({
      checklistId: payload.checklistId,
      payload: payload.data,
    })
    checklistDialogOpen.value = false
    toast.success('Sjekkliste oppdatert')
  } catch (error) {
    handleMutationError(error, 'Kunne ikke oppdatere sjekkliste')
  }
}

async function handleDeleteChecklist(checklistId: number) {
  try {
    await deleteChecklist.mutateAsync(checklistId)
    toast.success('Sjekkliste slettet')
  } catch (error) {
    handleMutationError(error, 'Kunne ikke slette sjekkliste')
  }
}

function openCreateItemDialog(checklist: Checklist) {
  itemDialogMode.value = 'create'
  activeItemChecklist.value = checklist
  activeItem.value = null
  itemDialogOpen.value = true
}

function openEditItemDialog(payload: { checklist: Checklist; item: ChecklistItem }) {
  itemDialogMode.value = 'edit'
  activeItemChecklist.value = payload.checklist
  activeItem.value = payload.item
  itemDialogOpen.value = true
}

async function handleCreateItem(payload: { checklistId: number; data: CreateChecklistItemRequest }) {
  try {
    await createItem.mutateAsync({
      checklistId: payload.checklistId,
      payload: payload.data,
    })
    itemDialogOpen.value = false
    toast.success('Oppgave lagt til')
  } catch (error) {
    handleMutationError(error, 'Kunne ikke legge til oppgave')
  }
}

async function handleUpdateItem(payload: {
  checklistId: number
  itemId: number
  data: UpdateChecklistItemRequest
}) {
  try {
    await updateItem.mutateAsync({
      checklistId: payload.checklistId,
      itemId: payload.itemId,
      payload: payload.data,
    })
    itemDialogOpen.value = false
    toast.success('Oppgave oppdatert')
  } catch (error) {
    handleMutationError(error, 'Kunne ikke oppdatere oppgave')
  }
}

async function handleDeleteItem(payload: { checklistId: number; itemId: number }) {
  try {
    await deleteItem.mutateAsync(payload)
    toast.success('Oppgave slettet')
  } catch (error) {
    handleMutationError(error, 'Kunne ikke slette oppgave')
  }
}

async function handleToggleItemCompleted(payload: { checklistId: number; itemId: number; completed: boolean }) {
  try {
    await updateItem.mutateAsync({
      checklistId: payload.checklistId,
      itemId: payload.itemId,
      payload: {
        completed: payload.completed,
      },
    })
  } catch (error) {
    handleMutationError(error, 'Kunne ikke oppdatere oppgavestatus')
  }
}

async function handleToggleChecklistCompleted(payload: { checklistId: number; completed: boolean }) {
  try {
    await setChecklistCompletion.mutateAsync(payload)
  } catch (error) {
    handleMutationError(error, 'Kunne ikke oppdatere sjekklistestatus')
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
  <AppLayout active-menu-item="Sjekklister">
    <header class="page-header">
      <div class="page-header-inner">
        <SidebarTrigger />
        <Separator orientation="vertical" class="header-separator" />
        <span class="page-title">Sjekklister</span>
      </div>
    </header>

    <div class="page-content">
      <section class="header-row">
        <div>
          <h1>Sjekklister</h1>
          <p>Dynamiske rutiner for daglig drift og internkontroll.</p>
        </div>

        <Button v-if="canManage" @click="openCreateChecklistDialog">
          + Ny sjekkliste
        </Button>
      </section>

      <section class="filters-row" aria-label="Filtrer frekvens">
        <button
          v-for="filter in filters"
          :key="filter.value"
          class="filter-button"
          :class="{ 'filter-button--active': activeFilter === filter.value }"
          type="button"
          @click="activeFilter = filter.value"
        >
          {{ filter.label }}
        </button>
      </section>

      <section class="list-section">
        <p v-if="checklistQuery.isLoading.value" class="state-line">Laster sjekklister...</p>
        <p v-else-if="checklistQuery.isError.value" class="state-line state-line--danger">
          Kunne ikke hente sjekklister.
        </p>

        <div v-else-if="groupedChecklists.length === 0" class="empty-state">
          <div class="empty-state-bg" />
          <div class="empty-state-inner">
            <div class="empty-state-icon">
              <ClipboardCheck :stroke-width="1.5" />
            </div>
            <div class="empty-state-text">
              <h3>Ingen sjekklister ennå</h3>
              <p>Det finnes ingen sjekklister i valgt frekvens. Opprett en ny sjekkliste for å komme i gang.</p>
            </div>
            <Button v-if="canManage" @click="openCreateChecklistDialog">
              + Ny sjekkliste
            </Button>
          </div>
        </div>

        <div v-else class="sections-wrap">
          <section v-for="section in groupedChecklists" :key="section.frequency" class="frequency-section">
            <div class="section-divider">
              <span>{{ section.label }}</span>
            </div>

            <div class="checklist-grid">
              <ChecklistCard
                v-for="checklist in section.items"
                :key="checklist.id"
                :checklist="checklist"
                :can-manage="canManage"
                :can-complete="canComplete"
                @edit-checklist="openEditChecklistDialog"
                @delete-checklist="handleDeleteChecklist"
                @new-item="openCreateItemDialog"
                @edit-item="openEditItemDialog"
                @delete-item="handleDeleteItem"
                @toggle-item-completed="handleToggleItemCompleted"
                @toggle-checklist-completed="handleToggleChecklistCompleted"
              />
            </div>
          </section>
        </div>
      </section>
    </div>

    <ChecklistFormDialog
      v-model:open="checklistDialogOpen"
      :mode="checklistDialogMode"
      :initial-checklist="activeChecklist"
      :submitting="createChecklist.isPending.value || updateChecklist.isPending.value"
      @create="handleCreateChecklist"
      @update="handleUpdateChecklist"
    />

    <ChecklistItemFormDialog
      v-model:open="itemDialogOpen"
      :mode="itemDialogMode"
      :checklist="activeItemChecklist"
      :initial-item="activeItem"
      :submitting="createItem.isPending.value || updateItem.isPending.value"
      @create="handleCreateItem"
      @update="handleUpdateItem"
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
}

.filters-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
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

.state-line {
  padding: 14px;
  border-radius: var(--radius-md);
  background: #ececea;
  color: #4b5056;
}

.state-line--danger {
  background: #f6e5e5;
  color: #913333;
}

.empty-state {
  position: relative;
  display: flex;
  min-height: 320px;
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

.checklist-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.sections-wrap {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.frequency-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.section-divider {
  padding-top: 2px;
}

.section-divider span {
  text-transform: uppercase;
  letter-spacing: 0.06em;
  font-size: 0.96rem;
  color: #474c53;
  font-weight: 700;
}

@media (max-width: 760px) {
  .header-row {
    flex-direction: column;
  }

  h1 {
    font-size: 2rem;
  }
}
</style>
