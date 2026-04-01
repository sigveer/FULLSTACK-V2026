<script setup lang="ts">
import axios from 'axios'
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, ClipboardCheck, MoreVertical, Pencil, Trash2 } from 'lucide-vue-next'
import { toast } from 'vue-sonner'
import AppLayout from '@/components/layout/AppLayout.vue'
import Button from '@/components/ui/button/Button.vue'
import Checkbox from '@/components/ui/checkbox/Checkbox.vue'
import StatusPill from '@/components/ui/StatusPill.vue'
import { Separator } from '@/components/ui/separator'
import { SidebarTrigger } from '@/components/ui/sidebar'
import Tooltip from '@/components/ui/tooltip/Tooltip.vue'
import TooltipTrigger from '@/components/ui/tooltip/TooltipTrigger.vue'
import TooltipContent from '@/components/ui/tooltip/TooltipContent.vue'
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
} from '@/components/ui/dropdown-menu'
import AlertDialog from '@/components/ui/alert-dialog/AlertDialog.vue'
import AlertDialogAction from '@/components/ui/alert-dialog/AlertDialogAction.vue'
import AlertDialogCancel from '@/components/ui/alert-dialog/AlertDialogCancel.vue'
import AlertDialogContent from '@/components/ui/alert-dialog/AlertDialogContent.vue'
import AlertDialogDescription from '@/components/ui/alert-dialog/AlertDialogDescription.vue'
import AlertDialogFooter from '@/components/ui/alert-dialog/AlertDialogFooter.vue'
import AlertDialogHeader from '@/components/ui/alert-dialog/AlertDialogHeader.vue'
import AlertDialogTitle from '@/components/ui/alert-dialog/AlertDialogTitle.vue'
import ChecklistFormDialog from '@/components/checklists/ChecklistFormDialog.vue'
import ChecklistItemFormDialog from '@/components/checklists/ChecklistItemFormDialog.vue'
import { useAuthStore } from '@/stores/auth'
import {
  useChecklistsQuery,
  useCreateChecklistItemMutation,
  useDeleteChecklistItemMutation,
  useDeleteChecklistMutation,
  useSetChecklistCompletionMutation,
  useUpdateChecklistItemMutation,
  useUpdateChecklistMutation,
} from '@/composables/useChecklists'
import type {
  ChecklistFrequency,
  ChecklistItem,
  CreateChecklistItemRequest,
  UpdateChecklistItemRequest,
  UpdateChecklistRequest,
} from '@/types/checklist'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const checklistsQuery = useChecklistsQuery()
const updateItem = useUpdateChecklistItemMutation()
const createItem = useCreateChecklistItemMutation()
const deleteItem = useDeleteChecklistItemMutation()
const updateChecklist = useUpdateChecklistMutation()
const deleteChecklist = useDeleteChecklistMutation()
const setChecklistCompletion = useSetChecklistCompletionMutation()

const canManage = computed(() => auth.role === 'ADMIN' || auth.role === 'MANAGER')
const canComplete = computed(() => !!auth.role)

const checklistId = computed(() => Number(route.params.id))
const checklist = computed(() =>
  (checklistsQuery.data.value ?? []).find((c) => c.id === checklistId.value) ?? null,
)

const itemDialogOpen = ref(false)
const itemDialogMode = ref<'create' | 'edit'>('create')
const activeItem = ref<ChecklistItem | null>(null)
const deleteItemDialogId = ref<number | null>(null)
const editChecklistDialogOpen = ref(false)
const deleteChecklistDialogOpen = ref(false)

const frequencyLabel: Record<ChecklistFrequency, string> = {
  DAILY: 'Daglig',
  WEEKLY: 'Ukentlig',
  MONTHLY: 'Månedlig',
  YEARLY: 'Årlig',
}

const frequencyTone: Record<ChecklistFrequency, 'brand' | 'ok' | 'warning' | 'neutral'> = {
  DAILY: 'brand',
  WEEKLY: 'ok',
  MONTHLY: 'warning',
  YEARLY: 'neutral',
}

const completionPercent = computed(() => {
  if (!checklist.value || checklist.value.itemCount === 0) return 0
  return Math.round((checklist.value.completedItemCount / checklist.value.itemCount) * 100)
})

const statusTone = computed(() => {
  if (!checklist.value) return 'neutral'
  switch (checklist.value.status) {
    case 'COMPLETED': return 'ok'
    case 'IN_PROGRESS': return 'warning'
    default: return 'neutral'
  }
})

