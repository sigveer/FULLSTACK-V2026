import { computed, ref } from 'vue'
import type { ComputedRef } from 'vue'
import type { TemperatureApplianceWithLastEntry } from '@/types/temperature'

export type ApplianceSortOption =
  | 'NAME_ASC'
  | 'NAME_DESC'
  | 'CREATED_NEWEST'
  | 'CREATED_OLDEST'
  | 'LAST_MEASURED_NEWEST'
  | 'LAST_MEASURED_OLDEST'
  | 'DEVIATION_FIRST'

export type ApplianceGroupOption = 'NONE' | 'TYPE' | 'STATUS'

export interface ApplianceGroup {
  key: string
  label: string
  items: TemperatureApplianceWithLastEntry[]
}

export function useApplianceSorting(appliances: ComputedRef<TemperatureApplianceWithLastEntry[]>) {
  const sortOption = ref<ApplianceSortOption>('NAME_ASC')
  const groupOption = ref<ApplianceGroupOption>('TYPE')

  const sortedAppliances = computed(() => {
    const list = [...appliances.value]

    return list.sort((a, b) => {
      if (sortOption.value === 'NAME_ASC') {
        return a.name.localeCompare(b.name, 'nb-NO')
      }

      if (sortOption.value === 'NAME_DESC') {
        return b.name.localeCompare(a.name, 'nb-NO')
      }

      if (sortOption.value === 'CREATED_NEWEST') {
        return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
      }

      if (sortOption.value === 'CREATED_OLDEST') {
        return new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
      }

      if (sortOption.value === 'LAST_MEASURED_NEWEST') {
        const aTime = new Date(a.lastEntry?.measuredAt ?? 0).getTime()
        const bTime = new Date(b.lastEntry?.measuredAt ?? 0).getTime()
        return bTime - aTime
      }

      if (sortOption.value === 'LAST_MEASURED_OLDEST') {
        const aTime = new Date(a.lastEntry?.measuredAt ?? 0).getTime()
        const bTime = new Date(b.lastEntry?.measuredAt ?? 0).getTime()
        return aTime - bTime
      }

      const aDeviation = a.lastEntry?.status === 'DEVIATION' ? 1 : 0
      const bDeviation = b.lastEntry?.status === 'DEVIATION' ? 1 : 0
      if (bDeviation !== aDeviation) {
        return bDeviation - aDeviation
      }

      return a.name.localeCompare(b.name, 'nb-NO')
    })
  })

  const groupedAppliances = computed<ApplianceGroup[]>(() => {
    if (groupOption.value === 'NONE') {
      return [{ key: 'all', label: '', items: sortedAppliances.value }]
    }

    if (groupOption.value === 'TYPE') {
      const fridges = sortedAppliances.value.filter((item) => item.type === 'FRIDGE')
      const freezers = sortedAppliances.value.filter((item) => item.type === 'FREEZER')
      return [
        { key: 'fridge', label: 'Kjøleskap', items: fridges },
        { key: 'freezer', label: 'Frysere', items: freezers },
      ].filter((group) => group.items.length > 0)
    }

    const active = sortedAppliances.value.filter((item) => item.isActive)
    const inactive = sortedAppliances.value.filter((item) => !item.isActive)

    return [
      { key: 'active', label: 'Aktive enheter', items: active },
      { key: 'inactive', label: 'Inaktive enheter', items: inactive },
    ].filter((group) => group.items.length > 0)
  })

  return {
    sortOption,
    groupOption,
    groupedAppliances,
  }
}
