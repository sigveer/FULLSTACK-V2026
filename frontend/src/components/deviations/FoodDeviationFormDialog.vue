<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import Dialog from '@/components/ui/dialog/Dialog.vue'
import DialogContent from '@/components/ui/dialog/DialogContent.vue'
import DialogDescription from '@/components/ui/dialog/DialogDescription.vue'
import DialogHeader from '@/components/ui/dialog/DialogHeader.vue'
import DialogTitle from '@/components/ui/dialog/DialogTitle.vue'
import Button from '@/components/ui/button/Button.vue'
import Input from '@/components/ui/input/Input.vue'
import Textarea from '@/components/ui/textarea/Textarea.vue'
import Select from '@/components/ui/select/Select.vue'
import SelectContent from '@/components/ui/select/SelectContent.vue'
import SelectItem from '@/components/ui/select/SelectItem.vue'
import SelectTrigger from '@/components/ui/select/SelectTrigger.vue'
import SelectValue from '@/components/ui/select/SelectValue.vue'
import type {
  CreateFoodDeviationRequest,
  FoodDeviation,
  FoodDeviationType,
  FoodDeviationStatus,
  DeviationSeverity,
  UpdateFoodDeviationRequest,
} from '@/types/deviation'

interface MemberOption { userId: number; label: string }

const props = withDefaults(
  defineProps<{
    open: boolean
    mode?: 'create' | 'edit'
    initial?: FoodDeviation | null
    prefill?: Partial<CreateFoodDeviationRequest> | null
    submitting?: boolean
    members: MemberOption[]
    inline?: boolean
  }>(),
  { mode: 'create', initial: null, prefill: null, submitting: false, inline: false },
)

const emits = defineEmits<{
  (e: 'update:open', v: boolean): void
  (e: 'create', payload: CreateFoodDeviationRequest): void
  (e: 'update', payload: { id: number; data: UpdateFoodDeviationRequest }): void
}>()

// Form state
const reportedDate = ref('')
const reportedTime = ref('')
const reportedByUserId = ref<string>('')
const deviationType = ref<FoodDeviationType>('TEMPERATUR')
const severity = ref<DeviationSeverity>('LOW')
const description = ref('')
const immediateAction = ref('')
const immediateActionByUserId = ref<string>('')
const immediateActionTime = ref('')
const cause = ref('')
const preventiveMeasures = ref('')
const preventiveResponsibleUserId = ref<string>('')
const preventiveDeadline = ref('')
const status = ref<FoodDeviationStatus>('OPEN')
const errorMessage = ref('')

const deviationTypeOptions: Array<{ value: FoodDeviationType; label: string }> = [
  { value: 'TEMPERATUR', label: 'Temperatur' },
  { value: 'RENHOLD', label: 'Renhold' },
  { value: 'PERSONLIG_HYGIENE', label: 'Personlig hygiene' },
  { value: 'ALLERGEN', label: 'Allergen' },
  { value: 'SKADEDYR', label: 'Skadedyr' },
  { value: 'MOTTAKSKONTROLL', label: 'Mottakskontroll' },
  { value: 'ANNET', label: 'Annet' },
]

const severityOptions: Array<{ value: DeviationSeverity; label: string; sub: string }> = [
  { value: 'LOW', label: 'Lav', sub: 'Ingen helsefare' },
  { value: 'MEDIUM', label: 'Middels', sub: 'Mulig risiko' },
  { value: 'HIGH', label: 'Kritisk', sub: 'Direkte helsefare' },
]

const statusOptions: Array<{ value: FoodDeviationStatus; label: string }> = [
  { value: 'OPEN', label: 'Åpen' },
  { value: 'UNDER_TREATMENT', label: 'Under behandling' },
  { value: 'CLOSED', label: 'Lukket' },
]

const dialogTitle = computed(() => props.mode === 'create' ? 'Registrer matavvik' : 'Rediger matavvik')
const dialogDescription = computed(() =>
  props.mode === 'create'
    ? 'Avvik skal registreres umiddelbart når det oppdages. Dokumentasjonen er lovpålagt iht. IK-matforskriften §5.4 og §5.5.'
    : 'Oppdater informasjonen for valgt avvik',
)
const submitLabel = computed(() => {
  if (props.submitting) return props.mode === 'create' ? 'Registrerer...' : 'Lagrer...'
  return props.mode === 'create' ? 'Registrer avvik' : 'Lagre endringer'
})

