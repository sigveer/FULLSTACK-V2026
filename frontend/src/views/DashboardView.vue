<script setup lang="ts">
import { computed } from 'vue'
import KpiCard from '@/components/dashboard/KpiCard.vue'
import LatestDeviationCard from '@/components/dashboard/LatestDeviationCard.vue'
import TemperatureLogCard from '@/components/dashboard/TemperatureLogCard.vue'
import AppLayout from '@/components/layout/AppLayout.vue'
import StatusPill from '@/components/ui/StatusPill.vue'
import { Separator } from '@/components/ui/separator'
import { SidebarTrigger } from '@/components/ui/sidebar'
import { useChecklistsQuery } from '@/composables/useChecklists'
import { useDeviationsQuery } from '@/composables/useDeviations'
import type { Checklist } from '@/types/checklist'
import type { DeviationSeverity } from '@/types/deviation'

const deviationsQuery = useDeviationsQuery()
const checklistsQuery = useChecklistsQuery()

const kpis = computed<Array<{
  title: string
  value: string
  subtitle?: string
  highlight?: 'default' | 'danger' | 'success'
  progress?: {
    current: number
    total: number
  }
}>>(() => {
  const dailyStats = getDailyChecklistStats(checklistsQuery.data.value ?? [])
  const allDeviations = deviationsQuery.data.value ?? []
  const openCount = allDeviations.filter((item) => item.status === 'OPEN').length
  const criticalOpenCount = allDeviations.filter(
    (item) => item.status === 'OPEN' && item.severity === 'CRITICAL',
  ).length

  return [
    {
      title: 'Sjekklister i dag',
      value: `${dailyStats.completed}/${dailyStats.total}`,
      progress: {
        current: dailyStats.completed,
        total: Math.max(dailyStats.total, 1),
      },
    },
    {
      title: 'Temp.avvik',
      value: '2',
      subtitle: 'Krever tiltak',
      highlight: 'danger' as const,
    },
    {
      title: 'Åpne avvik',
      value: String(openCount),
      subtitle: `${criticalOpenCount} kritisk`,
    },
    {
      title: 'Opplæring',
      value: '92%',
      subtitle: '1 utløper snart',
      highlight: 'success' as const,
    },
  ]
})

const temperatures = [
  {
    location: 'Kjøleskap 1',
    temperature: '3.2 C',
    limit: '0-4 C',
    status: 'OK' as const,
  },
  {
    location: 'Kjøleskap 2',
    temperature: '6.1 C',
    limit: '0-4 C',
    status: 'Avvik' as const,
  },
  {
    location: 'Fryser',
    temperature: '-19.4 C',
    limit: 'Under -18 C',
    status: 'OK' as const,
  },
  {
    location: 'Varmholding',
    temperature: '57.0 C',
    limit: 'Over 60 C',
    status: 'Avvik' as const,
  },
]

const latestDeviations = computed(() => {
  const source = deviationsQuery.data.value ?? []

  return [...source]
    .sort((a, b) => new Date(b.reportedAt).getTime() - new Date(a.reportedAt).getTime())
    .slice(0, 3)
    .map((item) => ({
      id: item.id,
      title: item.title,
      moduleLabel: item.module === 'IK_MAT' ? 'IK-Mat' : 'IK-Alkohol',
      reportedBy: item.reportedByUserName,
      relativeTime: toRelativeTime(item.reportedAt),
      severityLabel: toSeverityLabel(item.severity),
    }))
})

function toSeverityLabel(severity: DeviationSeverity): 'Lav' | 'Middels' | 'Høy' | 'Kritisk' {
  switch (severity) {
    case 'LOW':
      return 'Lav'
    case 'MEDIUM':
      return 'Middels'
    case 'HIGH':
      return 'Høy'
    default:
      return 'Kritisk'
  }
}

function toRelativeTime(value: string): string {
  const timestamp = new Date(value).getTime()
  if (Number.isNaN(timestamp)) {
    return '-'
  }

  const diffMs = Date.now() - timestamp
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour

  if (diffMs < hour) {
    const minutes = Math.max(1, Math.floor(diffMs / minute))
    return `${minutes} min siden`
  }

  if (diffMs < day) {
    const hours = Math.floor(diffMs / hour)
    return `${hours} time${hours > 1 ? 'r' : ''} siden`
  }

  const days = Math.floor(diffMs / day)
  return `${days} dag${days > 1 ? 'er' : ''} siden`
}

function getDailyChecklistStats(checklists: Checklist[]): { total: number; completed: number } {
  const daily = checklists.filter((item) => item.frequency === 'DAILY' && item.active)
  return {
    total: daily.length,
    completed: daily.filter((item) => item.status === 'COMPLETED').length,
  }
}
</script>

<template>
  <AppLayout>
    <header class="page-header">
      <div class="page-header-inner">
        <SidebarTrigger />
        <Separator orientation="vertical" class="header-separator" />
        <span class="page-title">Dashboard</span>
      </div>
    </header>

    <div class="page-content">
      <section class="dashboard-header">
        <div>
          <h1>Oversikt</h1>
          <p>Fredag 20. mars 2026</p>
        </div>

        <div class="module-toggle" aria-label="Velg modul">
          <StatusPill label="IK-Mat" tone="brand" />
          <StatusPill label="IK-Alkohol" tone="ok" />
        </div>
      </section>

      <section class="kpi-grid">
        <KpiCard
          v-for="kpi in kpis"
          :key="kpi.title"
          :title="kpi.title"
          :value="kpi.value"
          :subtitle="kpi.subtitle"
          :progress="kpi.progress"
          :highlight="kpi.highlight"
        />
      </section>

      <TemperatureLogCard :rows="temperatures" />
      <LatestDeviationCard :deviations="latestDeviations" />
    </div>
  </AppLayout>
</template>

<style scoped>
.page-header {
  display: flex;
  height: 4rem;
  flex-shrink: 0;
  align-items: center;
  gap: 0.5rem;
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

.dashboard-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.dashboard-header h1 {
  margin: 0;
  font-size: 3rem;
  letter-spacing: -0.03em;
}

.dashboard-header p {
  margin: 2px 0 0;
  color: var(--text-secondary);
  font-size: 1.75rem;
}

.module-toggle {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 4px;
}

.kpi-grid {
  margin-top: 14px;
  padding: 10px 8px 4px;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  border-radius: var(--radius-md);
}

@media (max-width: 1120px) {
  .kpi-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 760px) {
  .dashboard-header {
    flex-direction: column;
  }

  .dashboard-header h1 {
    font-size: 2.4rem;
  }

  .dashboard-header p {
    font-size: 1.35rem;
  }

  .kpi-grid {
    grid-template-columns: 1fr;
  }
}
</style>
