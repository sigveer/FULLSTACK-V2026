<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import Dialog from '@/components/ui/dialog/Dialog.vue'
import DialogContent from '@/components/ui/dialog/DialogContent.vue'
import DialogDescription from '@/components/ui/dialog/DialogDescription.vue'
import DialogFooter from '@/components/ui/dialog/DialogFooter.vue'
import DialogHeader from '@/components/ui/dialog/DialogHeader.vue'
import DialogTitle from '@/components/ui/dialog/DialogTitle.vue'
import Button from '@/components/ui/button/Button.vue'
import Input from '@/components/ui/input/Input.vue'
import Textarea from '@/components/ui/textarea/Textarea.vue'
import type {
  CreateDeviationRequest,
  Deviation,
  DeviationModule,
  DeviationSeverity,
  UpdateDeviationRequest,
} from '@/types/deviation'

interface AssigneeOption {
  userId: number
  label: string
}

const props = withDefaults(
  defineProps<{
    open: boolean
    mode?: 'create' | 'edit'
    initialDeviation?: Deviation | null
    submitting?: boolean
    assignees: AssigneeOption[]
  }>(),
  {
    mode: 'create',
    initialDeviation: null,
    submitting: false,
  },
)

const emits = defineEmits<{
  (e: 'update:open', value: boolean): void
  (e: 'create', payload: CreateDeviationRequest): void
  (e: 'update', payload: { id: number; data: UpdateDeviationRequest }): void
}>()

const moduleValue = ref<DeviationModule>('IK_MAT')
const title = ref('')
const severity = ref<DeviationSeverity>('LOW')
const description = ref('')
const immediateAction = ref('')
const assignedTo = ref<string>('')
const errorMessage = ref('')

const moduleOptions: Array<{ value: DeviationModule; label: string }> = [
  { value: 'IK_MAT', label: 'IK-Mat' },
  { value: 'IK_ALKOHOL', label: 'IK-Alkohol' },
]

const severityOptions: Array<{ value: DeviationSeverity; label: string }> = [
  { value: 'LOW', label: 'Lav' },
  { value: 'MEDIUM', label: 'Middels' },
  { value: 'HIGH', label: 'Høy' },
  { value: 'CRITICAL', label: 'Kritisk' },
]

const dialogTitle = computed(() => (props.mode === 'create' ? 'Rapporter avvik' : 'Rediger avvik'))

const dialogDescription = computed(() =>
  props.mode === 'create'
    ? 'Registrer et avvik fra rutiner eller grenseverdier'
    : 'Oppdater informasjonen for valgt avvik',
)

const submitLabel = computed(() => {
  if (props.submitting) {
    return props.mode === 'create' ? 'Registrerer...' : 'Lagrer...'
  }

  return props.mode === 'create' ? 'Registrer avvik' : 'Lagre endringer'
})

watch(
  () => [props.open, props.mode, props.initialDeviation],
  (open) => {
    const isOpen = Array.isArray(open) ? open[0] : open
    if (!isOpen) {
      return
    }

    if (props.mode === 'edit' && props.initialDeviation) {
      moduleValue.value = props.initialDeviation.module
      title.value = props.initialDeviation.title
      severity.value = props.initialDeviation.severity
      description.value = props.initialDeviation.description
      immediateAction.value = props.initialDeviation.immediateAction ?? ''
      assignedTo.value = props.initialDeviation.assignedToUserId
        ? String(props.initialDeviation.assignedToUserId)
        : ''
    } else {
      moduleValue.value = 'IK_MAT'
      title.value = ''
      severity.value = 'LOW'
      description.value = ''
      immediateAction.value = ''
      assignedTo.value = ''
    }

    errorMessage.value = ''
  },
)

function closeDialog() {
  emits('update:open', false)
}

