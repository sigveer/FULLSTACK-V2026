<script setup lang="ts">
import Button from '@/components/ui/button/Button.vue'
import Dialog from '@/components/ui/dialog/Dialog.vue'
import DialogContent from '@/components/ui/dialog/DialogContent.vue'
import DialogDescription from '@/components/ui/dialog/DialogDescription.vue'
import DialogFooter from '@/components/ui/dialog/DialogFooter.vue'
import DialogHeader from '@/components/ui/dialog/DialogHeader.vue'
import DialogTitle from '@/components/ui/dialog/DialogTitle.vue'
import StatusPill from '@/components/ui/StatusPill.vue'
import AlertDialog from '@/components/ui/alert-dialog/AlertDialog.vue'
import AlertDialogAction from '@/components/ui/alert-dialog/AlertDialogAction.vue'
import AlertDialogCancel from '@/components/ui/alert-dialog/AlertDialogCancel.vue'
import AlertDialogContent from '@/components/ui/alert-dialog/AlertDialogContent.vue'
import AlertDialogDescription from '@/components/ui/alert-dialog/AlertDialogDescription.vue'
import AlertDialogFooter from '@/components/ui/alert-dialog/AlertDialogFooter.vue'
import AlertDialogHeader from '@/components/ui/alert-dialog/AlertDialogHeader.vue'
import AlertDialogTitle from '@/components/ui/alert-dialog/AlertDialogTitle.vue'
import AlertDialogTrigger from '@/components/ui/alert-dialog/AlertDialogTrigger.vue'
import type { Deviation, DeviationModule, DeviationSeverity, DeviationStatus } from '@/types/deviation'

const props = withDefaults(
  defineProps<{
    open: boolean
    deviation: Deviation | null
    canManage: boolean
  }>(),
  {
    deviation: null,
  },
)

const emits = defineEmits<{
  (e: 'update:open', value: boolean): void
  (e: 'edit', deviation: Deviation): void
  (e: 'delete', id: number): void
  (e: 'update-status', payload: { id: number; status: DeviationStatus }): void
}>()

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

const severityTone: Record<DeviationSeverity, 'ok' | 'warning' | 'danger'> = {
  LOW: 'ok',
  MEDIUM: 'warning',
  HIGH: 'warning',
  CRITICAL: 'danger',
}

const availableStatuses: Array<{ value: DeviationStatus; label: string }> = [
  { value: 'OPEN', label: 'Åpen' },
  { value: 'IN_PROGRESS', label: 'Under behandling' },
  { value: 'RESOLVED', label: 'Løst' },
]

function onStatusClick(status: DeviationStatus) {
  if (!props.deviation) {
    return
  }

  emits('update-status', {
    id: props.deviation.id,
    status,
  })
}
</script>

<template>
  <Dialog :open="open" @update:open="(value) => emits('update:open', value)">
    <DialogContent class="details-dialog">
      <DialogHeader>
        <DialogTitle>{{ deviation?.title ?? 'Avvik' }}</DialogTitle>
        <DialogDescription>
          Full informasjon om avvikssaken
        </DialogDescription>
      </DialogHeader>

      <div v-if="deviation" class="details-wrap">
        <div class="tag-row">
          <StatusPill :label="severityLabel[deviation.severity]" :tone="severityTone[deviation.severity]" />
          <StatusPill :label="moduleLabel[deviation.module]" tone="brand" />
          <StatusPill :label="statusLabel[deviation.status]" :tone="deviation.status === 'OPEN' ? 'danger' : deviation.status === 'IN_PROGRESS' ? 'warning' : 'ok'" />
        </div>

        <section class="block">
          <h4>Beskrivelse</h4>
          <p>{{ deviation.description }}</p>
        </section>

        <section class="block">
          <h4>Umiddelbar korrigerende handling</h4>
          <p>{{ deviation.immediateAction ?? 'Ikke angitt' }}</p>
        </section>

        <section class="block metadata-grid">
          <div>
            <h4>Rapportert av</h4>
            <p>{{ deviation.reportedByUserName }}</p>
          </div>
          <div>
            <h4>Tildelt ansvar</h4>
            <p>{{ deviation.assignedToUserName ?? 'Ikke tildelt' }}</p>
          </div>
          <div>
            <h4>Rapportert</h4>
            <p>{{ new Date(deviation.reportedAt).toLocaleString('nb-NO') }}</p>
          </div>
          <div>
            <h4>Løst</h4>
            <p>{{ deviation.resolvedAt ? new Date(deviation.resolvedAt).toLocaleString('nb-NO') : 'Ikke løst' }}</p>
          </div>
          <div>
            <h4>Sist oppdatert</h4>
            <p>{{ new Date(deviation.updatedAt).toLocaleString('nb-NO') }}</p>
          </div>
        </section>

        <section v-if="canManage" class="block">
          <h4>Status</h4>
          <div class="status-buttons">
            <button
              v-for="statusOption in availableStatuses"
              :key="statusOption.value"
              type="button"
              class="status-button"
              :class="{ 'status-button--active': deviation.status === statusOption.value }"
              @click="onStatusClick(statusOption.value)"
            >
              {{ statusOption.label }}
            </button>
          </div>
        </section>
      </div>

      <DialogFooter v-if="deviation && canManage" class="footer-actions">
        <Button variant="secondary" @click="emits('edit', deviation)">Rediger avvik</Button>

        <AlertDialog>
          <AlertDialogTrigger>
            <Button variant="destructive">Slett avvik</Button>
          </AlertDialogTrigger>
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
      </DialogFooter>
    </DialogContent>
  </Dialog>
</template>

<style scoped>
.details-wrap {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.block {
  border-top: 1px solid #e5e5df;
  padding-top: 10px;
}

.block h4 {
  margin: 0 0 4px;
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.block p {
  margin: 0;
  font-size: 1rem;
}

.metadata-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.status-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.status-button {
  border: 1px solid hsl(var(--input));
  background: hsl(var(--card));
  color: hsl(var(--foreground));
  border-radius: var(--radius-pill);
  padding: 6px 10px;
  font-size: 0.84rem;
  cursor: pointer;
}

.status-button--active {
  border-color: hsl(var(--primary));
  background: hsl(var(--accent));
  color: hsl(var(--accent-foreground));
}

.footer-actions {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}

@media (max-width: 820px) {
  .metadata-grid {
    grid-template-columns: 1fr;
  }

  .footer-actions {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
