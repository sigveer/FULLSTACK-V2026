<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { MoreVertical, Pencil, Plus, Power, PowerOff, Refrigerator, Snowflake, Trash2 } from 'lucide-vue-next'
import AppLayout from '@/components/layout/AppLayout.vue'
import OverviewCard from '@/components/common/OverviewCard.vue'
import Badge from '@/components/ui/badge/Badge.vue'
import Button from '@/components/ui/button/Button.vue'
import AlertDialog from '@/components/ui/alert-dialog/AlertDialog.vue'
import AlertDialogAction from '@/components/ui/alert-dialog/AlertDialogAction.vue'
import AlertDialogCancel from '@/components/ui/alert-dialog/AlertDialogCancel.vue'
import AlertDialogContent from '@/components/ui/alert-dialog/AlertDialogContent.vue'
import AlertDialogDescription from '@/components/ui/alert-dialog/AlertDialogDescription.vue'
import AlertDialogFooter from '@/components/ui/alert-dialog/AlertDialogFooter.vue'
import AlertDialogHeader from '@/components/ui/alert-dialog/AlertDialogHeader.vue'
import AlertDialogTitle from '@/components/ui/alert-dialog/AlertDialogTitle.vue'
import Dialog from '@/components/ui/dialog/Dialog.vue'
import DialogContent from '@/components/ui/dialog/DialogContent.vue'
import DialogDescription from '@/components/ui/dialog/DialogDescription.vue'
import DialogFooter from '@/components/ui/dialog/DialogFooter.vue'
import DialogHeader from '@/components/ui/dialog/DialogHeader.vue'
import DialogTitle from '@/components/ui/dialog/DialogTitle.vue'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import Input from '@/components/ui/input/Input.vue'
import { Separator } from '@/components/ui/separator'
import Select from '@/components/ui/select/Select.vue'
import SelectContent from '@/components/ui/select/SelectContent.vue'
import SelectItem from '@/components/ui/select/SelectItem.vue'
import SelectTrigger from '@/components/ui/select/SelectTrigger.vue'
import SelectValue from '@/components/ui/select/SelectValue.vue'
import { SidebarTrigger } from '@/components/ui/sidebar'
import {
  formatThreshold,
  getDefaultThreshold,
  useTemperatureMonitoring,
} from '@/composables/useTemperatureMonitoring'
import {
  useApplianceSorting,
  type ApplianceGroupOption,
  type ApplianceSortOption,
} from '@/composables/useApplianceSorting'
import type {
  TemperatureAppliance,
  TemperatureApplianceType,
  TemperatureThreshold,
} from '@/types/temperature'

const {
  appliancesWithLastEntry,
  createAppliance,
  updateAppliance,
  deleteAppliance,
} = useTemperatureMonitoring()

const isCreateDialogOpen = ref(false)
const isEditDialogOpen = ref(false)
const editingApplianceId = ref<number | null>(null)
const isDeleteDialogOpen = ref(false)
const appliancePendingDelete = ref<TemperatureAppliance | null>(null)

const createName = ref('')
const createType = ref<TemperatureApplianceType>('FRIDGE')
const createMin = ref<number>(getDefaultThreshold('FRIDGE').min)
const createMax = ref<number>(getDefaultThreshold('FRIDGE').max)

const editName = ref('')
const editType = ref<TemperatureApplianceType>('FRIDGE')
const editMin = ref(0)
const editMax = ref(4)
const {
  sortOption,
  groupOption,
  groupedAppliances,
} = useApplianceSorting(appliancesWithLastEntry)

watch(createType, (nextType) => {
  const defaults = getDefaultThreshold(nextType)
  createMin.value = defaults.min
  createMax.value = defaults.max
})

const activeCount = computed(() => {
  return appliancesWithLastEntry.value.filter((item) => item.isActive).length
})

const inactiveCount = computed(() => {
  return appliancesWithLastEntry.value.length - activeCount.value
})

const withDeviationCount = computed(() => {
  return appliancesWithLastEntry.value.filter((item) => item.lastEntry?.status === 'DEVIATION').length
})

const lastRegisteredLabel = computed(() => {
  const latest = appliancesWithLastEntry.value
    .filter((item) => item.lastEntry)
    .sort((a, b) => {
      const aTime = new Date(a.lastEntry?.measuredAt ?? 0).getTime()
      const bTime = new Date(b.lastEntry?.measuredAt ?? 0).getTime()
      return bTime - aTime
    })[0]

  if (!latest?.lastEntry) {
    return 'Ingen målinger registrert'
  }

  return `Sist registrert: ${toShortDateTime(latest.lastEntry.measuredAt)}`
})

