<script setup lang="ts">
import { computed, ref } from 'vue'
import { MoreVertical, CheckCircle2, Pencil, Trash2, Check } from 'lucide-vue-next'
import type { Checklist } from '@/types/checklist'
import Badge from '@/components/ui/badge/Badge.vue'
import Button from '@/components/ui/button/Button.vue'
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
    case 'MONTHLY': return 'M\u00E5nedlig'
    case 'YEARLY': return '\u00C5rlig'
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
    case 'COMPLETED': return 'Fullf\u00F8rt'
    case 'IN_PROGRESS': return 'P\u00E5g\u00E5r'
    default: return 'Ikke startet'
  }
})

const isComplete = computed(() =>
  props.checklist.itemCount > 0 && props.checklist.completedItemCount === props.checklist.itemCount,
)

const progressColor = computed(() => {
  switch (props.checklist.status) {
    case 'COMPLETED': return 'var(--green, #3c8f2c)'
    case 'IN_PROGRESS': return 'var(--amber, #c08a2c)'
    default: return '#9a9a96'
  }
})

const metaLine = computed(() => {
  const { checklist } = props
  const parts: string[] = []

  const countLabel = `${checklist.completedItemCount}/${checklist.itemCount} punkter`
  if (checklist.status === 'COMPLETED') {
    parts.push(`${countLabel} fullf\u00F8rt`)
  } else {
    parts.push(countLabel)
  }

  if (checklist.description) {
    parts.push(checklist.description)
  } else {
    parts.push(statusLabel.value)
  }

  if (checklist.status === 'COMPLETED') {
    const lastCompleted = checklist.items
      ?.filter(i => i.completedAt)
      .map(i => new Date(i.completedAt!).getTime())
      .sort((a, b) => b - a)[0]

    if (lastCompleted) {
      const d = new Date(lastCompleted)
      parts.push(d.toLocaleTimeString('nb-NO', { hour: '2-digit', minute: '2-digit' }))
    }
  }

  return parts.join(' \u00B7 ')
})
</script>

<template>
  <div class="checklist-card">
    <button type="button" class="card-body" @click="emits('open', checklist)">
      <!-- Circle indicator -->
      <div class="circle" :class="{ 'circle--done': isComplete, 'circle--progress': !isComplete && checklist.status === 'IN_PROGRESS', 'circle--idle': checklist.status === 'NOT_STARTED' }">
        <Check v-if="isComplete" class="circle-check" />
        <span v-else class="circle-count">{{ checklist.completedItemCount }}/{{ checklist.itemCount }}</span>
      </div>

      <!-- Content -->
      <div class="card-content">
        <div class="title-row">
          <h3>{{ checklist.name }}</h3>
          <Badge :tone="frequencyTone" class="type-badge">{{ frequencyLabel }}</Badge>
        </div>

        <p class="meta">
          {{ metaLine }}
        </p>

        <div class="progress-track">
          <div
            class="progress-fill"
            :style="{ width: `${completionPercent}%`, backgroundColor: progressColor }"
          />
        </div>
      </div>

    </button>

    <div v-if="canManage || canComplete" class="card-actions" @click.stop>
      <DropdownMenu>
        <DropdownMenuTrigger as-child>
          <Button type="button" variant="ghost" size="icon-sm" class="actions-trigger">
            <MoreVertical :size="18" />
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end" :side-offset="4">
          <DropdownMenuItem
            v-if="canComplete && checklist.itemCount > 0"
            @click="emits('toggle-checklist-completed', { checklistId: checklist.id, completed: !isComplete })"
          >
            <CheckCircle2 :size="16" class="menu-icon--green" />
            {{ isComplete ? 'Angre fullf\u00F8ring' : 'Fullf\u00F8r hele' }}
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
</template>

<style scoped>
.checklist-card {
  position: relative;
  width: 100%;
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  background: var(--card-bg);
  display: flex;
  align-items: flex-start;
  transition: box-shadow 150ms ease;
}

.checklist-card:hover {
  box-shadow: 0 6px 14px rgb(0 0 0 / 0.08);
}

.card-body {
  flex: 1;
  min-width: 0;
  padding: 16px 16px 16px 20px;
  text-align: left;
  cursor: pointer;
  background: none;
  border: none;
  font: inherit;
  color: inherit;
  display: flex;
  align-items: center;
  gap: 16px;
}

.card-body:focus-visible {
  outline: none;
  box-shadow: inset 0 0 0 2px hsl(var(--ring) / 0.3);
  border-radius: var(--radius-lg);
}

/* Circle indicator */
.circle {
  width: 3rem;
  height: 3rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-weight: 600;
  font-size: 0.875rem;
}

.circle--done {
  background-color: var(--green-soft, #e6f4e6);
  color: #2f6f34;
}

.circle-check {
  width: 1.5rem;
  height: 1.5rem;
  color: #2f6f34;
  stroke-width: 3;
}

.circle--progress {
  background-color: var(--amber-soft, #fdf3e0);
  color: #8e5713;
}

.circle--idle {
  background-color: #e9e9e8;
  color: #464b52;
}

.circle-count {
  font-size: 0.9rem;
  font-weight: 600;
  letter-spacing: -0.01em;
}

/* Content area */
.card-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

h3 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 700;
  letter-spacing: -0.01em;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.type-badge {
  flex-shrink: 0;
  font-size: 0.72rem;
  padding: 0.2rem 0.6rem;
}

.meta {
  margin: 0;
  font-size: 0.85rem;
  color: var(--text-secondary, #6b7280);
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.progress-track {
  margin-top: 6px;
  height: 5px;
  border-radius: var(--radius-pill);
  background: #d8d8d5;
  overflow: hidden;
  width: 100%;
}

.progress-fill {
  height: 100%;
  border-radius: var(--radius-pill);
  transition: width 300ms ease;
}

/* Actions */
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
