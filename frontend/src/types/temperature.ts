export type TemperatureApplianceType = 'FRIDGE' | 'FREEZER'

export type TemperatureEntryStatus = 'OK' | 'DEVIATION'

export interface TemperatureThreshold {
  min: number
  max: number
}

export interface TemperatureAppliance {
  id: string
  name: string
  type: TemperatureApplianceType
  threshold: TemperatureThreshold
  isActive: boolean
  createdAt: string
  updatedAt: string
}

export interface TemperatureEntry {
  id: string
  applianceId: string
  measuredAt: string
  measuredBy: string
  temperature: number
  note: string
  status: TemperatureEntryStatus
  createdAt: string
}

export interface RegisterTemperaturePayload {
  applianceId: string
  temperature: number
  measuredBy: string
  measuredAt?: string
  note?: string
}

export interface CreateAppliancePayload {
  name: string
  type: TemperatureApplianceType
  threshold?: TemperatureThreshold
}

export interface UpdateAppliancePayload {
  name?: string
  threshold?: TemperatureThreshold
  isActive?: boolean
}

export interface TemperatureApplianceWithLastEntry extends TemperatureAppliance {
  lastEntry: TemperatureEntry | null
}