function toTypeLabel(type: TemperatureApplianceType): string {
  return type === 'FRIDGE' ? 'Kjøleskap' : 'Fryser'
}

function toShortDateTime(value: string): string {
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

function openCreateDialog(): void {
  createName.value = ''
  createType.value = 'FRIDGE'
  createMin.value = getDefaultThreshold('FRIDGE').min
  createMax.value = getDefaultThreshold('FRIDGE').max
  isCreateDialogOpen.value = true
}

async function submitCreate(): Promise<void> {
  const name = createName.value.trim()
  if (!name) {
    return
  }

  const threshold: TemperatureThreshold = {
    min: Number(createMin.value),
    max: Number(createMax.value),
  }

  if (threshold.min >= threshold.max) {
    return
  }

  const created = await createAppliance({
    name,
    type: createType.value,
    threshold,
  })

  if (!created) {
    return
  }

  isCreateDialogOpen.value = false
}

function openEditDialog(appliance: TemperatureAppliance): void {
  editingApplianceId.value = appliance.id
  editName.value = appliance.name
  editType.value = appliance.type
  editMin.value = appliance.threshold.min
  editMax.value = appliance.threshold.max
  isEditDialogOpen.value = true
}

async function submitEdit(): Promise<void> {
  if (!editingApplianceId.value) {
    return
  }

  const name = editName.value.trim()
  if (!name) {
    return
  }

  const threshold: TemperatureThreshold = {
    min: Number(editMin.value),
    max: Number(editMax.value),
  }

  if (threshold.min >= threshold.max) {
    return
  }

  const updated = await updateAppliance(editingApplianceId.value, {
    name,
    threshold,
  })

  if (!updated) {
    return
  }

  isEditDialogOpen.value = false
  editingApplianceId.value = null
}

async function toggleActive(appliance: TemperatureAppliance): Promise<void> {
  await updateAppliance(appliance.id, {
    isActive: !appliance.isActive,
  })
}

async function removeApplianceById(applianceId: number): Promise<void> {
  await deleteAppliance(applianceId)
}

function openDeleteDialog(appliance: TemperatureAppliance): void {
  appliancePendingDelete.value = appliance
  isDeleteDialogOpen.value = true
}

async function confirmDeleteAppliance(): Promise<void> {
  if (!appliancePendingDelete.value) {
    return
  }

  await removeApplianceById(appliancePendingDelete.value.id)
  isDeleteDialogOpen.value = false
  appliancePendingDelete.value = null
}
</script>

<template>
  <AppLayout>
    <header class="page-header">
      <div class="page-header-inner">
        <SidebarTrigger />
        <Separator orientation="vertical" class="header-separator" />
        <span class="page-title">Hvitevarer</span>
      </div>
    </header>

    <div class="page-content">
      <section class="page-intro">
        <div>
          <h1>Hvitevarer</h1>
          <p>Legg til, rediger og vedlikehold kjøleskap og frysere for temperaturmåling.</p>
        </div>

        <div>
          <Button @click="openCreateDialog">
            <Plus />
            Ny enhet
          </Button>
        </div>
      </section>

      <section class="overview-grid">
        <OverviewCard label="Aktive enheter" :value="activeCount" variant="resolved" />
        <OverviewCard label="Inaktive enheter" :value="inactiveCount" variant="neutral" />
        <OverviewCard label="Avvik sist målt" :value="withDeviationCount" variant="open" :sub-label="lastRegisteredLabel" />
      </section>

      <section class="controls-row">
        <label class="control-field">
          <span>Sortering</span>
          <Select :model-value="sortOption" @update:model-value="(v) => (sortOption = v as ApplianceSortOption)">
            <SelectTrigger>
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="NAME_ASC">Navn (A-Å)</SelectItem>
              <SelectItem value="NAME_DESC">Navn (Å-A)</SelectItem>
              <SelectItem value="CREATED_NEWEST">Nyeste opprettet</SelectItem>
              <SelectItem value="CREATED_OLDEST">Eldst opprettet</SelectItem>
              <SelectItem value="LAST_MEASURED_NEWEST">Sist målt (nyest)</SelectItem>
              <SelectItem value="LAST_MEASURED_OLDEST">Sist målt (eldst)</SelectItem>
              <SelectItem value="DEVIATION_FIRST">Avvik først</SelectItem>
            </SelectContent>
          </Select>
        </label>

        <label class="control-field">
          <span>Gruppering</span>
          <Select :model-value="groupOption" @update:model-value="(v) => (groupOption = v as ApplianceGroupOption)">
            <SelectTrigger>
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="NONE">Ingen gruppering</SelectItem>
              <SelectItem value="TYPE">Grupper etter type</SelectItem>
              <SelectItem value="STATUS">Grupper etter aktiv/inaktiv</SelectItem>
            </SelectContent>
          </Select>
        </label>
      </section>

      <section class="groups-wrap">
        <section v-for="group in groupedAppliances" :key="group.key" class="group-section">
          <h2 v-if="group.label" class="group-title">{{ group.label }}</h2>

          <div class="device-grid">
            <article
              v-for="item in group.items"
              :key="item.id"
              :class="['device-card', { 'device-card--inactive': !item.isActive }]"
            >
              <div class="device-card-head">
                <div class="device-icon-wrap">
                  <Refrigerator v-if="item.type === 'FRIDGE'" />
                  <Snowflake v-else />
                </div>
                <div>
                  <h3>{{ item.name }}</h3>
                  <p>{{ toTypeLabel(item.type) }}</p>
                </div>
                <Badge :tone="item.isActive ? 'ok' : 'neutral'">
                  {{ item.isActive ? 'Aktiv' : 'Inaktiv' }}
                </Badge>
              </div>

              <dl class="device-facts">
                <div>
                  <dt>Grense</dt>
                  <dd>{{ formatThreshold(item.threshold) }}</dd>
                </div>
                <div>
                  <dt>Siste temp</dt>
                  <dd v-if="item.lastEntry">{{ item.lastEntry.temperature.toFixed(1) }}°C</dd>
                  <dd v-else>-</dd>
                </div>
                <div>
                  <dt>Sist registrert</dt>
                  <dd v-if="item.lastEntry">{{ toShortDateTime(item.lastEntry.measuredAt) }}</dd>
                  <dd v-else>Ingen måling</dd>
                </div>
                <div>
                  <dt>Status</dt>
                  <dd>
                    <Badge v-if="item.lastEntry" :tone="item.lastEntry.status === 'OK' ? 'ok' : 'danger'">
                      {{ item.lastEntry.status === 'OK' ? 'OK' : 'Avvik' }}
                    </Badge>
                    <span v-else class="muted">Ingen data</span>
                  </dd>
                </div>
              </dl>

              <div class="device-actions">
                <DropdownMenu>
                  <DropdownMenuTrigger as-child>
                    <Button variant="ghost" size="icon-sm" class="actions-trigger">
                      <MoreVertical :size="18" />
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end" :side-offset="4">
                    <DropdownMenuItem @click="openEditDialog(item)">
                      <Pencil :size="16" />
                      Rediger
                    </DropdownMenuItem>
                    <DropdownMenuItem @click="toggleActive(item)">
                      <PowerOff v-if="item.isActive" :size="16" />
                      <Power v-else :size="16" />
                      {{ item.isActive ? 'Sett inaktiv' : 'Aktiver' }}
                    </DropdownMenuItem>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem class="menu-item--danger" @click="openDeleteDialog(item)">
                      <Trash2 :size="16" />
                      Slett enhet
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              </div>
            </article>
          </div>
        </section>
      </section>
    </div>

    <AlertDialog v-model:open="isDeleteDialogOpen">
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Slette enhet?</AlertDialogTitle>
          <AlertDialogDescription>
            Er du sikker på at du vil slette {{ appliancePendingDelete?.name }}? Alle tilhørende temperaturregistreringer for denne enheten blir også slettet.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>Avbryt</AlertDialogCancel>
          <AlertDialogAction variant="destructive" @click="confirmDeleteAppliance">
            Slett enhet
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>

    <Dialog :open="isCreateDialogOpen" @update:open="(v) => (isCreateDialogOpen = v)">
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Legg til hvitevare</DialogTitle>
          <DialogDescription>
            Type setter standard grense automatisk. Du kan endre verdiene før lagring.
          </DialogDescription>
        </DialogHeader>

        <div class="form-grid">
          <label class="field">
            <span>Navn</span>
            <Input v-model="createName" placeholder="For eksempel: Kjøleskap kjøkken" />
          </label>

          <label class="field">
            <span>Type</span>
            <Select :model-value="createType" @update:model-value="(v) => (createType = v as TemperatureApplianceType)">
              <SelectTrigger>
                <SelectValue placeholder="Velg type" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="FRIDGE">Kjøleskap</SelectItem>
                <SelectItem value="FREEZER">Fryser</SelectItem>
              </SelectContent>
            </Select>
          </label>

          <label class="field">
            <span>Min temperatur (°C)</span>
            <Input v-model="createMin" type="number" />
          </label>

          <label class="field">
            <span>Maks temperatur (°C)</span>
            <Input v-model="createMax" type="number" />
          </label>
        </div>

        <DialogFooter>
          <Button variant="outline" @click="isCreateDialogOpen = false">Avbryt</Button>
          <Button @click="submitCreate">Lagre enhet</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>

    <Dialog :open="isEditDialogOpen" @update:open="(v) => (isEditDialogOpen = v)">
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Rediger hvitevare</DialogTitle>
          <DialogDescription>
            Oppdater navn og temperaturgrense for valgt enhet.
          </DialogDescription>
        </DialogHeader>

        <div class="form-grid">
          <label class="field">
            <span>Navn</span>
            <Input v-model="editName" placeholder="Navn" />
          </label>

          <label class="field">
            <span>Type</span>
            <Input :model-value="toTypeLabel(editType)" disabled />
          </label>

          <label class="field">
            <span>Min temperatur (°C)</span>
            <Input v-model="editMin" type="number" />
          </label>

          <label class="field">
            <span>Maks temperatur (°C)</span>
            <Input v-model="editMax" type="number" />
          </label>
        </div>

        <DialogFooter>
          <Button variant="outline" @click="isEditDialogOpen = false">Avbryt</Button>
          <Button @click="submitEdit">Lagre endringer</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
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

.page-intro {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
}

.page-intro h1 {
  margin-top: 0;
  font-size: 1.45rem;
  line-height: 1.2;
}

.page-intro p {
  margin-top: 0.375rem;
  color: hsl(var(--muted-foreground));
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0.75rem;
}

.device-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 0.75rem;
}

