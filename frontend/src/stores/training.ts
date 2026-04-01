import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { useAuthStore } from '@/stores/auth'

export interface Training {
  id: number
  type: string
  completed: string | null
  expires: string | null
  status: 'Gyldig' | 'Utløper snart' | 'Mangler'
}

export interface Employee {
  id: number
  organizationId: number
  name: string
  initials: string
  role: string
  color: string
  trainings: Training[]
}

export interface TrainingRow extends Training {
  employee: Employee
}

export const useTrainingStore = defineStore('training', () => {
  const auth = useAuthStore()
  const _allEmployees = ref<Employee[]>([
    {
      id: 1, organizationId: 1,
      name: 'Leder Ledersen', initials: 'LL', role: 'Styrer', color: '#7c6fcd',
      trainings: [{ id: 101, type: 'Kunnskapsprøve alkohol', completed: '12.01.2025', expires: '12.01.2027', status: 'Gyldig' }]
    },
    {
      id: 2, organizationId: 1,
      name: 'Kari Hansen', initials: 'KH', role: 'Stedfortreder', color: '#4fad89',
      trainings: [{ id: 102, type: 'Kunnskapsprøve alkohol', completed: '05.03.2024', expires: '05.04.2026', status: 'Utløper snart' }]
    },
    {
      id: 3, organizationId: 1,
      name: 'Ole Johansen', initials: 'OJ', role: 'Servitør', color: '#e07b6a',
      trainings: [{ id: 103, type: 'Alderskontroll-opplæring', completed: '15.02.2026', expires: '15.02.2027', status: 'Gyldig' }]
    },
    {
      id: 4, organizationId: 1,
      name: 'Per Nilsen', initials: 'PN', role: 'Bartender', color: '#5b9bd5',
      trainings: [{ id: 104, type: 'Ansvarlig vertskap', completed: '20.01.2026', expires: '20.04.2026', status: 'Utløper snart' }]
    },
    {
      id: 5, organizationId: 1,
      name: 'Ny Bansen', initials: 'NB', role: 'Servitør', color: '#e0a04f',
      trainings: [{ id: 105, type: 'Alderskontroll-opplæring', completed: null, expires: null, status: 'Mangler' }]
    },
    {
      id: 6, organizationId: 1,
      name: 'Anna Sørensen', initials: 'AS', role: 'Servitør', color: '#a07cc5',
      trainings: [{ id: 106, type: 'Ansvarlig vertskap', completed: '10.11.2024', expires: '10.11.2026', status: 'Gyldig' }]
    },

    {
      id: 7, organizationId: 2,
      name: 'Bjørn Berg', initials: 'BB', role: 'Styrer', color: '#3b82f6',
      trainings: [{ id: 201, type: 'Kunnskapsprøve alkohol', completed: '01.06.2024', expires: '01.06.2026', status: 'Utløper snart' }]
    },
    {
      id: 8, organizationId: 2,
      name: 'Silje Strand', initials: 'SS', role: 'Servitør', color: '#ec4899',
      trainings: [{ id: 202, type: 'Alderskontroll-opplæring', completed: '10.01.2026', expires: '10.01.2027', status: 'Gyldig' }]
    },
  ])
  const employees = computed<Employee[]>(() => {
    const orgId = auth.organizationId
    if (!orgId) return []
    return _allEmployees.value.filter(e => e.organizationId === orgId)
  })

  const allTrainings = computed<TrainingRow[]>(() =>
    employees.value.flatMap(emp =>
      emp.trainings.map(t => ({ ...t, employee: emp }))
    )
  )

  const trainingTypes = computed<string[]>(() =>
    [...new Set(allTrainings.value.map(t => t.type))]
  )

  const totalEmployees    = computed(() => employees.value.length)
  const completedCount    = computed(() => employees.value.filter(e => e.trainings.some(t => t.status !== 'Mangler')).length)
  const expiringSoonCount = computed(() => allTrainings.value.filter(t => t.status === 'Utløper snart').length)

  function updateTraining(employeeId: number, trainingId: number, data: Omit<Training, 'id'>): void {
    const emp = employees.value.find(e => e.id === employeeId)
    if (!emp) return
    const idx = emp.trainings.findIndex(t => t.id === trainingId)
    if (idx !== -1) emp.trainings[idx] = { ...data, id: trainingId }
  }

  function deleteTraining(employeeId: number, trainingId: number): void {
    const emp = employees.value.find(e => e.id === employeeId)
    if (emp) emp.trainings = emp.trainings.filter(t => t.id !== trainingId)
  }

  function addTraining(employeeId: number, data: Omit<Training, 'id'>): void {
    const emp = employees.value.find(e => e.id === employeeId)
    if (emp) emp.trainings.push({ ...data, id: Date.now() })
  }

  return {
    employees,
    allTrainings,
    trainingTypes,
    totalEmployees,
    completedCount,
    expiringSoonCount,
    updateTraining,
    deleteTraining,
    addTraining,
  }
})
