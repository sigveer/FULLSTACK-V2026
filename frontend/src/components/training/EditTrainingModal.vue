<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import axios from 'axios'
import { toast } from 'vue-sonner'
import Dialog from '@/components/ui/dialog/Dialog.vue'
import DialogContent from '@/components/ui/dialog/DialogContent.vue'
import DialogDescription from '@/components/ui/dialog/DialogDescription.vue'
import DialogFooter from '@/components/ui/dialog/DialogFooter.vue'
import DialogHeader from '@/components/ui/dialog/DialogHeader.vue'
import DialogTitle from '@/components/ui/dialog/DialogTitle.vue'
import Button from '@/components/ui/button/Button.vue'
import Input from '@/components/ui/input/Input.vue'
import Textarea from '@/components/ui/textarea/Textarea.vue'
import {
  useUpdateTrainingLogMutation,
  useOrganizationMembersQuery,
} from '@/composables/useTrainingLogs'
import type { TrainingLog, TrainingStatus } from '@/types/training'

const props = defineProps<{
  open: boolean
  training: TrainingLog | null
}>()
const emits = defineEmits<{ (e: 'update:open', value: boolean): void }>()

const updateMutation = useUpdateTrainingLogMutation()
const canManage = computed(() => true)
const membersQuery = useOrganizationMembersQuery(canManage)
const members = computed(() => membersQuery.data.value ?? [])

const employeeUserId = ref<string>('')
const title = ref('')
const description = ref('')
const completedAt = ref('')
const expiresAt = ref('')
const status = ref<TrainingStatus>('COMPLETED')
const errorMessage = ref('')

const statusOptions: Array<{ value: TrainingStatus; label: string }> = [
  { value: 'COMPLETED', label: 'Fullført' },
  { value: 'EXPIRES_SOON', label: 'Utløper snart' },
  { value: 'EXPIRED', label: 'Utgått' },
  { value: 'NOT_COMPLETED', label: 'Ikke fullført' },
]

function isoToDateInput(iso: string | null): string {
  if (!iso) return ''
  return iso.slice(0, 10)
}

watch(
  () => [props.open, props.training],
  ([isOpen]) => {
    if (!isOpen) return
    if (props.training) {
      employeeUserId.value = String(props.training.employeeUserId)
      title.value = props.training.title
      description.value = props.training.description ?? ''
      completedAt.value = isoToDateInput(props.training.completedAt)
      expiresAt.value = isoToDateInput(props.training.expiresAt)
      status.value = props.training.status
    }
    errorMessage.value = ''
  },
)

function closeDialog() {
  emits('update:open', false)
}

function toIso(dateStr: string): string | undefined {
  if (!dateStr) return undefined
  return new Date(dateStr + 'T00:00:00Z').toISOString()
}

async function handleSubmit() {
  if (!props.training) return
  const trimmedTitle = title.value.trim()
  if (!trimmedTitle) {
    errorMessage.value = 'Opplæringstype er påkrevd.'
    return
  }
  errorMessage.value = ''

  try {
    await updateMutation.mutateAsync({
      id: props.training.id,
      payload: {
        employeeUserId: Number(employeeUserId.value) || undefined,
        title: trimmedTitle || undefined,
        description: description.value.trim() || undefined,
        completedAt: toIso(completedAt.value),
        expiresAt: toIso(expiresAt.value),
        status: status.value,
      },
    })
    toast.success('Opplæring oppdatert')
    closeDialog()
  } catch (error) {
    if (axios.isAxiosError(error)) {
      const msg = error.response?.data?.error?.message
      if (typeof msg === 'string' && msg.trim()) {
        toast.error(msg)
        return
      }
    }
    toast.error('Kunne ikke oppdatere opplæring')
  }
}
</script>

<template>
  <Dialog :open="open" @update:open="(value) => emits('update:open', value)">
    <DialogContent>
      <DialogHeader>
        <DialogTitle>Rediger opplæring</DialogTitle>
        <DialogDescription>Oppdater informasjonen for valgt opplæring</DialogDescription>
      </DialogHeader>

      <form class="form" @submit.prevent="handleSubmit">
        <label class="field">
          <span>Ansatt</span>
          <select v-model="employeeUserId" class="select-field">
            <option value="" disabled>Velg ansatt…</option>
            <option v-for="m in members" :key="m.userId" :value="String(m.userId)">
              {{ m.userFullName }}
            </option>
          </select>
        </label>

        <label class="field">
          <span>Opplæringstype *</span>
          <Input v-model="title" placeholder="Skriv inn type..." />
        </label>

        <label class="field">
          <span>Beskrivelse</span>
          <Textarea v-model="description" rows="3" placeholder="Valgfri beskrivelse..." />
        </label>

        <div class="field-row">
          <label class="field">
            <span>Fullført dato</span>
            <Input v-model="completedAt" type="date" />
          </label>
          <label class="field">
            <span>Utløpsdato</span>
            <Input v-model="expiresAt" type="date" />
          </label>
        </div>

        <div class="field">
          <span>Status</span>
          <div class="segmented-grid segmented-grid--4">
            <button
              v-for="opt in statusOptions"
              :key="opt.value"
              type="button"
              class="segment-button"
              :class="[
                `segment-button--${opt.value.toLowerCase()}`,
                { 'segment-button--active': status === opt.value },
              ]"
              @click="status = opt.value"
            >
              {{ opt.label }}
            </button>
          </div>
        </div>

        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

        <DialogFooter>
          <Button type="button" variant="outline" @click="closeDialog">Avbryt</Button>
          <Button type="submit" :disabled="updateMutation.isPending.value">
            {{ updateMutation.isPending.value ? 'Lagrer...' : 'Lagre endringer' }}
          </Button>
        </DialogFooter>
      </form>
    </DialogContent>
  </Dialog>
</template>

<style scoped>
.form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field span {
  font-size: 0.92rem;
  font-weight: 600;
}

.field-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.select-field {
  width: 100%;
  height: 2.5rem;
  border-radius: 0.5rem;
  border: 1px solid hsl(var(--input));
  background: hsl(var(--card));
  padding: 0 0.75rem;
  font-size: 0.875rem;
}

.select-field:focus-visible {
  outline: none;
  box-shadow: 0 0 0 2px hsl(var(--ring) / 0.2);
  border-color: hsl(var(--primary) / 0.5);
}

.segmented-grid {
  display: grid;
  gap: 8px;
}

.segmented-grid--4 {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.segment-button {
  border: 1px solid hsl(var(--input));
  background: hsl(var(--card));
  color: hsl(var(--foreground));
  border-radius: var(--radius-md);
  height: 2.5rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 150ms ease, border-color 150ms ease, color 150ms ease, box-shadow 150ms ease;
}

.segment-button--active {
  border-color: hsl(var(--primary));
  background: hsl(var(--accent));
  color: hsl(var(--accent-foreground));
}

.segment-button--completed.segment-button--active {
  border-color: #71a66a;
  background: #dcebd8;
  color: #2f6f34;
}

.segment-button--expires_soon.segment-button--active {
  border-color: #d1a768;
  background: #f4e6d1;
  color: #d0a11f;
}

.segment-button--expired.segment-button--active {
  border-color: #d68b3b;
  background: #f6dfc8;
  color: #a2550c;
}

.segment-button--not_completed.segment-button--active {
  border-color: #c95d5d;
  background: #f4e0e0;
  color: #902324;
}

.error-message {
  color: hsl(var(--destructive));
  font-size: 0.86rem;
}

@media (max-width: 780px) {
  .segmented-grid--4 {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
