<script setup lang="ts">
import { computed } from 'vue'
import {
  Check,
  ChevronDown,
  Pencil,
  Plus,
  Trash2,
} from 'lucide-vue-next'
import type { Checklist, ChecklistItem } from '@/types/checklist'
import StatusPill from '@/components/ui/StatusPill.vue'
import Button from '@/components/ui/button/Button.vue'
import Checkbox from '@/components/ui/checkbox/Checkbox.vue'
import Collapsible from '@/components/ui/collapsible/Collapsible.vue'
import CollapsibleContent from '@/components/ui/collapsible/CollapsibleContent.vue'
import CollapsibleTrigger from '@/components/ui/collapsible/CollapsibleTrigger.vue'
import AlertDialog from '@/components/ui/alert-dialog/AlertDialog.vue'
import AlertDialogTrigger from '@/components/ui/alert-dialog/AlertDialogTrigger.vue'
import AlertDialogContent from '@/components/ui/alert-dialog/AlertDialogContent.vue'
import AlertDialogHeader from '@/components/ui/alert-dialog/AlertDialogHeader.vue'
import AlertDialogTitle from '@/components/ui/alert-dialog/AlertDialogTitle.vue'
import AlertDialogDescription from '@/components/ui/alert-dialog/AlertDialogDescription.vue'
import AlertDialogFooter from '@/components/ui/alert-dialog/AlertDialogFooter.vue'
import AlertDialogCancel from '@/components/ui/alert-dialog/AlertDialogCancel.vue'
import AlertDialogAction from '@/components/ui/alert-dialog/AlertDialogAction.vue'

const props = defineProps<{
  checklist: Checklist
  canManage: boolean
  canComplete: boolean
}>()

const emits = defineEmits<{
  (e: 'edit-checklist', checklist: Checklist): void
  (e: 'delete-checklist', checklistId: number): void
  (e: 'new-item', checklist: Checklist): void
  (e: 'edit-item', payload: { checklist: Checklist; item: ChecklistItem }): void
  (e: 'delete-item', payload: { checklistId: number; itemId: number }): void
  (e: 'toggle-item-completed', payload: { checklistId: number; itemId: number; completed: boolean }): void
  (e: 'toggle-checklist-completed', payload: { checklistId: number; completed: boolean }): void
}>()

const frequencyLabel = computed(() => {
  switch (props.checklist.frequency) {
    case 'DAILY':
      return 'Daglig'
    case 'WEEKLY':
      return 'Ukentlig'
    case 'MONTHLY':
      return 'Månedlig'
    case 'YEARLY':
      return 'Årlig'
    default:
      return props.checklist.frequency
  }
})

const frequencyTone = computed(() => {
  switch (props.checklist.frequency) {
    case 'DAILY':
      return 'brand'
    case 'WEEKLY':
      return 'ok'
    case 'MONTHLY':
      return 'warning'
    case 'YEARLY':
      return 'neutral'
    default:
      return 'neutral'
  }
})

const completionPercent = computed(() => {
  if (props.checklist.itemCount === 0) {
    return 0
  }
  return Math.round((props.checklist.completedItemCount / props.checklist.itemCount) * 100)
})

const statusLabel = computed(() => {
  switch (props.checklist.status) {
    case 'COMPLETED':
      return 'Fullført'
    case 'IN_PROGRESS':
      return 'Pågår'
    default:
      return 'Ikke startet'
  }
})

const statusTone = computed(() => {
  switch (props.checklist.status) {
    case 'COMPLETED':
      return 'ok'
    case 'IN_PROGRESS':
      return 'warning'
    default:
      return 'neutral'
  }
})

const checklistComplete = computed(() =>
  props.checklist.itemCount > 0 && props.checklist.completedItemCount === props.checklist.itemCount,
)
</script>

