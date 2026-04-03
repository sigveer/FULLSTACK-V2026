<script setup lang="ts">
import { computed, ref } from 'vue'
import { MoreVertical, Trash2, Pencil, CircleDot, Clock, CheckCircle2 } from 'lucide-vue-next'
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
import type { FoodDeviation, FoodDeviationType, FoodDeviationStatus } from '@/types/deviation'

const props = defineProps<{
  deviation: FoodDeviation
  canManage: boolean
}>()

const emits = defineEmits<{
  (e: 'edit', d: FoodDeviation): void
  (e: 'delete', id: number): void
  (e: 'update-status', id: number, status: FoodDeviationStatus): void
  (e: 'open', d: FoodDeviation): void
}>()

const deleteDialogOpen = ref(false)

const typeLabel: Record<FoodDeviationType, string> = {
  TEMPERATUR: 'Temperatur',
  RENHOLD: 'Renhold',
  PERSONLIG_HYGIENE: 'Personlig hygiene',
  ALLERGEN: 'Allergen',
  SKADEDYR: 'Skadedyr',
  MOTTAKSKONTROLL: 'Mottakskontroll',
  ANNET: 'Annet',
}

const statusLabel: Record<FoodDeviationStatus, string> = {
  OPEN: 'Åpen', UNDER_TREATMENT: 'Under behandling', CLOSED: 'Lukket',
}

const statusTone: Record<FoodDeviationStatus, 'danger' | 'warning' | 'ok'> = {
  OPEN: 'danger', UNDER_TREATMENT: 'warning', CLOSED: 'ok',
}

const relativeTime = computed(() => {
  const diffMs = Date.now() - new Date(props.deviation.reportedAt).getTime()
  const min = 60_000; const hr = 3_600_000; const day = 86_400_000
  if (diffMs < hr) return `${Math.max(1, Math.floor(diffMs / min))} min siden`
  if (diffMs < day) { const h = Math.floor(diffMs / hr); return `${h} time${h > 1 ? 'r' : ''} siden` }
  const d = Math.floor(diffMs / day); return `${d} dag${d > 1 ? 'er' : ''} siden`
})
</script>

<template>
  <div class="deviation-card" role="button" tabindex="0" @click="emits('open', deviation)" @keydown.enter="emits('open', deviation)">
    <div class="card-top">
      <div class="card-body">
        <div class="tag-row">
          <StatusPill :label="typeLabel[deviation.deviationType]" tone="neutral" />
          <StatusPill label="IK-Mat" tone="brand" />
          <StatusPill :label="statusLabel[deviation.status]" :tone="statusTone[deviation.status]" />
        </div>
        <p class="description">{{ deviation.description }}</p>
        <span class="time-label">{{ relativeTime }}</span>
      </div>

      <div v-if="canManage" class="card-actions" @click.stop>
        <DropdownMenu>
          <DropdownMenuTrigger as-child>
            <button type="button" class="actions-trigger"><MoreVertical :size="18" /></button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" :side-offset="4">
            <DropdownMenuItem @click="emits('edit', deviation)">
              <Pencil :size="16" /> Rediger
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem v-if="deviation.status !== 'OPEN'" class="menu-item--status-open" @click="emits('update-status', deviation.id, 'OPEN')">
              <CircleDot :size="16" /> Åpen
            </DropdownMenuItem>
            <DropdownMenuItem v-if="deviation.status !== 'UNDER_TREATMENT'" class="menu-item--status-progress" @click="emits('update-status', deviation.id, 'UNDER_TREATMENT')">
              <Clock :size="16" /> Under behandling
            </DropdownMenuItem>
            <DropdownMenuItem v-if="deviation.status !== 'CLOSED'" class="menu-item--status-closed" @click="emits('update-status', deviation.id, 'CLOSED')">
              <CheckCircle2 :size="16" /> Lukket
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem class="menu-item--danger" @click="deleteDialogOpen = true">
              <Trash2 :size="16" /> Slett
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>

        <AlertDialog v-model:open="deleteDialogOpen">
          <AlertDialogContent>
            <AlertDialogHeader>
              <AlertDialogTitle>Slette matavvik?</AlertDialogTitle>
              <AlertDialogDescription>Avviket blir permanent slettet.</AlertDialogDescription>
            </AlertDialogHeader>
            <AlertDialogFooter>
              <AlertDialogCancel>Avbryt</AlertDialogCancel>
              <AlertDialogAction variant="destructive" @click="emits('delete', deviation.id)">Slett</AlertDialogAction>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialog>
      </div>
    </div>
  </div>
</template>

<style scoped>
.deviation-card {
  position: relative; width: 100%;
  border: 1px solid hsl(var(--border)); border-radius: var(--radius-lg);
  background: var(--card-bg); display: flex; flex-direction: column;
  transition: box-shadow 150ms ease; cursor: pointer;
}
.deviation-card:hover { box-shadow: 0 6px 14px rgb(0 0 0 / 0.08); }

.card-top { display: flex; align-items: flex-start; }
.card-body { flex: 1; min-width: 0; padding: 14px 16px; }
.tag-row { display: flex; flex-wrap: wrap; gap: 6px; }
.description {
  margin: 6px 0 4px; font-size: 1.05rem; line-height: 1.35;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.time-label { font-size: 0.82rem; color: hsl(var(--muted-foreground)); }

.card-actions { display: flex; align-items: flex-start; padding: 12px 12px 0 0; flex-shrink: 0; }
.actions-trigger {
  display: flex; align-items: center; justify-content: center;
  width: 2rem; height: 2rem; border-radius: var(--radius-md);
  border: none; background: none; color: var(--text-secondary); cursor: pointer;
  transition: background 150ms ease;
}
.actions-trigger:hover { background: hsl(var(--accent)); color: hsl(var(--foreground)); }

.menu-item--status-open { color: #a62929; }
.menu-item--status-progress { color: #946013; }
.menu-item--status-closed { color: #2f6f34; }
.menu-item--danger { color: #c62828; }
.menu-item--danger:hover { background-color: #fde8e8 !important; color: #c62828 !important; }
</style>
