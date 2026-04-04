import { computed } from 'vue'
import { useMutation, useQuery, useQueryClient } from '@tanstack/vue-query'
import api from '@/lib/api'
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

interface TemperatureApplianceApi {
  id: number
  organizationId: number
  name: string
  applianceType: TemperatureApplianceType
  minTemperature: number
  maxTemperature: number
  isActive: boolean
  createdAt: string
  updatedAt: string
  lastMeasurement: TemperatureMeasurementApi | null
}

interface TemperatureMeasurementApi {
  id: number
  organizationId: number
  applianceId: number
  applianceName: string
  applianceType: TemperatureApplianceType
  measuredByUserId: number
  measuredByUserName: string
  measuredAt: string
  temperature: number
  note: string | null
  status: TemperatureEntryStatus
  createdAt: string
}

const temperatureAppliancesQueryKey = ['temperature-appliances']
const temperatureMeasurementsQueryKey = ['temperature-measurements']

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

export function isValidThreshold(threshold: TemperatureThreshold): boolean {
  return Number.isFinite(threshold.min) && Number.isFinite(threshold.max) && threshold.min < threshold.max
}

export function formatThreshold(threshold: TemperatureThreshold): string {
  return `${threshold.min} til ${threshold.max}°C`
}

function mapAppliance(apiItem: TemperatureApplianceApi): TemperatureAppliance {
  return {
    id: apiItem.id,
    name: apiItem.name,
    type: apiItem.applianceType,
    threshold: {
      min: Number(apiItem.minTemperature),
      max: Number(apiItem.maxTemperature),
    },
    isActive: apiItem.isActive,
    createdAt: apiItem.createdAt,
    updatedAt: apiItem.updatedAt,
  }
}

function mapMeasurement(apiItem: TemperatureMeasurementApi): TemperatureEntry {
  return {
    id: apiItem.id,
    applianceId: apiItem.applianceId,
    measuredAt: apiItem.measuredAt,
    measuredBy: apiItem.measuredByUserName,
    temperature: Number(apiItem.temperature),
    note: apiItem.note ?? '',
    status: apiItem.status,
    createdAt: apiItem.createdAt,
  }
}

export function useTemperatureMonitoring() {
  const qc = useQueryClient()

  const appliancesQuery = useQuery({
    queryKey: temperatureAppliancesQueryKey,
    queryFn: () => api.get<TemperatureApplianceApi[]>('/temperature/appliances').then((r) => r.data),
  })

  const measurementsQuery = useQuery({
    queryKey: temperatureMeasurementsQueryKey,
    queryFn: () => api.get<TemperatureMeasurementApi[]>('/temperature/measurements').then((r) => r.data),
  })

  const createApplianceMutation = useMutation({
    mutationFn: (payload: CreateAppliancePayload) => {
      return api.post<TemperatureApplianceApi>('/temperature/appliances', {
        name: payload.name,
        applianceType: payload.type,
        minTemperature: payload.threshold?.min ?? getDefaultThreshold(payload.type).min,
        maxTemperature: payload.threshold?.max ?? getDefaultThreshold(payload.type).max,
      }).then((r) => r.data)
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: temperatureAppliancesQueryKey }),
  })

  const updateApplianceMutation = useMutation({
    mutationFn: ({ applianceId, payload }: { applianceId: number; payload: UpdateAppliancePayload }) => {
      return api.patch<TemperatureApplianceApi>(`/temperature/appliances/${applianceId}`, {
        name: payload.name,
        minTemperature: payload.threshold?.min,
        maxTemperature: payload.threshold?.max,
        isActive: payload.isActive,
      }).then((r) => r.data)
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: temperatureAppliancesQueryKey }),
  })

  const deleteApplianceMutation = useMutation({
    mutationFn: (applianceId: number) => api.delete(`/temperature/appliances/${applianceId}`),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: temperatureAppliancesQueryKey })
      qc.invalidateQueries({ queryKey: temperatureMeasurementsQueryKey })
    },
  })

  const registerMeasurementMutation = useMutation({
    mutationFn: (payload: RegisterTemperaturePayload) => {
      return api.post<TemperatureMeasurementApi>('/temperature/measurements', {
        applianceId: payload.applianceId,
        temperature: payload.temperature,
        measuredAt: payload.measuredAt,
        note: payload.note,
      }).then((r) => r.data)
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: temperatureMeasurementsQueryKey }),
  })

  const deleteMeasurementsMutation = useMutation({
    mutationFn: (ids: number[]) => api.delete<{ deleted: number }>('/temperature/measurements', { data: { ids } }).then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: temperatureMeasurementsQueryKey }),
  })

  const appliances = computed<TemperatureAppliance[]>(() => {
    return (appliancesQuery.data.value ?? []).map(mapAppliance)
  })

  const activeAppliances = computed(() => appliances.value.filter((item) => item.isActive))

  const entries = computed<TemperatureEntry[]>(() => {
    return (measurementsQuery.data.value ?? []).map(mapMeasurement)
  })

  const entriesByApplianceId = computed<Record<number, TemperatureEntry[]>>(() => {
    return entries.value.reduce<Record<number, TemperatureEntry[]>>((acc, item) => {
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

  async function createAppliance(payload: CreateAppliancePayload): Promise<TemperatureAppliance | null> {
    const normalizedName = payload.name.trim()
    const threshold = payload.threshold ?? getDefaultThreshold(payload.type)

    if (!normalizedName || !isValidThreshold(threshold)) {
      return null
    }

    const result = await createApplianceMutation.mutateAsync({
      ...payload,
      name: normalizedName,
      threshold,
    })

    return mapAppliance(result)
  }

  async function updateAppliance(applianceId: number, payload: UpdateAppliancePayload): Promise<TemperatureAppliance | null> {
    const existing = appliances.value.find((item) => item.id === applianceId)
    if (!existing) {
      return null
    }

    const nextThreshold = payload.threshold ?? existing.threshold
    if (!isValidThreshold(nextThreshold)) {
      return null
    }

    const result = await updateApplianceMutation.mutateAsync({
      applianceId,
      payload: {
        ...payload,
        threshold: nextThreshold,
      },
    })

    return mapAppliance(result)
  }

  async function deleteAppliance(applianceId: number): Promise<void> {
    await deleteApplianceMutation.mutateAsync(applianceId)
  }

  async function registerTemperature(payload: RegisterTemperaturePayload): Promise<TemperatureEntry | null> {
    const appliance = appliances.value.find((item) => item.id === payload.applianceId)
    if (!appliance) {
      return null
    }

    const result = await registerMeasurementMutation.mutateAsync(payload)
    return mapMeasurement(result)
  }

  async function deleteEntries(entryIds: number[]): Promise<number> {
    if (entryIds.length === 0) {
      return 0
    }

    const result = await deleteMeasurementsMutation.mutateAsync(entryIds)
    return result.deleted ?? 0
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
    deleteEntries,
  }
}
