<script setup lang="ts">
import { computed } from 'vue'
import type { PenaltyPointSummary } from '@/types/deviation'

const props = defineProps<{
  summary: PenaltyPointSummary | null
}>()

const MAX_POINTS = 12

const totalPoints = computed(() => props.summary?.totalPoints ?? 0)
const progressPercent = computed(() => Math.min(100, (totalPoints.value / MAX_POINTS) * 100))

const lastEntry = computed(() => {
  if (!props.summary?.entries.length) return null
  return props.summary.entries[0]
})

const lastEntryDate = computed(() => {
  if (!lastEntry.value) return null
  return new Date(lastEntry.value.createdAt).toLocaleDateString('nb-NO', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  })
})

const lastEntryLabel = computed(() => {
  if (!lastEntry.value) return ''
  const typeLabels: Record<string, string> = {
    SKJENKING_MINDREAARIGE: 'Skjenking mindreårige',
    BRUDD_BISTANDSPLIKT: 'Brudd bistandsplikt',
    UFORSVARLIG_DRIFT: 'Uforsvarlig drift',
    HINDRING_KONTROLL: 'Hindring kontroll',
    SKJENKING_APENBART_BERUSET: 'Skjenking beruset',
    BRUDD_SJENKETIDER: 'Brudd sjenketider',
    BRENNEVIN_18_19: 'Brennevin 18-19',
    BERUSET_PERSON_I_LOKALET: 'Beruset i lokalet',
    MANGLER_IK_SYSTEM: 'Mangler IK-system',
    MANGLER_STYRER_STEDFORTREDER: 'Mangler styrer',
    NARKOTIKA: 'Narkotika',
    ALKOHOLFRI_ALTERNATIV_MANGLER: 'Alkoholfri mangler',
    MEDBRAKT_ALKOHOL: 'Medbrakt alkohol',
    REKLAMEBRUDD: 'Reklamebrudd',
    VILKAARSBRUDD: 'Vilkårsbrudd',
  }
  const label = typeLabels[lastEntry.value.violationType] ?? lastEntry.value.violationType
  return `${label} (${lastEntry.value.points}p)`
})

const progressColor = computed(() => {
  const pct = progressPercent.value
  if (pct >= 75) return '#ae2c2d'
  if (pct >= 50) return '#c9751a'
  return '#9f6511'
})
</script>

<template>
  <div class="status-card">
    <h3 class="status-title">Prikkstatus</h3>
    <p class="status-subtitle">12 prikker i løpet av 2 år = inndragning i 1 uke.</p>

    <div class="points-display">
      <strong class="points-value" :style="{ color: progressColor }">{{ totalPoints }}</strong>
      <span class="points-of">av {{ MAX_POINTS }}</span>
    </div>

    <div class="progress-bar">
      <div
        class="progress-fill"
        :style="{ width: `${progressPercent}%`, backgroundColor: progressColor }"
      />
    </div>

    <div class="info-cards">
      <div class="info-card info-card--last">
        <span class="info-label">Siste hendelse</span>
        <strong class="info-value">{{ lastEntryDate ?? 'Ingen' }}</strong>
        <span v-if="lastEntryLabel" class="info-sub">{{ lastEntryLabel }}</span>
      </div>
      <div class="info-card info-card--period">
        <span class="info-label">Totalt prikker</span>
        <strong class="info-value">{{ totalPoints }} prikker</strong>
        <span class="info-sub">{{ summary?.entries.length ?? 0 }} hendelser registrert</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.status-card {
  border: 1px solid hsl(var(--border));
  border-radius: var(--radius-lg);
  background: #ffffff;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.status-title {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 700;
  letter-spacing: -0.01em;
}

.status-subtitle {
  margin: 0;
  font-size: 0.84rem;
  color: hsl(var(--muted-foreground));
}

.points-display {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.points-value {
  font-size: 2.2rem;
  letter-spacing: -0.02em;
  line-height: 1;
}

.points-of {
  font-size: 0.9rem;
  color: hsl(var(--muted-foreground));
}

.progress-bar {
  width: 100%;
  height: 8px;
  border-radius: 999px;
  background: hsl(var(--secondary));
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 999px;
  transition: width 500ms ease;
}

.info-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin-top: 8px;
}

.info-card {
  border-radius: var(--radius-md);
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.info-card--last { background: #fde8e8; }
.info-card--period { background: #dcebd8; }

.info-label {
  font-size: 0.78rem;
  font-weight: 600;
}

.info-card--last .info-label { color: #991b1b; }
.info-card--period .info-label { color: #166534; }

.info-value { font-size: 1.05rem; font-weight: 700; }
.info-card--last .info-value { color: #7f1d1d; }
.info-card--period .info-value { color: #14532d; }

.info-sub { font-size: 0.78rem; }
.info-card--last .info-sub { color: #991b1b; }
.info-card--period .info-sub { color: #166534; }
</style>