watch(
  () => [props.open, props.mode, props.initial],
  () => {
    if (!props.open) return
    if (props.mode === 'edit' && props.initial) {
      const d = props.initial
      const dt = d.reportedAt ? new Date(d.reportedAt) : null
      reportedDate.value = dt ? dt.toISOString().slice(0, 10) : ''
      reportedTime.value = dt ? dt.toISOString().slice(11, 16) : ''
      reportedByUserId.value = ''
      deviationType.value = d.deviationType
      severity.value = d.severity
      description.value = d.description
      immediateAction.value = d.immediateAction ?? ''
      immediateActionByUserId.value = d.immediateActionByUserId ? String(d.immediateActionByUserId) : ''
      immediateActionTime.value = d.immediateActionAt ? new Date(d.immediateActionAt).toISOString().slice(11, 16) : ''
      cause.value = d.cause ?? ''
      preventiveMeasures.value = d.preventiveMeasures ?? ''
      preventiveResponsibleUserId.value = d.preventiveResponsibleUserId ? String(d.preventiveResponsibleUserId) : ''
      preventiveDeadline.value = d.preventiveDeadline ? new Date(d.preventiveDeadline).toISOString().slice(0, 10) : ''
      status.value = d.status
    } else {
      reportedDate.value = ''
      reportedTime.value = ''
      reportedByUserId.value = ''
      deviationType.value = 'TEMPERATUR'
      severity.value = 'LOW'
      description.value = ''
      immediateAction.value = ''
      immediateActionByUserId.value = ''
      immediateActionTime.value = ''
      cause.value = ''
      preventiveMeasures.value = ''
      preventiveResponsibleUserId.value = ''
      preventiveDeadline.value = ''
      status.value = 'OPEN'

      const prefill = props.prefill
      if (prefill?.reportedAt) {
        const reported = new Date(prefill.reportedAt)
        if (!Number.isNaN(reported.getTime())) {
          reportedDate.value = reported.toISOString().slice(0, 10)
          reportedTime.value = reported.toISOString().slice(11, 16)
        }
      }
      if (prefill?.deviationType) deviationType.value = prefill.deviationType
      if (prefill?.severity) severity.value = prefill.severity
      if (prefill?.description) description.value = prefill.description
      if (prefill?.immediateAction) immediateAction.value = prefill.immediateAction
      if (prefill?.immediateActionByUserId) immediateActionByUserId.value = String(prefill.immediateActionByUserId)
      if (prefill?.immediateActionAt) {
        const actionAt = new Date(prefill.immediateActionAt)
        if (!Number.isNaN(actionAt.getTime())) {
          immediateActionTime.value = actionAt.toISOString().slice(11, 16)
        }
      }
      if (prefill?.cause) cause.value = prefill.cause
      if (prefill?.preventiveMeasures) preventiveMeasures.value = prefill.preventiveMeasures
      if (prefill?.preventiveResponsibleUserId) {
        preventiveResponsibleUserId.value = String(prefill.preventiveResponsibleUserId)
      }
      if (prefill?.preventiveDeadline) {
        const deadline = new Date(prefill.preventiveDeadline)
        if (!Number.isNaN(deadline.getTime())) {
          preventiveDeadline.value = deadline.toISOString().slice(0, 10)
        }
      }
    }
    errorMessage.value = ''
  },
)

function buildReportedAt(): string | undefined {
  if (!reportedDate.value) return undefined
  const time = reportedTime.value || '00:00'
  return new Date(`${reportedDate.value}T${time}:00`).toISOString()
}

function buildImmediateActionAt(): string | undefined {
  if (!immediateActionTime.value || !reportedDate.value) return undefined
  return new Date(`${reportedDate.value}T${immediateActionTime.value}:00`).toISOString()
}

function onReporterChange(val: string) { reportedByUserId.value = val }
function onImmediateByChange(val: string) { immediateActionByUserId.value = val }
function onPreventiveByChange(val: string) { preventiveResponsibleUserId.value = val }

function handleSubmit() {
  if (!description.value.trim()) {
    errorMessage.value = 'Beskrivelse er påkrevd.'
    return
  }
  errorMessage.value = ''

  const base: CreateFoodDeviationRequest = {
    deviationType: deviationType.value,
    severity: severity.value,
    description: description.value.trim(),
  }

  const ra = buildReportedAt()
  if (ra) base.reportedAt = ra
  if (immediateAction.value.trim()) base.immediateAction = immediateAction.value.trim()
  if (immediateActionByUserId.value) base.immediateActionByUserId = Number(immediateActionByUserId.value)
  const iat = buildImmediateActionAt()
  if (iat) base.immediateActionAt = iat
  if (cause.value.trim()) base.cause = cause.value.trim()
  if (preventiveMeasures.value.trim()) base.preventiveMeasures = preventiveMeasures.value.trim()
  if (preventiveResponsibleUserId.value) base.preventiveResponsibleUserId = Number(preventiveResponsibleUserId.value)
  if (preventiveDeadline.value) base.preventiveDeadline = new Date(`${preventiveDeadline.value}T00:00:00`).toISOString()

  if (props.mode === 'edit' && props.initial) {
    const updatePayload: UpdateFoodDeviationRequest = { ...base, status: status.value }
    emits('update', { id: props.initial.id, data: updatePayload })
  } else {
    emits('create', base)
  }
}
</script>