const checklistComplete = computed(() =>
  !!checklist.value && checklist.value.itemCount > 0 && checklist.value.completedItemCount === checklist.value.itemCount,
)

const dateLabel = computed(() => {
  const now = new Date()
  return new Intl.DateTimeFormat('nb-NO', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  }).format(now)
})

function formatTime(value: string | null): string {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return ''
  return date.toLocaleTimeString('nb-NO', { hour: '2-digit', minute: '2-digit' })
}

function openCreateItemDialog() {
  itemDialogMode.value = 'create'
  activeItem.value = null
  itemDialogOpen.value = true
}

function openEditItemDialog(item: ChecklistItem) {
  itemDialogMode.value = 'edit'
  activeItem.value = item
  itemDialogOpen.value = true
}

async function handleToggleItem(item: ChecklistItem) {
  if (!checklist.value) return
  try {
    await updateItem.mutateAsync({
      checklistId: checklistId.value,
      itemId: item.id,
      payload: { completed: !item.completed },
    })
  } catch (error) {
    handleMutationError(error, 'Kunne ikke oppdatere oppgavestatus')
  }
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

async function handleDeleteItem(itemId: number) {
  try {
    await deleteItem.mutateAsync({ checklistId: checklistId.value, itemId })
    deleteItemDialogId.value = null
    toast.success('Oppgave slettet')
  } catch (error) {
    handleMutationError(error, 'Kunne ikke slette oppgave')
  }
}

async function handleUpdateChecklist(payload: { checklistId: number; data: UpdateChecklistRequest }) {
  try {
    await updateChecklist.mutateAsync({
      checklistId: payload.checklistId,
      payload: payload.data,
    })
    editChecklistDialogOpen.value = false
    toast.success('Sjekkliste oppdatert')
  } catch (error) {
    handleMutationError(error, 'Kunne ikke oppdatere sjekkliste')
  }
}

async function handleDeleteChecklist() {
  if (!checklist.value) return
  try {
    await deleteChecklist.mutateAsync(checklist.value.id)
    toast.success('Sjekkliste slettet')
    router.push('/sjekklister')
  } catch (error) {
    handleMutationError(error, 'Kunne ikke slette sjekkliste')
  }
}

async function handleCompleteAll() {
  if (!checklist.value) return
  try {
    await setChecklistCompletion.mutateAsync({
      checklistId: checklistId.value,
      completed: !checklistComplete.value,
    })
    toast.success(checklistComplete.value ? 'Fullføring angret' : 'Sjekkliste fullført')
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
      <button class="back-button" @click="router.push('/sjekklister')">
        <ArrowLeft :size="18" />
        <span>Tilbake til sjekklister</span>
      </button>

      <p v-if="checklistsQuery.isLoading.value" class="state-line">Laster sjekkliste...</p>

      <div v-else-if="!checklist" class="empty-state">
        <div class="empty-state-bg" />
        <div class="empty-state-inner">
          <div class="empty-state-icon">
            <ClipboardCheck :stroke-width="1.5" />
          </div>
          <div class="empty-state-text">
            <h3>Sjekkliste ikke funnet</h3>
            <p>Sjekklisten du leter etter finnes ikke eller har blitt slettet.</p>
          </div>
          <Button @click="router.push('/sjekklister')">Tilbake til sjekklister</Button>
        </div>
      </div>

      <template v-else>
        <section class="detail-header">
          <div>
            <div class="tag-row">
              <StatusPill :label="frequencyLabel[checklist.frequency]" :tone="frequencyTone[checklist.frequency]" />
              <StatusPill
                :label="checklist.status === 'COMPLETED' ? 'Fullført' : checklist.status === 'IN_PROGRESS' ? 'Pågår' : 'Ikke startet'"
                :tone="statusTone"
              />
            </div>
            <h1>{{ checklist.name }}</h1>
            <p v-if="checklist.description" class="detail-description">{{ checklist.description }}</p>
            <p class="date-label">{{ dateLabel }}</p>
          </div>

          <Button v-if="canManage" @click="openCreateItemDialog">
            + Legg til oppgave
          </Button>
        </section>

        <section class="items-section">
          <div v-if="checklist.items.length === 0" class="empty-state">
            <div class="empty-state-bg" />
            <div class="empty-state-inner">
              <div class="empty-state-icon">
                <ClipboardCheck :stroke-width="1.5" />
              </div>
              <div class="empty-state-text">
                <h3>Ingen oppgaver ennå</h3>
                <p>Legg til en oppgave for å komme i gang.</p>
              </div>
              <Button v-if="canManage" @click="openCreateItemDialog">
                + Legg til oppgave
              </Button>
            </div>
          </div>

          <div v-else class="item-list">
            <Tooltip v-for="item in checklist.items" :key="item.id">
              <TooltipTrigger>
                <div class="item-row">
                  <div class="item-left">
                    <Checkbox
                      v-if="canComplete"
                      :checked="item.completed"
                      @update:checked="() => handleToggleItem(item)"
                    />
                    <span class="item-title" :class="{ 'item-title--done': item.completed }">
                      {{ item.title }}
                    </span>
                  </div>

                  <div class="item-right">
                    <span v-if="item.completed && item.completedAt" class="item-time">
                      {{ formatTime(item.completedAt) }}
                    </span>

                    <div v-if="canManage" class="item-actions" @click.stop>
                      <DropdownMenu>
                        <DropdownMenuTrigger as-child>
                          <button type="button" class="item-action-btn">
                            <MoreVertical :size="16" />
                          </button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end" :side-offset="4">
                          <DropdownMenuItem @click="openEditItemDialog(item)">
                            <Pencil :size="16" />
                            Rediger
                          </DropdownMenuItem>
                          <DropdownMenuItem class="menu-item--danger" @click="deleteItemDialogId = item.id">
                            <Trash2 :size="16" />
                            Slett oppgave
                          </DropdownMenuItem>
                        </DropdownMenuContent>
                      </DropdownMenu>

                      <AlertDialog :open="deleteItemDialogId === item.id" @update:open="(v) => { if (!v) deleteItemDialogId = null }">
                        <AlertDialogContent>
                          <AlertDialogHeader>
                            <AlertDialogTitle>Slett oppgave?</AlertDialogTitle>
                            <AlertDialogDescription>
                              Oppgaven kan ikke gjenopprettes etter sletting.
                            </AlertDialogDescription>
                          </AlertDialogHeader>
                          <AlertDialogFooter>
                            <AlertDialogCancel>Avbryt</AlertDialogCancel>
                            <AlertDialogAction variant="destructive" @click="handleDeleteItem(item.id)">
                              Slett
                            </AlertDialogAction>
                          </AlertDialogFooter>
                        </AlertDialogContent>
                      </AlertDialog>
                    </div>
                  </div>
                </div>
              </TooltipTrigger>
              <TooltipContent v-if="item.description" side="bottom" align="start">
                {{ item.description }}
              </TooltipContent>
            </Tooltip>
          </div>
        </section>

        <section v-if="checklist.items.length > 0" class="actions-section">
          <Button v-if="canComplete" class="complete-btn" @click="handleCompleteAll">
            {{ checklistComplete ? 'Angre fullføring' : `Fullfør sjekkliste (${checklist.completedItemCount}/${checklist.itemCount})` }}
          </Button>
          <Button variant="outline" @click="router.push('/avvik')">
            Rapporter avvik
          </Button>
        </section>

        <section class="progress-section">
          <div class="progress-track" role="progressbar" :aria-valuenow="completionPercent" aria-valuemin="0" aria-valuemax="100">
            <div class="progress-fill" :class="`progress-fill--${statusTone}`" :style="{ width: `${completionPercent}%` }" />
          </div>
          <div class="progress-stats">
            <div class="stat">
              <strong class="stat-number stat-number--green">{{ checklist.completedItemCount }}</strong>
              <span>fullført</span>
            </div>
            <div class="stat">
              <strong class="stat-number">{{ checklist.itemCount - checklist.completedItemCount }}</strong>
              <span>gjenstår</span>
            </div>
          </div>
        </section>

        <div v-if="canManage" class="detail-actions">
          <Button variant="secondary" @click="editChecklistDialogOpen = true">
            <Pencil />
            Rediger sjekkliste
          </Button>

          <Button variant="destructive" class="delete-btn" @click="deleteChecklistDialogOpen = true">
            <Trash2 />
            Slett sjekkliste
          </Button>

          <AlertDialog v-model:open="deleteChecklistDialogOpen">
            <AlertDialogContent>
              <AlertDialogHeader>
                <AlertDialogTitle>Slett sjekkliste?</AlertDialogTitle>
                <AlertDialogDescription>
                  Dette fjerner sjekklisten og alle oppgaver permanent.
                </AlertDialogDescription>
              </AlertDialogHeader>
              <AlertDialogFooter>
                <AlertDialogCancel>Avbryt</AlertDialogCancel>
                <AlertDialogAction variant="destructive" @click="handleDeleteChecklist">
                  Slett
                </AlertDialogAction>
              </AlertDialogFooter>
            </AlertDialogContent>
          </AlertDialog>
        </div>
      </template>
    </div>

    <ChecklistFormDialog
      v-model:open="editChecklistDialogOpen"
      mode="edit"
      :initial-checklist="checklist"
      :submitting="updateChecklist.isPending.value"
      @update="handleUpdateChecklist"
    />

    <ChecklistItemFormDialog
      v-model:open="itemDialogOpen"
      :mode="itemDialogMode"
      :checklist="checklist"
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
.page-content { display: flex; flex: 1; flex-direction: column; gap: 1.25rem; padding: 0 1rem 2rem; }

.back-button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: none;
  border: none;
  cursor: pointer;
  color: hsl(var(--muted-foreground));
  font-size: 0.9rem;
  padding: 4px 0;
  transition: color 150ms ease;
}
.back-button:hover { color: hsl(var(--foreground)); }

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

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 8px;
}

h1 {
  margin: 0;
  font-size: 2rem;
  letter-spacing: -0.02em;
}

.detail-description {
  margin-top: 6px;
  color: var(--text-secondary);
  font-size: 1.08rem;
}

.date-label {
  margin-top: 4px;
  color: var(--text-secondary);
  font-size: 0.95rem;
}

.items-section {
  display: flex;
  flex-direction: column;
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

.item-list {
  display: flex;
  flex-direction: column;
  border: 1px solid #d9d9d6;
  border-radius: var(--radius-md);
}

.item-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  gap: 12px;
  background: #fff;
  border-bottom: 1px solid #e0e0dd;
}

.item-list > :deep(:last-child) .item-row {
  border-bottom: none;
}

.item-list > :deep(:first-child) .item-row {
  border-radius: var(--radius-md) var(--radius-md) 0 0;
}

.item-list > :deep(:last-child) .item-row {
  border-radius: 0 0 var(--radius-md) var(--radius-md);
}

.item-left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.item-title {
  font-size: 1.05rem;
  font-weight: 500;
}

.item-title--done {
  text-decoration: line-through;
  color: #5b615f;
}

.item-right {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.item-time {
  color: var(--text-secondary);
  font-size: 0.85rem;
  white-space: nowrap;
}

.item-actions {
  display: flex;
  align-items: center;
}

.item-action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  border-radius: var(--radius-md);
  border: none;
  background: none;
  color: var(--text-secondary);
  cursor: pointer;
  transition: background 150ms ease, color 150ms ease;
}

.item-action-btn:hover {
  background: hsl(var(--accent, 250 40% 95%));
  color: hsl(var(--foreground));
}

.menu-item--danger { color: #c62828; }
.menu-item--danger:hover {
  background-color: #fde8e8 !important;
  color: #c62828 !important;
}

.actions-section {
  display: flex;
  gap: 10px;
}

.complete-btn {
  flex: 1;
}

.progress-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
  border: 1px solid #d9d9d6;
  border-radius: var(--radius-md);
  padding: 16px;
  background: #fff;
}

.progress-track {
  height: 6px;
  border-radius: var(--radius-pill);
  background: #d8d8d5;
  overflow: hidden;
  width: 100%;
}

.progress-fill {
  height: 100%;
  border-radius: var(--radius-pill);
  transition: width 400ms ease;
}

.progress-fill--ok { background: var(--green); }
.progress-fill--warning { background: var(--amber); }
.progress-fill--neutral { background: #9a9a96; }

.progress-stats {
  display: flex;
  gap: 24px;
}

.stat {
  display: flex;
  align-items: baseline;
  gap: 4px;
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.stat-number {
  font-size: 1.15rem;
}

.stat-number--green {
  color: #3c8f2c;
}

.detail-actions {
  display: flex;
  gap: 10px;
}

.delete-btn {
  background-color: #fde8e8;
  color: #c62828;
  border: none;
  box-shadow: none;
}
.delete-btn:hover { background-color: #fad4d4; }
.delete-btn:active { background-color: #f5c2c2; }

@media (max-width: 760px) {
  .detail-header {
    flex-direction: column;
  }

  h1 {
    font-size: 1.6rem;
  }

  .actions-section {
    flex-direction: column;
  }
}
</style>
