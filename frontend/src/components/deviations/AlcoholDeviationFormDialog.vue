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
  AlcoholDeviation,
  AlcoholReportSource,
  AlcoholDeviationType,
  AlcoholCausalAnalysis,
  AlcoholDeviationStatus,
  CreateAlcoholDeviationRequest,
  UpdateAlcoholDeviationRequest,
  PenaltyPointSummary,
} from '@/types/deviation'

interface MemberOption { userId: number; label: string }

const props = withDefaults(
  defineProps<{
    open: boolean
    mode?: 'create' | 'edit'
    initial?: AlcoholDeviation | null
    submitting?: boolean
    members: MemberOption[]
    penaltySummary?: PenaltyPointSummary | null
    inline?: boolean
  }>(),
  { mode: 'create', initial: null, submitting: false, penaltySummary: null, inline: false },
)

const emits = defineEmits<{
  (e: 'update:open', v: boolean): void
  (e: 'create', payload: CreateAlcoholDeviationRequest): void
  (e: 'update', payload: { id: number; data: UpdateAlcoholDeviationRequest }): void
}>()

const reportedDate = ref('')
const reportedTime = ref('')
const reportedByUserId = ref<string>('')
const reportSource = ref<AlcoholReportSource>('EGENRAPPORT')
const deviationType = ref<AlcoholDeviationType | ''>('')
const description = ref('')
const immediateAction = ref('')
const causalAnalysis = ref<AlcoholCausalAnalysis | ''>('')
const causalExplanation = ref('')
const preventiveMeasures = ref('')
const preventiveDeadline = ref('')
const preventiveResponsibleUserId = ref<string>('')
const status = ref<AlcoholDeviationStatus>('OPEN')
const errorMessage = ref('')

const POINTS_MAP: Record<AlcoholDeviationType, number> = {
  SKJENKING_MINDREAARIGE: 8,
  BRUDD_BISTANDSPLIKT: 8,
  UFORSVARLIG_DRIFT: 8,
  HINDRING_KONTROLL: 8,
  SKJENKING_APENBART_BERUSET: 4,
  BRUDD_SJENKETIDER: 4,
  BRENNEVIN_18_19: 4,
  BERUSET_PERSON_I_LOKALET: 2,
  MANGLER_IK_SYSTEM: 2,
  MANGLER_STYRER_STEDFORTREDER: 2,
  NARKOTIKA: 2,
  ALKOHOLFRI_ALTERNATIV_MANGLER: 1,
  MEDBRAKT_ALKOHOL: 1,
  REKLAMEBRUDD: 1,
  VILKAARSBRUDD: 1,
}

const reportSourceOptions: Array<{ value: AlcoholReportSource; label: string; sub: string }> = [
  { value: 'EGENRAPPORT', label: 'Egenrapport', sub: 'Oppdaget internt' },
  { value: 'SJENKEKONTROLL', label: 'Skjenkekontroll', sub: 'Kommunal kontroll' },
  { value: 'POLITIRAPPORT', label: 'Politirapport', sub: 'Fra politiet' },
]

