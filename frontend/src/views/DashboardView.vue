<script setup lang="ts">
import { computed } from 'vue'
import KpiCard from '@/components/dashboard/KpiCard.vue'
import LatestDeviationCard from '@/components/dashboard/LatestDeviationCard.vue'
import TemperatureLogCard from '@/components/dashboard/TemperatureLogCard.vue'
import AppLayout from '@/components/layout/AppLayout.vue'
import StatusPill from '@/components/ui/StatusPill.vue'
import { Separator } from '@/components/ui/separator'
import { SidebarTrigger } from '@/components/ui/sidebar'
import { useDeviationsQuery } from '@/composables/useDeviations'
import type { DeviationStatus } from '@/types/deviation'

const deviationsQuery = useDeviationsQuery()

const kpis: Array<{
  title: string
  value: string
  subtitle?: string
  highlight?: 'default' | 'danger' | 'success'
  progress?: {
    current: number
    total: number
  }
}> = [
  {
    title: 'Temp.avvik',
    value: '2',
    subtitle: 'Krever tiltak',
    highlight: 'danger' as const,
  },
  {
    title: 'Apne avvik',
    value: '4',
    subtitle: '1 kritisk',
  },
  {
    title: 'Opplæring',
    value: '92%',
    subtitle: '1 utløper snart',
    highlight: 'success' as const,
  },
]

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
      dateLabel: toDashboardDate(item.reportedAt),
      statusLabel: toStatusLabel(item.status),
    }))
})

function toDashboardDate(value: string): string {
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return '-'
  }

  return date.toLocaleDateString('nb-NO', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  })
}

function toStatusLabel(status: DeviationStatus): 'Åpen' | 'Under behandling' | 'Løst' | 'Lukket' {
  switch (status) {
    case 'OPEN':
      return 'Åpen'
    case 'IN_PROGRESS':
      return 'Under behandling'
    case 'RESOLVED':
      return 'Løst'
    default:
      return 'Lukket'
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
