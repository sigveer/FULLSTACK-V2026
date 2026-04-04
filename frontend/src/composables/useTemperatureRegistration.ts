import { useCreateFoodDeviationMutation } from '@/composables/useFoodDeviations'
import { useTemperatureMonitoring } from '@/composables/useTemperatureMonitoring'

function toDeviationSeverity(temperature: number, min: number, max: number): 'MEDIUM' | 'HIGH' | 'CRITICAL' {
  const distance = temperature < min ? min - temperature : temperature - max

  if (distance >= 5) {
    return 'CRITICAL'
  }

  if (distance >= 2) {
    return 'HIGH'
  }

  return 'MEDIUM'
}

export function useTemperatureRegistration() {
  const createFoodDeviation = useCreateFoodDeviationMutation()
  const { appliances, registerTemperature } = useTemperatureMonitoring()

  async function registerTemperatureWithDeviation(payload: {
    applianceId: number
    temperature: number
    note?: string
  }) {
    const appliance = appliances.value.find((item) => item.id === payload.applianceId)
    if (!appliance) {
      return { entry: null, deviationCreated: false }
    }

    const entry = await registerTemperature({
      applianceId: appliance.id,
      temperature: payload.temperature,
      note: payload.note,
    })

    if (!entry) {
      return { entry: null, deviationCreated: false }
    }

    if (entry.status !== 'DEVIATION') {
      return { entry, deviationCreated: false }
    }

    const threshold = appliance.threshold
    const severity = toDeviationSeverity(entry.temperature, threshold.min, threshold.max)

    try {
      await createFoodDeviation.mutateAsync({
        reportedAt: entry.measuredAt,
        deviationType: 'TEMPERATUR',
        severity,
        description: `Temperaturavvik registrert for ${appliance.name}. Målt ${entry.temperature.toFixed(1)}°C (grense ${threshold.min} til ${threshold.max}°C).`,
        immediateAction: entry.note || undefined,
      })

      return { entry, deviationCreated: true }
    } catch {
      return { entry, deviationCreated: false, deviationFailed: true }
    }
  }

  return {
    registerTemperatureWithDeviation,
  }
}
