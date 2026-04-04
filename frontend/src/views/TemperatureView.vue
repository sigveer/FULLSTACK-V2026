<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { Thermometer, TriangleAlert } from 'lucide-vue-next'
import AppLayout from '@/components/layout/AppLayout.vue'
import OverviewCard from '@/components/common/OverviewCard.vue'
import Badge from '@/components/ui/badge/Badge.vue'
import Button from '@/components/ui/button/Button.vue'
import Checkbox from '@/components/ui/checkbox/Checkbox.vue'
import Input from '@/components/ui/input/Input.vue'
import { Separator } from '@/components/ui/separator'
import Select from '@/components/ui/select/Select.vue'
import SelectContent from '@/components/ui/select/SelectContent.vue'
import SelectItem from '@/components/ui/select/SelectItem.vue'
import SelectTrigger from '@/components/ui/select/SelectTrigger.vue'
import SelectValue from '@/components/ui/select/SelectValue.vue'
import { SidebarTrigger } from '@/components/ui/sidebar'
import {
  evaluateTemperatureStatus,
  formatThreshold,
  useTemperatureMonitoring,
} from '@/composables/useTemperatureMonitoring'
import { useTemperatureRegistration } from '@/composables/useTemperatureRegistration'
import { useAuthStore } from '@/stores/auth'
import { Table, TableBody, TableCell, TableEmpty, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { toast } from 'vue-sonner'

const auth = useAuthStore()
const { activeAppliances, appliances, entries, deleteEntries } = useTemperatureMonitoring()
const { registerTemperatureWithDeviation } = useTemperatureRegistration()

const selectedApplianceId = ref<number | null>(null)
const temperatureInput = ref('')
const note = ref('')
const selectedEntryIds = ref<number[]>([])

watch(
  activeAppliances,
  (list) => {
    if (!selectedApplianceId.value && list[0]) {
      selectedApplianceId.value = list[0].id
    }

    if (selectedApplianceId.value != null && !list.some((item) => item.id === selectedApplianceId.value)) {
      selectedApplianceId.value = list[0]?.id ?? null
    }
  },
  { immediate: true },
)

const selectedAppliance = computed(() => {
  if (selectedApplianceId.value == null) {
    return null
  }

  return activeAppliances.value.find((item) => item.id === selectedApplianceId.value) ?? null
})

const recentEntries = computed(() => {
  return entries.value.map((entry) => {
    const appliance = appliances.value.find((item) => item.id === entry.applianceId)
    return {
      ...entry,
      applianceName: appliance?.name ?? 'Ukjent enhet',
      applianceType: appliance?.type ?? 'FRIDGE',
      threshold: appliance ? formatThreshold(appliance.threshold) : '-',
      statusTone: entry.status === 'OK' ? ('ok' as const) : ('danger' as const),
    }
  })
})

const allRowsSelected = computed(() => {
  return recentEntries.value.length > 0 && recentEntries.value.every((entry) => selectedEntryIds.value.includes(entry.id))
})

const expectedMeasurementsToday = computed(() => activeAppliances.value.length * 2)

const measurementsToday = computed(() => {
  const today = new Date()
  const year = today.getFullYear()
  const month = today.getMonth()
  const day = today.getDate()

  return entries.value.filter((entry) => {
    const measured = new Date(entry.measuredAt)
    return measured.getFullYear() === year
      && measured.getMonth() === month
      && measured.getDate() === day
  })
})

const measurementsTodayCount = computed(() => measurementsToday.value.length)

const completedUnitsToday = computed(() => {
  const countsByAppliance = measurementsToday.value.reduce<Record<string, number>>((acc, entry) => {
    acc[entry.applianceId] = (acc[entry.applianceId] ?? 0) + 1
    return acc
  }, {})

  return activeAppliances.value.filter((appliance) => {
    return (countsByAppliance[appliance.id] ?? 0) >= 2
  }).length
})

const completedUnitsTarget = computed(() => activeAppliances.value.length)

function progressVariant(done: number, total: number): 'open' | 'in-progress' | 'resolved' | 'neutral' {
  if (total <= 0) {
    return 'neutral'
  }

  if (done <= 0) {
    return 'open'
  }

  if (done >= total) {
    return 'resolved'
  }

  return 'in-progress'
}

const measurementsTodayVariant = computed(() => {
  return progressVariant(measurementsTodayCount.value, expectedMeasurementsToday.value)
})

const completedUnitsVariant = computed(() => {
  return progressVariant(completedUnitsToday.value, completedUnitsTarget.value)
})

watch(recentEntries, (rows) => {
  const validIds = new Set(rows.map((row) => row.id))
  selectedEntryIds.value = selectedEntryIds.value.filter((id) => validIds.has(id))
})

const deviationCount = computed(() => entries.value.filter((item) => item.status === 'DEVIATION').length)
const latestEntry = computed(() => entries.value[0] ?? null)

const formStatus = computed(() => {
  if (!selectedAppliance.value) {
    return null
  }

  const value = Number(temperatureInput.value)
  if (Number.isNaN(value)) {
    return null
  }

  return evaluateTemperatureStatus(value, selectedAppliance.value.threshold)
})

function formatDateTime(value: string): string {
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return '-'
  }

  return new Intl.DateTimeFormat('nb-NO', {
    day: '2-digit',
    month: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  }).format(date)
}

