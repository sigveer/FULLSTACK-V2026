<script setup lang="ts">
import { computed, ref } from 'vue'
import { MoreVertical, CheckCircle2, Pencil, Trash2 } from 'lucide-vue-next'
import type { Checklist } from '@/types/checklist'
import StatusPill from '@/components/ui/StatusPill.vue'
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
} from '@/components/ui/dropdown-menu'
import AlertDialog from '@/components/ui/alert-dialog/AlertDialog.vue'
import AlertDialogAction from '@/components/ui/alert-dialog/AlertDialogAction.vue'
import AlertDialogCancel from '@/components/ui/alert-dialog/AlertDialogCancel.vue'
import AlertDialogContent from '@/components/ui/alert-dialog/AlertDialogContent.vue'
import AlertDialogDescription from '@/components/ui/alert-dialog/AlertDialogDescription.vue'
import AlertDialogFooter from '@/components/ui/alert-dialog/AlertDialogFooter.vue'
import AlertDialogHeader from '@/components/ui/alert-dialog/AlertDialogHeader.vue'
import AlertDialogTitle from '@/components/ui/alert-dialog/AlertDialogTitle.vue'

const props = defineProps<{
  checklist: Checklist
  canManage: boolean
  canComplete: boolean
}>()

const emits = defineEmits<{
  (e: 'open', checklist: Checklist): void
  (e: 'edit-checklist', checklist: Checklist): void
  (e: 'delete-checklist', checklistId: number): void
  (e: 'toggle-checklist-completed', payload: { checklistId: number; completed: boolean }): void
}>()

const deleteDialogOpen = ref(false)

const frequencyLabel = computed(() => {
  switch (props.checklist.frequency) {
    case 'DAILY': return 'Daglig'
    case 'WEEKLY': return 'Ukentlig'
    case 'MONTHLY': return 'Månedlig'
    case 'YEARLY': return 'Årlig'
    default: return props.checklist.frequency
  }
})

const frequencyTone = computed(() => {
  switch (props.checklist.frequency) {
    case 'DAILY': return 'brand'
    case 'WEEKLY': return 'ok'
    case 'MONTHLY': return 'warning'
    case 'YEARLY': return 'neutral'
    default: return 'neutral'
  }
})

const completionPercent = computed(() => {
  if (props.checklist.itemCount === 0) return 0
  return Math.round((props.checklist.completedItemCount / props.checklist.itemCount) * 100)
})

const statusLabel = computed(() => {
  switch (props.checklist.status) {
    case 'COMPLETED': return 'Fullført'
    case 'IN_PROGRESS': return 'Pågår'
    default: return 'Ikke startet'
  }
})

const statusTone = computed(() => {
  switch (props.checklist.status) {
    case 'COMPLETED': return 'ok'
    case 'IN_PROGRESS': return 'warning'
    default: return 'neutral'
  }
})

const checklistComplete = computed(() =>
  props.checklist.itemCount > 0 && props.checklist.completedItemCount === props.checklist.itemCount,
)
</script>

