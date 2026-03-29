<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import type { Checklist, ChecklistFrequency, CreateChecklistRequest, UpdateChecklistRequest } from '@/types/checklist'
import Dialog from '@/components/ui/dialog/Dialog.vue'
import DialogContent from '@/components/ui/dialog/DialogContent.vue'
import DialogHeader from '@/components/ui/dialog/DialogHeader.vue'
import DialogTitle from '@/components/ui/dialog/DialogTitle.vue'
import DialogDescription from '@/components/ui/dialog/DialogDescription.vue'
import DialogFooter from '@/components/ui/dialog/DialogFooter.vue'
import Button from '@/components/ui/button/Button.vue'
import Input from '@/components/ui/input/Input.vue'
import Textarea from '@/components/ui/textarea/Textarea.vue'
import Switch from '@/components/ui/switch/Switch.vue'

const props = withDefaults(
  defineProps<{
    open: boolean
    mode: 'create' | 'edit'
    initialChecklist?: Checklist | null
    submitting?: boolean
  }>(),
  {
    initialChecklist: null,
    submitting: false,
  },
)

const emits = defineEmits<{
  (e: 'update:open', value: boolean): void
  (e: 'create', payload: CreateChecklistRequest): void
  (e: 'update', payload: { checklistId: number; data: UpdateChecklistRequest }): void
}>()

const formName = ref('')
const formDescription = ref('')
const formFrequency = ref<ChecklistFrequency>('DAILY')
const formActive = ref(true)
const errorMessage = ref('')

const title = computed(() =>
  props.mode === 'create' ? 'Opprett ny sjekkliste' : 'Rediger sjekkliste',
)

const submitLabel = computed(() => {
  if (props.submitting) {
    return props.mode === 'create' ? 'Oppretter...' : 'Lagrer...'
  }
  return props.mode === 'create' ? 'Opprett sjekkliste' : 'Lagre endringer'
})

watch(
  () => [props.open, props.initialChecklist, props.mode],
  ([open]) => {
    if (!open) {
      return
    }

    if (props.mode === 'edit' && props.initialChecklist) {
      formName.value = props.initialChecklist.name
      formDescription.value = props.initialChecklist.description ?? ''
      formFrequency.value = props.initialChecklist.frequency
      formActive.value = props.initialChecklist.active
    } else {
      formName.value = ''
      formDescription.value = ''
      formFrequency.value = 'DAILY'
      formActive.value = true
    }

    errorMessage.value = ''
  },
  { immediate: true },
)

function closeDialog() {
  emits('update:open', false)
}

function handleSubmit() {
  const name = formName.value.trim()
  if (!name) {
    errorMessage.value = 'Navn på sjekkliste er påkrevd.'
    return
  }

  errorMessage.value = ''

  if (props.mode === 'create') {
    emits('create', {
      name,
      description: formDescription.value.trim() || undefined,
      frequency: formFrequency.value,
    })
    return
  }

  if (!props.initialChecklist) {
    return
  }

  emits('update', {
    checklistId: props.initialChecklist.id,
    data: {
      name,
      description: formDescription.value.trim() || undefined,
      frequency: formFrequency.value,
      active: formActive.value,
    },
  })
}
</script>

<template>
  <Dialog :open="open" @update:open="(value) => emits('update:open', value)">
    <DialogContent class="checklist-dialog">
      <DialogHeader>
        <DialogTitle>{{ title }}</DialogTitle>
        <DialogDescription>
          Definer navn, frekvens og innhold for sjekklisten.
        </DialogDescription>
      </DialogHeader>

      <form class="form" @submit.prevent="handleSubmit">
        <label class="field">
          <span>Navn</span>
          <Input v-model="formName" placeholder="For eksempel: Morgenrutine kjøkken" />
        </label>

        <label class="field">
          <span>Frekvens</span>
          <select v-model="formFrequency" class="select-field">
            <option value="DAILY">Daglig</option>
            <option value="WEEKLY">Ukentlig</option>
            <option value="MONTHLY">Månedlig</option>
            <option value="YEARLY">Årlig</option>
          </select>
        </label>

        <label class="field">
          <span>Beskrivelse (valgfritt)</span>
          <Textarea v-model="formDescription" rows="3" placeholder="Kort beskrivelse av hva sjekklisten gjelder" />
        </label>

        <label v-if="mode === 'edit'" class="toggle-row">
          <span>Aktiv</span>
          <Switch :checked="formActive" @update:checked="(value) => (formActive = value)" />
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

.select-field {
  width: 100%;
  height: 2.5rem;
  border: 1px solid hsl(var(--input, 35 15% 85%));
  border-radius: 0.5rem;
  background-color: hsl(var(--card, 40 25% 98%));
  font-size: 0.92rem;
  color: hsl(var(--foreground));
  padding: 0 12px;
  outline: none;
}

.select-field:focus {
  border-color: hsl(var(--primary, 245 43% 52%) / 0.5);
  box-shadow: 0 0 0 2px hsl(var(--ring, 245 43% 52%) / 0.2);
}

.toggle-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.error-message {
  color: hsl(var(--destructive));
  font-size: 0.86rem;
}

@media (max-width: 720px) {
  .checklist-dialog {
    width: min(96vw, 32rem);
  }
}
</style>