<template>
  <component :is="inline ? 'div' : Dialog" v-bind="inline ? {} : { open, 'onUpdate:open': (v: boolean) => emits('update:open', v) }">
    <component :is="inline ? 'div' : DialogContent" :class="inline ? '' : 'food-dialog'">
      <DialogHeader v-if="!inline">
        <DialogTitle>{{ dialogTitle }}</DialogTitle>
        <DialogDescription>{{ dialogDescription }}</DialogDescription>
      </DialogHeader>

      <div v-if="inline" class="description-box">
        <p>{{ dialogDescription }}</p>
      </div>

      <form class="form" @submit.prevent="handleSubmit">
        <!-- Date/time in one row -->
        <div class="row-2">
          <label class="field">
            <span>Dato</span>
            <Input v-model="reportedDate" type="date" />
          </label>
          <label class="field">
            <span>Tidspunkt</span>
            <Input v-model="reportedTime" type="time" />
          </label>
        </div>

        <!-- Reporter below -->
        <div class="field">
          <span>Oppdaget av</span>
          <Select :model-value="reportedByUserId" @update:model-value="onReporterChange">
            <SelectTrigger>
              <SelectValue placeholder="Velg ansatt..." />
            </SelectTrigger>
            <SelectContent>
              <SelectItem v-for="m in members" :key="m.userId" :value="String(m.userId)">{{ m.label }}</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <!-- Deviation type chips -->
        <div class="field">
          <span>Avvikstype</span>
          <div class="chip-grid">
            <button
              v-for="opt in deviationTypeOptions"
              :key="opt.value"
              type="button"
              class="chip"
              :class="{ 'chip--active': deviationType === opt.value }"
              @click="deviationType = opt.value"
            >
              {{ opt.label }}
            </button>
          </div>
        </div>

        <!-- Severity -->
        <div class="field">
          <span>Alvorlighetsgrad</span>
          <div class="segmented-grid segmented-grid--3">
            <button
              v-for="opt in severityOptions"
              :key="opt.value"
              type="button"
              class="segment-button severity-btn"
              :class="[`severity-btn--${opt.value.toLowerCase()}`, { 'segment-button--active': severity === opt.value }]"
              @click="severity = opt.value"
            >
              <strong>{{ opt.label }}</strong>
              <small>{{ opt.sub }}</small>
            </button>
          </div>
        </div>

        <!-- Step 1 -->
        <div class="step-header">Steg 1: Hva skjedde?</div>
        <label class="field">
          <span>Beskriv avviket *</span>
          <Textarea v-model="description" rows="3" placeholder="Hva ble observert? Hvor skjedde det? Hvilke produkter/prosesser var berørt?" />
        </label>

        <!-- Step 2 -->
        <div class="step-header">Steg 2: Umiddelbar handling (§5.4)</div>
        <label class="field">
          <span>Hva ble gjort umiddelbart?</span>
          <Textarea v-model="immediateAction" rows="3" placeholder="F.eks.: Varer kastet, kjøleskap justert, område rengjort..." />
        </label>
        <div class="row-2">
          <div class="field">
            <span>Utført av</span>
            <Select :model-value="immediateActionByUserId" @update:model-value="onImmediateByChange">
              <SelectTrigger>
                <SelectValue placeholder="Velg ansatt..." />
              </SelectTrigger>
              <SelectContent>
                <SelectItem v-for="m in members" :key="m.userId" :value="String(m.userId)">{{ m.label }}</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <label class="field">
            <span>Tidspunkt for handling</span>
            <Input v-model="immediateActionTime" type="time" />
          </label>
        </div>

        <!-- Step 3 -->
        <div class="step-header">Steg 3: Årsaksanalyse og forebygging (§5.5)</div>
        <label class="field">
          <span>Hva var årsaken til avviket?</span>
          <Textarea v-model="cause" rows="3" placeholder="Hvorfor oppstod dette? Var det menneskelig feil, utstyrsfeil, manglende rutine?" />
        </label>
        <label class="field">
          <span>Forebyggende tiltak</span>
          <Textarea v-model="preventiveMeasures" rows="3" placeholder="Hva gjør dere for å hindre at dette skjer igjen?" />
        </label>
        <div class="row-2">
          <div class="field">
            <span>Ansvarlig for oppfølging</span>
            <Select :model-value="preventiveResponsibleUserId" @update:model-value="onPreventiveByChange">
              <SelectTrigger>
                <SelectValue placeholder="Velg ansatt..." />
              </SelectTrigger>
              <SelectContent>
                <SelectItem v-for="m in members" :key="m.userId" :value="String(m.userId)">{{ m.label }}</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <label class="field">
            <span>Frist for gjennomføring</span>
            <Input v-model="preventiveDeadline" type="date" />
          </label>
        </div>

        <!-- Status (edit mode) -->
        <div v-if="mode === 'edit'" class="status-section">
          <span class="status-title">Status og oppfølging</span>
          <div class="status-chips">
            <button
              v-for="opt in statusOptions"
              :key="opt.value"
              type="button"
              class="status-chip"
              :class="{ 'status-chip--active': status === opt.value }"
              @click="status = opt.value"
            >
              {{ opt.label }}
            </button>
          </div>
        </div>

        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

        <div class="form-footer">
          <Button type="button" variant="outline" @click="emits('update:open', false)">Avbryt</Button>
          <Button type="submit" :disabled="submitting">{{ submitLabel }}</Button>
        </div>
      </form>
    </component>
  </component>