const deviationTypeGroups = [
  { points: 8, types: [
    { value: 'SKJENKING_MINDREAARIGE' as AlcoholDeviationType, label: 'Skjenking til mindreårige' },
    { value: 'BRUDD_BISTANDSPLIKT' as AlcoholDeviationType, label: 'Brudd på bistandsplikt' },
    { value: 'UFORSVARLIG_DRIFT' as AlcoholDeviationType, label: 'Uforsvarlig drift' },
    { value: 'HINDRING_KONTROLL' as AlcoholDeviationType, label: 'Hindring av kontroll' },
  ]},
  { points: 4, types: [
    { value: 'SKJENKING_APENBART_BERUSET' as AlcoholDeviationType, label: 'Skjenking til åpenbart beruset' },
    { value: 'BRUDD_SJENKETIDER' as AlcoholDeviationType, label: 'Brudd på skjenketider' },
    { value: 'BRENNEVIN_18_19' as AlcoholDeviationType, label: 'Brennevin til 18-19-åringer' },
  ]},
  { points: 2, types: [
    { value: 'BERUSET_PERSON_I_LOKALET' as AlcoholDeviationType, label: 'Beruset person i lokalet' },
    { value: 'MANGLER_IK_SYSTEM' as AlcoholDeviationType, label: 'Mangler IK-system' },
    { value: 'MANGLER_STYRER_STEDFORTREDER' as AlcoholDeviationType, label: 'Mangler styrer/stedfortreder' },
    { value: 'NARKOTIKA' as AlcoholDeviationType, label: 'Narkotika' },
  ]},
  { points: 1, types: [
    { value: 'ALKOHOLFRI_ALTERNATIV_MANGLER' as AlcoholDeviationType, label: 'Alkoholfri alternativ mangler' },
    { value: 'MEDBRAKT_ALKOHOL' as AlcoholDeviationType, label: 'Medbrakt alkohol' },
    { value: 'REKLAMEBRUDD' as AlcoholDeviationType, label: 'Reklamebrudd' },
    { value: 'VILKAARSBRUDD' as AlcoholDeviationType, label: 'Vilkårsbrudd' },
  ]},
]

const allDeviationTypes = deviationTypeGroups.flatMap((g) =>
  g.types.map((t) => ({ ...t, points: g.points })),
)

const causalOptions: Array<{ value: AlcoholCausalAnalysis; label: string }> = [
  { value: 'MANGLENDE_OPPLAERING', label: 'Manglende opplæring' },
  { value: 'RUTINE_IKKE_FULGT', label: 'Rutine ikke fulgt' },
  { value: 'RUTINE_MANGLER', label: 'Rutine mangler' },
  { value: 'HOYT_TRYKK_STRESS', label: 'Høyt trykk / stress' },
  { value: 'UNDERBEMANNING', label: 'Underbemanning' },
  { value: 'KOMMUNIKASJON', label: 'Kommunikasjon' },
  { value: 'ANNET', label: 'Annet' },
]

const statusOptions: Array<{ value: AlcoholDeviationStatus; label: string }> = [
  { value: 'OPEN', label: 'Åpen' },
  { value: 'UNDER_TREATMENT', label: 'Under behandling' },
  { value: 'CLOSED', label: 'Lukket' },
]

const showPenaltyWarning = computed(() => {
  if (!deviationType.value) return false
  return reportSource.value === 'SJENKEKONTROLL' || reportSource.value === 'POLITIRAPPORT'
})

const selectedPoints = computed(() => {
  if (!deviationType.value) return 0
  return POINTS_MAP[deviationType.value as AlcoholDeviationType] ?? 0
})

const currentTotalPoints = computed(() => props.penaltySummary?.totalPoints ?? 0)

const dialogTitle = computed(() => props.mode === 'create' ? 'Registrer alkoholavvik' : 'Rediger alkoholavvik')
const dialogDescription = computed(() =>
  'Registrer alle hendelser — både de som oppdages internt og de som avdekkes ved kommunal kontroll.',
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
      reportSource.value = d.reportSource
      deviationType.value = d.deviationType
      description.value = d.description
      immediateAction.value = d.immediateAction ?? ''
      causalAnalysis.value = d.causalAnalysis ?? ''
      causalExplanation.value = d.causalExplanation ?? ''
      preventiveMeasures.value = d.preventiveMeasures ?? ''
      preventiveDeadline.value = d.preventiveDeadline ? new Date(d.preventiveDeadline).toISOString().slice(0, 10) : ''
      preventiveResponsibleUserId.value = d.preventiveResponsibleUserId ? String(d.preventiveResponsibleUserId) : ''
      status.value = d.status
    } else {
      reportedDate.value = ''
      reportedTime.value = ''
      reportedByUserId.value = ''
      reportSource.value = 'EGENRAPPORT'
      deviationType.value = ''
      description.value = ''
      immediateAction.value = ''
      causalAnalysis.value = ''
      causalExplanation.value = ''
      preventiveMeasures.value = ''
      preventiveDeadline.value = ''
      preventiveResponsibleUserId.value = ''
      status.value = 'OPEN'
    }
    errorMessage.value = ''
  },
)