async function submitTemperature(): Promise<void> {
  if (!selectedAppliance.value) {
    return
  }

  const temperature = Number(temperatureInput.value)
  if (Number.isNaN(temperature)) {
    return
  }

  const result = await registerTemperatureWithDeviation({
    applianceId: selectedAppliance.value.id,
    temperature,
    note: note.value,
  })

  if (!result.entry) {
    return
  }

  if (result.deviationFailed) {
    toast.warning('Temperatur ble lagret, men avvik kunne ikke opprettes automatisk.')
  }

  temperatureInput.value = ''
  note.value = ''
}

function toggleEntrySelection(entryId: number, checked: boolean): void {
  if (checked) {
    if (!selectedEntryIds.value.includes(entryId)) {
      selectedEntryIds.value = [...selectedEntryIds.value, entryId]
    }
    return
  }

  selectedEntryIds.value = selectedEntryIds.value.filter((id) => id !== entryId)
}

function toggleSelectAll(checked: boolean): void {
  if (!checked) {
    selectedEntryIds.value = []
    return
  }

  selectedEntryIds.value = recentEntries.value.map((entry) => entry.id)
}

async function deleteSelectedMeasurements(): Promise<void> {
  const deletedCount = await deleteEntries(selectedEntryIds.value)
  selectedEntryIds.value = []

  if (deletedCount > 0) {
    toast.success(`${deletedCount} måling${deletedCount > 1 ? 'er' : ''} slettet`)
  }
}
</script>