<template>
  <div class="checklist-card">
    <div class="card-top">
      <button type="button" class="card-body" @click="emits('open', checklist)">
        <div class="tag-row">
          <StatusPill :label="frequencyLabel" :tone="frequencyTone" />
          <StatusPill :label="statusLabel" :tone="statusTone" />
        </div>

        <h3>{{ checklist.name }}</h3>

        <div class="detail-row">
          <p v-if="checklist.description" class="description">{{ checklist.description }}</p>
          <span class="completion-label">{{ checklist.completedItemCount }}/{{ checklist.itemCount }} fullført</span>
        </div>

        <div class="progress-track" role="progressbar" :aria-valuenow="completionPercent" aria-valuemin="0" aria-valuemax="100">
          <div class="progress-fill" :class="`progress-fill--${statusTone}`" :style="{ width: `${completionPercent}%` }" />
        </div>
      </button>

      <div v-if="canManage || canComplete" class="card-actions" @click.stop>
        <DropdownMenu>
          <DropdownMenuTrigger as-child>
            <button type="button" class="actions-trigger">
              <MoreVertical :size="18" />
            </button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" :side-offset="4">
            <DropdownMenuItem
              v-if="canComplete && checklist.itemCount > 0"
              @click="emits('toggle-checklist-completed', { checklistId: checklist.id, completed: !checklistComplete })"
            >
              <CheckCircle2 :size="16" class="menu-icon--green" />
              {{ checklistComplete ? 'Angre fullføring' : 'Fullfør hele' }}
            </DropdownMenuItem>
            <DropdownMenuSeparator v-if="canComplete && canManage && checklist.itemCount > 0" />
            <DropdownMenuItem v-if="canManage" @click="emits('edit-checklist', checklist)">
              <Pencil :size="16" />
              Rediger
            </DropdownMenuItem>
            <DropdownMenuItem v-if="canManage" class="menu-item--danger" @click="deleteDialogOpen = true">
              <Trash2 :size="16" />
              Slett sjekkliste
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>

        <AlertDialog v-model:open="deleteDialogOpen">
          <AlertDialogContent>
            <AlertDialogHeader>
              <AlertDialogTitle>Slett sjekkliste?</AlertDialogTitle>
              <AlertDialogDescription>
                Dette fjerner sjekklisten og alle oppgaver permanent.
              </AlertDialogDescription>
            </AlertDialogHeader>
            <AlertDialogFooter>
              <AlertDialogCancel>Avbryt</AlertDialogCancel>
              <AlertDialogAction variant="destructive" @click="emits('delete-checklist', checklist.id)">
                Slett
              </AlertDialogAction>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialog>
      </div>
    </div>
  </div>
</template>

<style scoped>
.checklist-card {
  position: relative;
  width: 100%;
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  background: var(--card-bg);
  display: flex;
  flex-direction: column;
  transition: box-shadow 150ms ease;
}

.checklist-card:hover {
  box-shadow: 0 6px 14px rgb(0 0 0 / 0.08);
}

.card-top {
  display: flex;
  align-items: flex-start;
}

.card-body {
  flex: 1;
  min-width: 0;
  padding: 14px 16px 14px 22px;
  text-align: left;
  cursor: pointer;
  background: none;
  border: none;
  font: inherit;
  color: inherit;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.card-body:focus-visible {
  outline: none;
  box-shadow: inset 0 0 0 2px hsl(var(--ring) / 0.3);
  border-radius: var(--radius-lg);
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

h3 {
  margin: 2px 0 0;
  font-size: 1.5rem;
  letter-spacing: -0.02em;
}

.detail-row {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-top: 2px;
}

.description {
  color: var(--text-primary);
  margin: 0;
  font-size: 1.05rem;
  line-height: 1.35;
  flex: 1;
  min-width: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.completion-label {
  color: var(--text-secondary);
  font-size: 0.85rem;
  flex-shrink: 0;
  white-space: nowrap;
  margin-left: auto;
}

.progress-track {
  margin-top: 4px;
  height: 5px;
  border-radius: var(--radius-pill);
  background: #d8d8d5;
  overflow: hidden;
  width: 100%;
}

.progress-fill {
  height: 100%;
  border-radius: var(--radius-pill);
}

.progress-fill--ok { background: var(--green); }
.progress-fill--warning { background: var(--amber); }
.progress-fill--neutral { background: #9a9a96; }

.card-actions {
  display: flex;
  align-items: flex-start;
  padding: 12px 12px 0 0;
  flex-shrink: 0;
}

.actions-trigger {
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

.actions-trigger:hover {
  background: hsl(var(--accent, 250 40% 95%));
  color: hsl(var(--foreground));
}

.menu-icon--green { color: #3c8f2c; }

.menu-item--danger { color: #c62828; }
.menu-item--danger:hover {
  background-color: #fde8e8 !important;
  color: #c62828 !important;
}
</style>
