<script setup lang="ts">
import KpiCard from '@/components/dashboard/KpiCard.vue'
import LatestDeviationCard from '@/components/dashboard/LatestDeviationCard.vue'
import TemperatureLogCard from '@/components/dashboard/TemperatureLogCard.vue'
import AppLayout from '@/components/layout/AppLayout.vue'
import StatusPill from '@/components/ui/StatusPill.vue'

const kpis = [
  {
    title: 'Sjekklister i dag',
    value: '3/5',
    progress: { current: 3, total: 5 },
  },
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
    location: 'Kjoeleskap 2',
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

const deviations = [
  {
    id: 1,
    title: 'Kjøleskap 2 over grenseverdi',
    meta: 'IK-Mat - Rapportert av Ansatt Ansattsen - 2 timer siden',
    severity: 'Kritisk' as const,
  },
  {
    id: 2,
    title: 'Manglende ID-kontroll observert',
    meta: 'IK-Alkohol - Rapportert av Leder Ledersen - I går',
    severity: 'Middels' as const,
  },
  {
    id: 3,
    title: 'Renholdsplan ikke fullført tirsdag',
    meta: 'IK-Mat - Rapportert av Ansatt Ansattsen - 3 dager siden',
    severity: 'Løst' as const,
  },
]
</script>

<template>
  <AppLayout active-menu-item="Dashboard">
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
    <LatestDeviationCard :deviations="deviations" />
  </AppLayout>
</template>

<style scoped>
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