function handleSubmit() {
  const trimmedTitle = title.value.trim()
  const trimmedDescription = description.value.trim()
  const trimmedImmediateAction = immediateAction.value.trim()

  if (!trimmedTitle) {
    errorMessage.value = 'Tittel er paakrevd.'
    return
  }

  if (!trimmedDescription) {
    errorMessage.value = 'Beskrivelse er paakrevd.'
    return
  }

  errorMessage.value = ''

  const payload: CreateDeviationRequest = {
    module: moduleValue.value,
    title: trimmedTitle,
    description: trimmedDescription,
    severity: severity.value,
  }

  if (trimmedImmediateAction) {
    payload.immediateAction = trimmedImmediateAction
  }

  if (assignedTo.value) {
    payload.assignedToUserId = Number(assignedTo.value)
  }

  if (props.mode === 'edit' && props.initialDeviation) {
    emits('update', {
      id: props.initialDeviation.id,
      data: payload,
    })
    return
  }

  emits('create', payload)
}
</script>

<template>
  <Dialog :open="open" @update:open="(value) => emits('update:open', value)">
    <DialogContent class="deviation-dialog">
      <DialogHeader>
        <DialogTitle>{{ dialogTitle }}</DialogTitle>
        <DialogDescription>
          {{ dialogDescription }}
        </DialogDescription>
      </DialogHeader>

      <form class="form" @submit.prevent="handleSubmit">
        <div class="field">
          <span>Modul</span>
          <div class="segmented-grid segmented-grid--2">
            <button
              v-for="option in moduleOptions"
              :key="option.value"
              type="button"
              class="segment-button"
              :class="{ 'segment-button--active': moduleValue === option.value }"
              @click="moduleValue = option.value"
            >
              {{ option.label }}
            </button>
          </div>
        </div>

        <label class="field">
          <span>Tittel *</span>
          <Input v-model="title" placeholder="For eksempel: Kjøleskap 2 over grenseverdi" />
        </label>

        <div class="field">
          <span>Alvorlighetsgrad *</span>
          <div class="segmented-grid segmented-grid--4">
            <button
              v-for="option in severityOptions"
              :key="option.value"
              type="button"
              class="segment-button severity-button"
              :class="[
                `severity-button--${option.value.toLowerCase()}`,
                { 'segment-button--active': severity === option.value },
              ]"
              @click="severity = option.value"
            >
              {{ option.label }}
            </button>
          </div>
        </div>

        <label class="field">
          <span>Beskrivelse *</span>
          <Textarea
            v-model="description"
            rows="4"
            placeholder="Beskriv hva som er observert, og hva som avviker fra krav eller rutine"
          />
        </label>

        <label class="field">
          <span>Umiddelbar korrigerende handling</span>
          <Textarea
            v-model="immediateAction"
            rows="3"
            placeholder="Beskriv handling utført med en gang"
          />
        </label>

        <label class="field">
          <span>Tildel til</span>
          <select v-model="assignedTo" class="select-field">
            <option value="">Ikke tildelt</option>
            <option v-for="assignee in assignees" :key="assignee.userId" :value="String(assignee.userId)">
              {{ assignee.label }}
            </option>
          </select>
        </label>

        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

        <DialogFooter>
          <Button type="button" variant="outline" @click="closeDialog">Avbryt</Button>
          <Button type="submit" :disabled="submitting">{{ submitLabel }}</Button>
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

.segmented-grid {
  display: grid;
  gap: 8px;
}

.segmented-grid--2 {
  grid-template-columns: repeat(2, minmax(0, 1fr));
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

.severity-button--low.segment-button--active {
  border-color: #71a66a;
  background: #dcebd8;
  color: #2f6f34;
}

.severity-button--medium.segment-button--active {
  border-color: #d1a768;
  background: #f4e6d1;
  color: #d0a11f;
}

.severity-button--high.segment-button--active {
  border-color: #d68b3b;
  background: #f6dfc8;
  color: #a2550c;
}

.severity-button--critical.segment-button--active {
  border-color: #c95d5d;
  background: #f4e0e0;
  color: #902324;
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

.error-message {
  color: hsl(var(--destructive));
  font-size: 0.86rem;
}

@media (max-width: 780px) {
  .deviation-dialog {
    width: min(96vw, 42rem);
  }

  .segmented-grid--4 {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
