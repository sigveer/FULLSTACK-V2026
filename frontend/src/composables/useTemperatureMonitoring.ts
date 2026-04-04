import { computed } from 'vue'
import { useStorage } from '@vueuse/core'
import type {
  CreateAppliancePayload,
  RegisterTemperaturePayload,
  TemperatureAppliance,
  TemperatureApplianceType,
  TemperatureApplianceWithLastEntry,
  TemperatureEntry,
  TemperatureEntryStatus,
  TemperatureThreshold,
  UpdateAppliancePayload,
} from '@/types/temperature'

const APPLIANCES_STORAGE_KEY = 'ik.temperature.appliances.v1'
const ENTRIES_STORAGE_KEY = 'ik.temperature.entries.v1'
const DEFAULT_MEASURED_BY = 'Ansatt Ansattsen'

function nowIso(): string {
  return new Date().toISOString()
}

function createId(prefix: string): string {
  return `${prefix}-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`
}

export function getDefaultThreshold(type: TemperatureApplianceType): TemperatureThreshold {
  if (type === 'FRIDGE') {
    return { min: 0, max: 4 }
  }

  return { min: -25, max: -18 }
}

export function evaluateTemperatureStatus(value: number, threshold: TemperatureThreshold): TemperatureEntryStatus {
  if (value < threshold.min || value > threshold.max) {
    return 'DEVIATION'
  }

  return 'OK'
}

export function formatThreshold(threshold: TemperatureThreshold): string {
  return `${threshold.min} til ${threshold.max}°C`
}

const seededAppliances: TemperatureAppliance[] = [
  {
    id: 'appliance-fridge-1',
    name: 'Kjøleskap 1',
    type: 'FRIDGE',
    threshold: getDefaultThreshold('FRIDGE'),
    isActive: true,
    createdAt: nowIso(),
    updatedAt: nowIso(),
  },
  {
    id: 'appliance-freezer-1',
    name: 'Fryser 1',
    type: 'FREEZER',
    threshold: getDefaultThreshold('FREEZER'),
    isActive: true,
    createdAt: nowIso(),
    updatedAt: nowIso(),
  },
]

const appliancesState = useStorage<TemperatureAppliance[]>(APPLIANCES_STORAGE_KEY, seededAppliances)
const entriesState = useStorage<TemperatureEntry[]>(ENTRIES_STORAGE_KEY, [])

export function useTemperatureMonitoring() {
  const appliances = computed(() => appliancesState.value)
  const activeAppliances = computed(() => appliancesState.value.filter((item) => item.isActive))

  const entries = computed(() => {
    return [...entriesState.value].sort((a, b) => {
      return new Date(b.measuredAt).getTime() - new Date(a.measuredAt).getTime()
    })
  })

  const entriesByApplianceId = computed<Record<string, TemperatureEntry[]>>(() => {
    return entries.value.reduce<Record<string, TemperatureEntry[]>>((acc, item) => {
      const existing = acc[item.applianceId]
      if (!existing) {
        acc[item.applianceId] = [item]
        return acc
      }

      existing.push(item)
      return acc
    }, {})
  })

  const appliancesWithLastEntry = computed<TemperatureApplianceWithLastEntry[]>(() => {
    return appliances.value.map((appliance) => {
      const applianceEntries = entriesByApplianceId.value[appliance.id] ?? []
      return {
        ...appliance,
        lastEntry: applianceEntries[0] ?? null,
      }
    })
  })

  function createAppliance(payload: CreateAppliancePayload): TemperatureAppliance {
    const timestamp = nowIso()
    const appliance: TemperatureAppliance = {
      id: createId('appliance'),
      name: payload.name.trim(),
      type: payload.type,
      threshold: payload.threshold ?? getDefaultThreshold(payload.type),
      isActive: true,
      createdAt: timestamp,
      updatedAt: timestamp,
    }

    appliancesState.value = [...appliancesState.value, appliance]
    return appliance
  }

  function updateAppliance(applianceId: string, payload: UpdateAppliancePayload): TemperatureAppliance | null {
    const existing = appliancesState.value.find((item) => item.id === applianceId)
    if (!existing) {
      return null
    }

    const updated: TemperatureAppliance = {
      ...existing,
      name: payload.name?.trim() || existing.name,
      threshold: payload.threshold ?? existing.threshold,
      isActive: payload.isActive ?? existing.isActive,
      updatedAt: nowIso(),
    }

    appliancesState.value = appliancesState.value.map((item) => {
      return item.id === applianceId ? updated : item
    })

    return updated
  }

  function deleteAppliance(applianceId: string): void {
    appliancesState.value = appliancesState.value.filter((item) => item.id !== applianceId)
    entriesState.value = entriesState.value.filter((item) => item.applianceId !== applianceId)
  }

  function registerTemperature(payload: RegisterTemperaturePayload): TemperatureEntry | null {
    const appliance = appliancesState.value.find((item) => item.id === payload.applianceId)
    if (!appliance) {
      return null
    }

    const measuredAt = payload.measuredAt ?? nowIso()
    const status = evaluateTemperatureStatus(payload.temperature, appliance.threshold)

    const entry: TemperatureEntry = {
      id: createId('entry'),
      applianceId: appliance.id,
      measuredAt,
      measuredBy: payload.measuredBy.trim() || DEFAULT_MEASURED_BY,
      temperature: payload.temperature,
      note: payload.note?.trim() ?? '',
      status,
      createdAt: nowIso(),
    }

    entriesState.value = [entry, ...entriesState.value]
    return entry
  }

  return {
    appliances,
    activeAppliances,
    entries,
    appliancesWithLastEntry,
    createAppliance,
    updateAppliance,
    deleteAppliance,
    registerTemperature,
  }
}