<template>
  <AppLayout>
    <header class="page-header">
      <div class="page-header-inner">
        <SidebarTrigger />
        <Separator orientation="vertical" class="header-separator" />
        <span class="page-title">Temperaturmåling</span>
      </div>
    </header>

    <div class="page-content">
      <section class="page-intro">
        <Badge tone="brand">IK-Mat</Badge>
        <h1>Temperaturmåling</h1>
        <p>Manuell registrering av temperaturer på kjøleskap og frysere.</p>
      </section>

      <section class="overview-grid">
        <OverviewCard
          label="Målinger i dag"
          :value="`${measurementsTodayCount}/${expectedMeasurementsToday}`"
          :variant="measurementsTodayVariant"
          sub-label="Mål: 2 målinger per aktiv enhet"
        />
        <OverviewCard
          label="Ferdigmålte enheter"
          :value="`${completedUnitsToday}/${completedUnitsTarget}`"
          :variant="completedUnitsVariant"
          sub-label="En enhet er ferdig når den har 2 målinger i dag"
        />
        <OverviewCard label="Avvik registrert" :value="deviationCount" variant="open" />
        <OverviewCard
          label="Siste måling"
          :value="latestEntry ? `${latestEntry.temperature.toFixed(1)}°C` : '-'"
          :sub-label="latestEntry ? `${formatDateTime(latestEntry.measuredAt)} • ${latestEntry.measuredBy}` : 'Ingen målinger ennå'"
          :value-class="latestEntry?.status === 'DEVIATION' ? 'text-red-600' : 'text-emerald-700'"
        />
      </section>

      <section class="workspace-grid">
        <article class="form-panel">
          <div class="panel-header">
            <div>
              <Badge tone="ok">Ny registrering</Badge>
              <h2>Velg enhet og logg temp</h2>
              <p>Bruk samme liste av registrerte enheter hver gang, så slipper du manuell tekstinnskriving.</p>
            </div>
          </div>

          <div v-if="activeAppliances.length === 0" class="empty-warning">
            <TriangleAlert />
            <div>
              <strong>Ingen aktive enheter</strong>
              <p>Legg til eller aktiver kjøleskap og frysere før du registrerer målinger.</p>
            </div>
          </div>

          <div class="form-grid">
            <label class="field field--full">
              <span>Hvitevare</span>
              <Select :model-value="selectedApplianceId == null ? '' : String(selectedApplianceId)" @update:model-value="(v) => (selectedApplianceId = Number(v))">
                <SelectTrigger :disabled="activeAppliances.length === 0">
                  <SelectValue placeholder="Velg enhet" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem v-for="item in activeAppliances" :key="item.id" :value="String(item.id)">
                    {{ item.name }} · {{ item.type === 'FRIDGE' ? 'Kjøleskap' : 'Fryser' }}
                  </SelectItem>
                </SelectContent>
              </Select>
            </label>

            <label class="field">
              <span>Temperatur (°C)</span>
              <Input v-model="temperatureInput" type="number" step="0.1" placeholder="Skriv inn målt verdi" />
            </label>

            <label class="field">
              <span>Registrert av</span>
              <Input :model-value="auth.user?.fullName ?? 'Innlogget bruker'" disabled />
            </label>

            <label class="field field--full">
              <span>Merknad</span>
              <Input v-model="note" placeholder="Valgfri kommentar, for eksempel årsak til avvik" />
            </label>
          </div>

          <div class="status-strip" v-if="selectedAppliance">
            <div>
              <span>Standardgrense</span>
              <strong>{{ formatThreshold(selectedAppliance.threshold) }}</strong>
            </div>
            <div>
              <span>Forventet status</span>
              <Badge :tone="formStatus === 'DEVIATION' ? 'danger' : 'ok'">
                {{ formStatus === 'DEVIATION' ? 'Avvik' : 'OK' }}
              </Badge>
            </div>
          </div>

          <Button class="submit-btn" :disabled="activeAppliances.length === 0 || !selectedAppliance || !temperatureInput" @click="submitTemperature">
            <Thermometer />
            Lagre temperatur
          </Button>
        </article>

        <article class="log-panel">
          <div class="panel-header">
            <div>
              <Badge tone="neutral">Logg</Badge>
              <h2>Siste registreringer</h2>
              <p>En oversikt som viser temp, grense, ansvarlig og om målingen ga avvik.</p>
            </div>
            <Button variant="outline" size="sm" :disabled="selectedEntryIds.length === 0" @click="deleteSelectedMeasurements">
              Slett valgte ({{ selectedEntryIds.length }})
            </Button>
          </div>

          <div class="log-table-scroll">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>
                    <Checkbox :checked="allRowsSelected" @update:checked="toggleSelectAll" />
                  </TableHead>
                  <TableHead>Tidspunkt</TableHead>
                  <TableHead>Enhet</TableHead>
                  <TableHead>Temp</TableHead>
                  <TableHead>Grense</TableHead>
                  <TableHead>Registrert av</TableHead>
                  <TableHead>Status</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <TableEmpty v-if="recentEntries.length === 0" :colspan="7">
                  <div class="table-empty-content">
                    <Thermometer />
                    <div>
                      <strong>Ingen temperaturregistreringer enda</strong>
                      <p>Registrer den første målingen i panelet til venstre.</p>
                    </div>
                  </div>
                </TableEmpty>

                <TableRow v-for="entry in recentEntries" :key="entry.id" :class="entry.status === 'DEVIATION' ? 'row--deviation' : ''">
                  <TableCell>
                    <Checkbox
                      :checked="selectedEntryIds.includes(entry.id)"
                      @update:checked="(checked) => toggleEntrySelection(entry.id, checked)"
                    />
                  </TableCell>
                  <TableCell>
                    <div class="cell-stack">
                      <strong>{{ formatDateTime(entry.measuredAt) }}</strong>
                      <span>{{ entry.note || 'Måling registrert' }}</span>
                    </div>
                  </TableCell>
                  <TableCell>
                    <div class="cell-stack">
                      <strong>{{ entry.applianceName }}</strong>
                      <span>{{ entry.applianceType === 'FRIDGE' ? 'Kjøleskap' : 'Fryser' }}</span>
                    </div>
                  </TableCell>
                  <TableCell>
                    <strong :class="entry.status === 'DEVIATION' ? 'danger-text' : 'ok-text'">{{ entry.temperature.toFixed(1) }}°C</strong>
                  </TableCell>
                  <TableCell>{{ entry.threshold }}</TableCell>
                  <TableCell>{{ entry.measuredBy }}</TableCell>
                  <TableCell>
                    <Badge :tone="entry.statusTone">{{ entry.status === 'OK' ? 'OK' : 'Avvik' }}</Badge>
                  </TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </div>
        </article>
      </section>
    </div>
  </AppLayout>
