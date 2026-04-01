<script setup lang="ts">
import { computed, ref } from 'vue'
import { MoreVertical, CircleDot, Clock, CheckCircle2, Trash2, Pencil } from 'lucide-vue-next'
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
import type { Deviation, DeviationModule, DeviationSeverity, DeviationStatus } from '@/types/deviation'

const props = defineProps<{
  deviation: Deviation
  canManage: boolean
}>()

const emits = defineEmits<{
  (e: 'open', deviation: Deviation): void
  (e: 'edit', deviation: Deviation): void
  (e: 'update-status', payload: { id: number; status: DeviationStatus }): void
  (e: 'delete', id: number): void
}>()

const deleteDialogOpen = ref(false)

const moduleLabel: Record<DeviationModule, string> = {
  IK_MAT: 'IK-Mat',
  IK_ALKOHOL: 'IK-Alkohol',
}

const statusLabel: Record<DeviationStatus, string> = {
  OPEN: 'Åpen',
  IN_PROGRESS: 'Under behandling',
  RESOLVED: 'Løst',
}

const severityLabel: Record<DeviationSeverity, string> = {
  LOW: 'Lav',
  MEDIUM: 'Middels',
  HIGH: 'Høy',
  CRITICAL: 'Kritisk',
}

const severityTone: Record<DeviationSeverity, 'neutral' | 'ok' | 'danger' | 'warning' | 'brand'> = {
  LOW: 'ok',
  MEDIUM: 'warning',
  HIGH: 'warning',
  CRITICAL: 'danger',
}

const statusTone: Record<DeviationStatus, 'neutral' | 'ok' | 'danger' | 'warning' | 'brand'> = {
  OPEN: 'danger',
  IN_PROGRESS: 'warning',
  RESOLVED: 'ok',
}

const severityRailClass: Record<DeviationSeverity, string> = {
  LOW: 'deviation-card--low',
  MEDIUM: 'deviation-card--medium',
  HIGH: 'deviation-card--high',
  CRITICAL: 'deviation-card--critical',
}

const relativeTime = computed(() => toRelativeTime(props.deviation.reportedAt))

function toRelativeTime(value: string): string {
  const timestamp = new Date(value).getTime()
  if (Number.isNaN(timestamp)) return '-'

  const diffMs = Date.now() - timestamp
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour

  if (diffMs < hour) {
    const minutes = Math.max(1, Math.floor(diffMs / minute))
    return `${minutes} min siden`
  }

  if (diffMs < day) {
    const hours = Math.floor(diffMs / hour)
    return `${hours} time${hours > 1 ? 'r' : ''} siden`
  }

  const days = Math.floor(diffMs / day)
  return `${days} dag${days > 1 ? 'er' : ''} siden`
}
</script>

<template>
  <div :class="['deviation-card', severityRailClass[deviation.severity]]">
    <div class="card-top">
      <button
        type="button"
        class="card-body"
        @click="emits('open', deviation)"
      >
        <div class="tag-row">
          <StatusPill :label="severityLabel[deviation.severity]" :tone="severityTone[deviation.severity]" />
          <StatusPill :label="moduleLabel[deviation.module]" tone="brand" />
          <StatusPill :label="statusLabel[deviation.status]" :tone="statusTone[deviation.status]" />
        </div>

        <h3>{{ deviation.title }}</h3>

        <div class="detail-row">
          <p class="description">{{ deviation.description }}</p>
          <span class="time-label">{{ relativeTime }}</span>
        </div>
      </button>

      <div v-if="canManage" class="card-actions" @click.stop>
        <DropdownMenu>
          <DropdownMenuTrigger as-child>
            <button type="button" class="actions-trigger">
              <MoreVertical :size="18" />
            </button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" :side-offset="4">
            <DropdownMenuItem @click="emits('edit', deviation)">
              <Pencil :size="16" />
              Rediger avvik
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem
              :class="deviation.status === 'OPEN' ? 'menu-item--active' : ''"
              @click="emits('update-status', { id: deviation.id, status: 'OPEN' })"
            >
              <CircleDot :size="16" class="menu-icon--red" />
              Åpen
            </DropdownMenuItem>
            <DropdownMenuItem
              :class="deviation.status === 'IN_PROGRESS' ? 'menu-item--active' : ''"
              @click="emits('update-status', { id: deviation.id, status: 'IN_PROGRESS' })"
            >
              <Clock :size="16" class="menu-icon--amber" />
              Under behandling
            </DropdownMenuItem>
            <DropdownMenuItem
              :class="deviation.status === 'RESOLVED' ? 'menu-item--active' : ''"
              @click="emits('update-status', { id: deviation.id, status: 'RESOLVED' })"
            >
              <CheckCircle2 :size="16" class="menu-icon--green" />
              Løst
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem class="menu-item--danger" @click="deleteDialogOpen = true">
              <Trash2 :size="16" />
              Slett avvik
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>

        <AlertDialog v-model:open="deleteDialogOpen">
          <AlertDialogContent>
            <AlertDialogHeader>
              <AlertDialogTitle>Slette avvik?</AlertDialogTitle>
              <AlertDialogDescription>
                Avviket blir permanent slettet og kan ikke gjenopprettes.
              </AlertDialogDescription>
            </AlertDialogHeader>
            <AlertDialogFooter>
              <AlertDialogCancel>Avbryt</AlertDialogCancel>
              <AlertDialogAction variant="destructive" @click="emits('delete', deviation.id)">
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
.deviation-card {
  position: relative;
  width: 100%;
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  background: var(--card-bg);
  display: flex;
  flex-direction: column;
  transition: box-shadow 150ms ease;
}

.deviation-card:hover {
  box-shadow: 0 6px 14px rgb(0 0 0 / 0.08);
}

.deviation-card::before {
  content: '';
  position: absolute;
  left: -1px;
  top: -1px;
  bottom: -1px;
  width: 5px;
  border-radius: var(--radius-lg) 0 0 var(--radius-lg);
  background: transparent;
}

.deviation-card--critical { border-color: hsl(var(--destructive) / 0.25); }
.deviation-card--critical::before { background: var(--red); }
.deviation-card--medium { border-color: #ead8a6; }
.deviation-card--medium::before { background: #d0a11f; }
.deviation-card--high { border-color: #e7bf99; }
.deviation-card--high::before { background: #c9751a; }
.deviation-card--low { border-color: #b9d9be; }
.deviation-card--low::before { background: var(--green); }

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
  margin: 6px 0 4px;
  font-size: 1.5rem;
  letter-spacing: -0.02em;
}

.detail-row {
  display: flex;
  align-items: baseline;
  gap: 12px;
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

.time-label {
  color: var(--text-secondary);
  font-size: 0.85rem;
  flex-shrink: 0;
  white-space: nowrap;
}

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

.menu-icon--red { color: #a62929; }
.menu-icon--amber { color: #946013; }
.menu-icon--green { color: #3c8f2c; }

.menu-item--active {
  font-weight: 600;
}

.menu-item--danger {
  color: #c62828;
}
.menu-item--danger:hover {
  background-color: #fde8e8 !important;
  color: #c62828 !important;
}
</style>