function onReporterChange(val: string) { reportedByUserId.value = val }
function onDeviationTypeChange(val: string) { deviationType.value = val as AlcoholDeviationType }
function onPreventiveByChange(val: string) { preventiveResponsibleUserId.value = val }

function handleSubmit() {
  if (!deviationType.value) {
    errorMessage.value = 'Velg type hendelse.'
    return
  }
  if (!description.value.trim()) {
    errorMessage.value = 'Beskrivelse er påkrevd.'
    return
  }
  errorMessage.value = ''

  const base: CreateAlcoholDeviationRequest = {
    reportSource: reportSource.value,
    deviationType: deviationType.value as AlcoholDeviationType,
    description: description.value.trim(),
  }

  if (reportedDate.value) {
    const time = reportedTime.value || '00:00'
    base.reportedAt = new Date(`${reportedDate.value}T${time}:00`).toISOString()
  }
  if (immediateAction.value.trim()) base.immediateAction = immediateAction.value.trim()
  if (causalAnalysis.value) base.causalAnalysis = causalAnalysis.value as AlcoholCausalAnalysis
  if (causalExplanation.value.trim()) base.causalExplanation = causalExplanation.value.trim()
  if (preventiveMeasures.value.trim()) base.preventiveMeasures = preventiveMeasures.value.trim()
  if (preventiveDeadline.value) base.preventiveDeadline = new Date(`${preventiveDeadline.value}T00:00:00`).toISOString()
  if (preventiveResponsibleUserId.value) base.preventiveResponsibleUserId = Number(preventiveResponsibleUserId.value)

  if (props.mode === 'edit' && props.initial) {
    emits('update', { id: props.initial.id, data: { ...base, status: status.value } })
  } else {
    emits('create', base)
  }
}
</script>