.controls-row {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.control-field {
  min-width: 220px;
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.control-field span {
  font-size: 0.85rem;
  color: hsl(var(--muted-foreground));
}

.groups-wrap {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.group-section {
  display: flex;
  flex-direction: column;
  gap: 0.65rem;
}

.group-title {
  font-size: 1rem;
  font-weight: 700;
  color: hsl(var(--foreground));
}

.device-card {
  border-radius: 14px;
  border: 1px solid #ddd9d1;
  background: color-mix(in srgb, #ffffff 85%, #f3f2ee 15%);
  backdrop-filter: blur(6px);
  padding: 0.95rem;
}

.device-card--inactive {
  opacity: 0.74;
}

.device-card-head {
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 0.625rem;
}

.device-icon-wrap {
  display: flex;
  height: 2.25rem;
  width: 2.25rem;
  align-items: center;
  justify-content: center;
  border-radius: 0.75rem;
  background: var(--brand-soft);
  color: #423ea8;
}

.device-icon-wrap :deep(svg) {
  width: 1.1rem;
  height: 1.1rem;
}

.device-card-head h3 {
  font-size: 1.05rem;
  line-height: 1.2;
}

.device-card-head p {
  margin-top: 0.125rem;
  font-size: 0.85rem;
  color: hsl(var(--muted-foreground));
}

.device-facts {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.55rem;
  margin-top: 0.85rem;
}

.device-facts dt {
  font-size: 0.73rem;
  text-transform: uppercase;
  letter-spacing: 0.03em;
  color: hsl(var(--muted-foreground));
}

.device-facts dd {
  margin-top: 0.125rem;
  font-size: 0.95rem;
  font-weight: 600;
}

.muted {
  color: hsl(var(--muted-foreground));
  font-size: 0.85rem;
}

.device-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 0.95rem;
}

.actions-trigger {
  margin-left: auto;
}

:deep(.menu-item--danger) {
  color: hsl(var(--destructive));
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.75rem;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.field span {
  font-size: 0.85rem;
  color: hsl(var(--muted-foreground));
}

@media (max-width: 920px) {
  .overview-grid {
    grid-template-columns: 1fr;
  }

  .page-intro {
    flex-direction: column;
  }

  .controls-row {
    flex-direction: column;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