<template>
  <Collapsible class="checklist-card" :default-open="false">
    <div class="card-header">
      <div class="progress-circle" :class="{ 'progress-circle--done': checklistComplete }">
        <Check v-if="checklistComplete" />
        <span v-else>{{ checklist.completedItemCount }}/{{ checklist.itemCount }}</span>
      </div>

      <div>
        <h3>{{ checklist.name }}</h3>
        <p v-if="checklist.description" class="description">{{ checklist.description }}</p>
        <div class="meta-row">
          <StatusPill :label="frequencyLabel" :tone="frequencyTone" />
          <StatusPill :label="statusLabel" :tone="statusTone" />
          <span>{{ checklist.completedItemCount }}/{{ checklist.itemCount }} punkter fullført</span>
        </div>
        <div class="progress-track" role="progressbar" :aria-valuenow="completionPercent" aria-valuemin="0" aria-valuemax="100">
          <div class="progress-fill" :class="`progress-fill--${statusTone}`" :style="{ width: `${completionPercent}%` }" />
        </div>
      </div>

      <div class="header-actions">
        <label v-if="canComplete && checklist.itemCount > 0" class="complete-all-toggle">
          <Checkbox
            :checked="checklistComplete"
            @update:checked="(value) => emits('toggle-checklist-completed', { checklistId: checklist.id, completed: value })"
          />
          <span>Fullfør hele</span>
        </label>

        <Button v-if="canManage" variant="ghost" size="icon-sm" @click="emits('edit-checklist', checklist)">
          <Pencil />
        </Button>

        <AlertDialog v-if="canManage">
          <AlertDialogTrigger>
            <Button variant="ghost" size="icon-sm" class="danger-action">
              <Trash2 />
            </Button>
          </AlertDialogTrigger>
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

        <CollapsibleTrigger as-child>
          <Button variant="outline" size="icon-sm" class="toggle-button">
            <ChevronDown />
          </Button>
        </CollapsibleTrigger>
      </div>
    </div>

    <CollapsibleContent>
      <div class="content-wrap">
        <div v-if="checklist.items.length === 0" class="empty-state">
          Ingen oppgaver ennå.
        </div>

        <ul v-else class="item-list">
          <li v-for="item in checklist.items" :key="item.id" class="item-row">
            <div class="item-main">
              <Checkbox
                v-if="canComplete"
                :checked="item.completed"
                @update:checked="(value) => emits('toggle-item-completed', { checklistId: checklist.id, itemId: item.id, completed: value })"
              />

              <div>
              <p class="item-title" :class="{ 'item-title--done': item.completed }">{{ item.title }}</p>
              <p v-if="item.description" class="item-description">{{ item.description }}</p>
              </div>
            </div>

            <div v-if="canManage" class="item-actions">
              <Button
                variant="ghost"
                size="icon-sm"
                @click="emits('edit-item', { checklist, item })"
              >
                <Pencil />
              </Button>

              <AlertDialog>
                <AlertDialogTrigger>
                  <Button variant="ghost" size="icon-sm" class="danger-action">
                    <Trash2 />
                  </Button>
                </AlertDialogTrigger>
                <AlertDialogContent>
                  <AlertDialogHeader>
                    <AlertDialogTitle>Slett oppgave?</AlertDialogTitle>
                    <AlertDialogDescription>
                      Oppgaven kan ikke gjenopprettes etter sletting.
                    </AlertDialogDescription>
                  </AlertDialogHeader>
                  <AlertDialogFooter>
                    <AlertDialogCancel>Avbryt</AlertDialogCancel>
                    <AlertDialogAction
                      variant="destructive"
                      @click="emits('delete-item', { checklistId: checklist.id, itemId: item.id })"
                    >
                      Slett
                    </AlertDialogAction>
                  </AlertDialogFooter>
                </AlertDialogContent>
              </AlertDialog>
            </div>
          </li>
        </ul>

        <Button v-if="canManage" variant="secondary" @click="emits('new-item', checklist)">
          <Plus /> Legg til oppgave
        </Button>
      </div>
    </CollapsibleContent>
  </Collapsible>
</template>

<style scoped>
.checklist-card {
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  background: var(--card-bg);
  padding: 14px 16px;
}

.card-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.progress-circle {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: #ece8de;
  color: #806b2f;
  font-weight: 700;
  display: grid;
  place-items: center;
  flex-shrink: 0;
}

.progress-circle--done {
  background: #dcebd8;
  color: #2f6f34;
}

.progress-circle :deep(svg) {
  width: 22px;
  height: 22px;
}

.card-header h3 {
  margin: 0;
  font-size: 1.3rem;
}

.description {
  margin-top: 4px;
  color: var(--text-secondary);
  font-size: 0.96rem;
}

.meta-row {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.progress-track {
  margin-top: 8px;
  height: 5px;
  border-radius: var(--radius-pill);
  background: #d8d8d5;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: var(--radius-pill);
}

.progress-fill--ok {
  background: var(--green);
}

.progress-fill--warning {
  background: var(--amber);
}

.progress-fill--neutral {
  background: #9a9a96;
}

.header-actions {
  display: flex;
  align-items: flex-start;
  gap: 4px;
}

.complete-all-toggle {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.danger-action {
  color: var(--red);
}

.toggle-button :deep(svg) {
  transition: transform 160ms ease;
}

[data-state='open'] .toggle-button :deep(svg) {
  transform: rotate(180deg);
}

.content-wrap {
  margin-top: 12px;
  border-top: 1px solid #d8d8d5;
  padding-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.empty-state {
  font-size: 0.94rem;
  color: var(--text-secondary);
  background: #efefed;
  border-radius: var(--radius-md);
  padding: 10px;
}

.item-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.item-row {
  border: 1px solid #d9d9d6;
  border-radius: var(--radius-md);
  background: #f3f3f2;
  padding: 10px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 10px;
}

.item-main {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.item-title {
  margin: 0;
  font-weight: 700;
}

.item-title--done {
  text-decoration: line-through;
  color: #5b615f;
}

.item-description {
  margin: 2px 0 0;
  font-size: 0.92rem;
  color: var(--text-secondary);
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}

@media (max-width: 760px) {
  .card-header {
    flex-direction: column;
  }

  .header-actions {
    justify-content: flex-end;
  }

  .item-row {
    flex-direction: column;
  }
}
</style>