</template>

<style scoped>
.food-dialog { max-width: 48rem; max-height: 90vh; overflow-y: auto; display: flex; flex-direction: column; }
.form { display: flex; flex-direction: column; gap: 14px; }
.field { display: flex; flex-direction: column; gap: 6px; }
.field > span { font-size: 0.92rem; font-weight: 600; }

.description-box {
  background: hsl(var(--secondary));
  border-radius: var(--radius-md);
  padding: 14px 16px;
  margin: 4px 0 8px;
}
.description-box p {
  margin: 0;
  font-size: 0.85rem;
  color: hsl(var(--muted-foreground));
  line-height: 1.45;
}

.row-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }

.chip-grid { display: flex; flex-wrap: wrap; gap: 8px; }
.chip {
  border: 1px solid hsl(var(--input));
  background: hsl(var(--card));
  color: hsl(var(--foreground));
  border-radius: var(--radius-pill);
  padding: 8px 16px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 150ms ease;
}
.chip--active {
  border-color: hsl(var(--primary));
  background: hsl(var(--accent));
  color: hsl(var(--accent-foreground));
  font-weight: 700;
}

.segmented-grid { display: grid; gap: 8px; }
.segmented-grid--3 { grid-template-columns: repeat(3, minmax(0, 1fr)); }

.segment-button {
  border: 1px solid hsl(var(--input));
  background: hsl(var(--card));
  color: hsl(var(--foreground));
  border-radius: var(--radius-md);
  padding: 10px 8px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  transition: all 150ms ease;
}
.segment-button strong { font-size: 0.95rem; }
.segment-button small { font-size: 0.78rem; color: hsl(var(--muted-foreground)); }

.severity-btn--low.segment-button--active {
  border-color: #71a66a; background: #dcebd8; color: #2f6f34;
}
.severity-btn--low.segment-button--active small { color: #2f6f34; }
.severity-btn--medium.segment-button--active {
  border-color: #d1a768; background: #f4e6d1; color: #946013;
}
.severity-btn--medium.segment-button--active small { color: #946013; }
.severity-btn--high.segment-button--active {
  border-color: #c95d5d; background: #f4e0e0; color: #902324;
}
.severity-btn--high.segment-button--active small { color: #902324; }

.step-header {
  font-size: 1.05rem;
  font-weight: 600;
  margin-top: 6px;
  color: hsl(var(--foreground));
}

.status-section {
  background: hsl(var(--secondary));
  border-radius: var(--radius-md);
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.status-title { font-weight: 600; font-size: 0.95rem; }
.status-chips { display: flex; gap: 8px; flex-wrap: wrap; }
.status-chip {
  border: 1.5px solid hsl(var(--input));
  background: hsl(var(--card));
  border-radius: var(--radius-pill);
  padding: 6px 14px;
  font-size: 0.88rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 150ms ease;
}
.status-chip--active {
  border-color: hsl(var(--primary));
  background: hsl(var(--accent));
  color: hsl(var(--accent-foreground));
  font-weight: 700;
}

.error-message { color: hsl(var(--destructive)); font-size: 0.86rem; }

.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  padding-top: 4px;
}

@media (max-width: 640px) {
  .row-2 { grid-template-columns: 1fr; }
  .segmented-grid--3 { grid-template-columns: 1fr; }
  .form-footer { flex-direction: column-reverse; }
}
</style>