<template>
  <component :is="inline ? 'div' : Dialog" v-bind="inline ? {} : { open, 'onUpdate:open': (v: boolean) => emits('update:open', v) }">
    <component :is="inline ? 'div' : DialogContent" :class="inline ? '' : 'alcohol-dialog'">
      <DialogHeader v-if="!inline">
        <DialogTitle>{{ dialogTitle }}</DialogTitle>
        <DialogDescription>{{ dialogDescription }}</DialogDescription>
      </DialogHeader>

      <div v-if="inline" class="description-box">
        <p>{{ dialogDescription }}</p>
      </div>

      <form class="form" @submit.prevent="handleSubmit">
        <!-- 1. Grunninfo -->
        <div class="step-header">1. Grunninfo</div>
        <div class="row-2">
          <label class="field">
            <span>Dato for hendelse</span>
            <Input v-model="reportedDate" type="date" />
          </label>
          <label class="field">
            <span>Tidspunkt (ca.)</span>
            <Input v-model="reportedTime" type="time" />
          </label>
        </div>
        <div class="field">
          <span>Hvem rapporterer?</span>
          <Select :model-value="reportedByUserId" @update:model-value="onReporterChange">
            <SelectTrigger>
              <SelectValue placeholder="Velg ansatt..." />
            </SelectTrigger>
            <SelectContent>
              <SelectItem v-for="m in members" :key="m.userId" :value="String(m.userId)">{{ m.label }}</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <!-- Source -->
        <div class="field">
          <span>Kilde</span>
          <div class="segmented-grid segmented-grid--3">
            <button
              v-for="opt in reportSourceOptions"
              :key="opt.value"
              type="button"
              class="segment-button source-btn"
              :class="{ 'segment-button--active': reportSource === opt.value }"
              @click="reportSource = opt.value"
            >
              <strong>{{ opt.label }}</strong>
              <small>{{ opt.sub }}</small>
            </button>
          </div>
        </div>

        <!-- 2. Hva skjedde -->
        <div class="step-header">2. Hva skjedde?</div>
        <div class="field">
          <span>Type hendelse</span>
          <Select :model-value="deviationType" @update:model-value="onDeviationTypeChange">
            <SelectTrigger>
              <SelectValue placeholder="Velg kategori..." />
            </SelectTrigger>
            <SelectContent>
              <SelectItem v-for="t in allDeviationTypes" :key="t.value" :value="t.value">
                {{ t.label }} ({{ t.points }}p)
              </SelectItem>
            </SelectContent>
          </Select>
        </div>

        <!-- Penalty warning -->
        <div v-if="showPenaltyWarning && deviationType" class="penalty-warning">
          <div class="penalty-warning-header">
            <strong class="penalty-points">{{ selectedPoints }}</strong>
            <div>
              <strong>prikker ved {{ reportSource === 'SJENKEKONTROLL' ? 'kommunal kontroll' : 'politirapport' }}</strong>
              <p>Dere har nå {{ currentTotalPoints }} prikker. Dette avviket ville bringe dere til {{ currentTotalPoints + selectedPoints }} {{ currentTotalPoints + selectedPoints >= 12 ? '= inndragning' : '' }}</p>
            </div>
          </div>
        </div>

        <label class="field">
          <span>Beskriv hendelsen i detalj</span>
          <Textarea v-model="description" rows="3" placeholder="Hva skjedde? Hvem var involvert? Hvor i lokalet? Var det vitner?" />
        </label>

        <!-- 3. Umiddelbar handling -->
        <div class="step-header">3. Umiddelbar handling</div>
        <label class="field">
          <span>Hva ble gjort med en gang for å håndtere situasjonen?</span>
          <Textarea v-model="immediateAction" rows="3" placeholder="F.eks.: Stoppet servering, sjekket ID, tilkalte vekter, ringte taxi..." />
        </label>

        <!-- 4. Årsaksanalyse -->
        <div class="step-header">4. Årsaksanalyse</div>
        <div class="field">
          <span>Hvorfor skjedde dette? Hva sviktet?</span>
          <div class="chip-grid">
            <button
              v-for="opt in causalOptions"
              :key="opt.value"
              type="button"
              class="chip"
              :class="{ 'chip--active': causalAnalysis === opt.value }"
              @click="causalAnalysis = causalAnalysis === opt.value ? '' : opt.value"
            >
              {{ opt.label }}
            </button>
          </div>
        </div>
        <label class="field">
          <span>Utdyp: Hva er den egentlige årsaken?</span>
          <Textarea v-model="causalExplanation" rows="2" placeholder="Utdyp: Hva er den egentlige årsaken?" />
        </label>

        <!-- 5. Forebyggende tiltak -->
        <div class="step-header">5. Forebyggende tiltak</div>
        <label class="field">
          <span>Hva skal gjøres for å hindre at dette skjer igjen?</span>
          <Textarea v-model="preventiveMeasures" rows="3" placeholder="F.eks.: Oppdatere rutine, gjennomføre ny opplæring, endre bemanning..." />
        </label>
        <div class="row-2">
          <div class="field">
            <span>Ansvarlig for oppfølging</span>
            <Select :model-value="preventiveResponsibleUserId" @update:model-value="onPreventiveByChange">
              <SelectTrigger>
                <SelectValue placeholder="Velg person..." />
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
          <span class="status-title">Status</span>
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
.alcohol-dialog { max-width: 48rem; max-height: 90vh; overflow-y: auto; display: flex; flex-direction: column; }
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

.step-header {
  font-size: 1.05rem;
  font-weight: 700;
  margin-top: 6px;
  color: hsl(var(--foreground));
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

.source-btn.segment-button--active {
  border-color: hsl(var(--primary));
  background: hsl(var(--accent));
  color: hsl(var(--accent-foreground));
}

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

.penalty-warning {
  background: #fde8e8;
  border-left: 4px solid #c62828;
  border-radius: var(--radius-md);
  padding: 12px 14px;
}
.penalty-warning-header {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}
.penalty-points {
  font-size: 1.5rem;
  color: #c62828;
  line-height: 1;
}
.penalty-warning strong { color: #7f1d1d; font-size: 0.9rem; }
.penalty-warning p { margin: 2px 0 0; color: #991b1b; font-size: 0.82rem; }

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