</template>

<style scoped>
.page-header {
  display: flex;
  height: 4rem;
  flex-shrink: 0;
  align-items: center;
}

.page-header-inner {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0 1rem;
}

.header-separator {
  height: 1rem !important;
  width: 1px !important;
  margin-right: 0.5rem;
}

.page-title {
  font-weight: 500;
  color: hsl(var(--sidebar-primary, 245 43% 52%));
}

.page-content {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 1rem;
  padding: 0 1rem 1rem;
}

.form-panel,
.log-panel {
  border-radius: 14px;
  border: 1px solid hsl(var(--border));
  background: hsl(var(--card));
  box-shadow: 0 1px 2px rgb(0 0 0 / 0.04);
}

.page-intro h1 {
  margin-top: 0.5rem;
  font-size: 1.5rem;
  line-height: 1.2;
}

.page-intro p {
  margin-top: 0.25rem;
  color: hsl(var(--muted-foreground));
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 0.75rem;
}

.workspace-grid {
  display: grid;
  grid-template-columns: 0.9fr 1.1fr;
  gap: 0.75rem;
  align-items: start;
}

.form-panel,
.log-panel {
  padding: 1rem;
}

.log-panel {
  height: 680px;
  display: flex;
  flex-direction: column;
}

.panel-header h2 {
  margin-top: 0.5rem;
  font-size: 1.25rem;
}

.panel-header p {
  margin-top: 0.25rem;
  color: hsl(var(--muted-foreground));
}

.panel-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 0.75rem;
  flex-shrink: 0;
}

.log-table-scroll {
  margin-top: 0.75rem;
  overflow: auto;
  flex: 1;
  min-height: 0;
}

.empty-warning {
  display: flex;
  gap: 0.75rem;
  margin-top: 0.9rem;
  border-radius: 12px;
  border: 1px solid color-mix(in srgb, var(--amber-soft) 65%, #8e5713 35%);
  background: #f8edd6;
  padding: 0.75rem;
}

.empty-warning :deep(svg) {
  width: 1.1rem;
  height: 1.1rem;
  color: #8e5713;
  flex-shrink: 0;
  margin-top: 0.1rem;
}

.empty-warning strong {
  display: block;
  color: #7a4a0d;
}

.empty-warning p {
  margin-top: 0.15rem;
  color: #7a4a0d;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.75rem;
  margin-top: 0.9rem;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.field--full {
  grid-column: 1 / -1;
}

.field span {
  font-size: 0.85rem;
  color: hsl(var(--muted-foreground));
}

.status-strip {
  display: flex;
  justify-content: space-between;
  gap: 0.75rem;
  margin-top: 0.9rem;
  border-radius: 12px;
  background: hsl(var(--muted) / 0.4);
  padding: 0.8rem 0.9rem;
}

.status-strip span {
  display: block;
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.03em;
  color: hsl(var(--muted-foreground));
}

.status-strip strong {
  margin-top: 0.2rem;
  display: block;
}

.submit-btn {
  margin-top: 0.9rem;
}

.submit-btn :deep(svg) {
  width: 1rem;
  height: 1rem;
}

.table-empty-content {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 0;
}

.table-empty-content :deep(svg) {
  width: 1.25rem;
  height: 1.25rem;
  color: hsl(var(--primary));
}

.table-empty-content p {
  color: hsl(var(--muted-foreground));
  margin-top: 0.15rem;
}

.cell-stack {
  display: flex;
  flex-direction: column;
  gap: 0.125rem;
}

.cell-stack span {
  font-size: 0.8rem;
  color: hsl(var(--muted-foreground));
}

.row--deviation {
  background: #fdf0f0;
}

.danger-text {
  color: #ae2c2d;
}

.ok-text {
  color: #1a7a4f;
}

@media (max-width: 1080px) {
  .overview-grid,
  .workspace-grid {
    grid-template-columns: 1fr;
  }

  .log-panel {
    height: 560px;
  }
}

@media (max-width: 720px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
