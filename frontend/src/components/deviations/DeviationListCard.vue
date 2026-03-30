<script setup lang="ts">
import { computed } from 'vue'
import StatusPill from '@/components/ui/StatusPill.vue'
import type { Deviation, DeviationModule, DeviationSeverity, DeviationStatus } from '@/types/deviation'

const props = defineProps<{
  deviation: Deviation
}>()

const emits = defineEmits<{
  (e: 'open', deviation: Deviation): void
}>()

const moduleLabel: Record<DeviationModule, string> = {
  IK_MAT: 'IK-Mat',
  IK_ALKOHOL: 'IK-Alkohol',
}

const statusLabel: Record<DeviationStatus, string> = {
  OPEN: 'Åpen',
  IN_PROGRESS: 'Under behandling',
  RESOLVED: 'Løst',
  CLOSED: 'Løst',
}

const severityLabel: Record<DeviationSeverity, string> = {
  LOW: 'Lav',
  MEDIUM: 'Middels',
  HIGH: 'Høy',
  CRITICAL: 'Kritisk',
}

const severityTone: Record<DeviationSeverity, 'neutral' | 'ok' | 'danger' | 'warning' | 'brand'> = {
  LOW: 'ok',
  MEDIUM: 'warning',
  HIGH: 'warning',
  CRITICAL: 'danger',
}

const statusTone: Record<DeviationStatus, 'neutral' | 'ok' | 'danger' | 'warning' | 'brand'> = {
  OPEN: 'danger',
  IN_PROGRESS: 'warning',
  RESOLVED: 'ok',
  CLOSED: 'ok',
}

const severityRailClass: Record<DeviationSeverity, string> = {
  LOW: 'deviation-card--low',
  MEDIUM: 'deviation-card--medium',
  HIGH: 'deviation-card--high',
  CRITICAL: 'deviation-card--critical',
}

const relativeTime = computed(() => toRelativeTime(props.deviation.reportedAt))

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
</script>

<template>
  <button
    type="button"
    :class="`deviation-card ${severityRailClass[deviation.severity]}`"
    @click="emits('open', deviation)"
  >
    <div class="deviation-card-header">
      <div class="header-main">
        <div class="tag-row">
          <StatusPill :label="severityLabel[deviation.severity]" :tone="severityTone[deviation.severity]" />
          <StatusPill :label="moduleLabel[deviation.module]" tone="brand" />
          <StatusPill :label="statusLabel[deviation.status]" :tone="statusTone[deviation.status]" />
        </div>

        <h3>{{ deviation.title }}</h3>
        <p class="description">{{ deviation.description }}</p>

        <div class="meta-line">
          <span>Rapportert av: {{ deviation.reportedByUserName }}</span>
          <span>Tildelt: {{ deviation.assignedToUserName ?? 'Ikke tildelt' }}</span>
        </div>
      </div>

      <div class="header-side">
        <span class="time-label">{{ relativeTime }}</span>
      </div>
    </div>
  </button>
</template>

<style scoped>
.deviation-card {
  position: relative;
  width: 100%;
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  background: var(--card-bg);
  padding: 14px 14px 12px 18px;
  text-align: left;
  cursor: pointer;
  transition: box-shadow 150ms ease, transform 120ms ease;
}

.deviation-card:hover {
  box-shadow: 0 6px 14px rgb(0 0 0 / 0.08);
}

.deviation-card:active {
  transform: scale(0.998);
}

.deviation-card:focus-visible {
  outline: none;
  box-shadow: 0 0 0 2px hsl(var(--ring) / 0.3);
}

.deviation-card::before {
  content: '';
  position: absolute;
  left: -1px;
  top: -1px;
  bottom: -1px;
  width: 5px;
  border-radius: var(--radius-lg) 0 0 var(--radius-lg);
  background: transparent;
}

.deviation-card--critical {
  border-color: hsl(var(--destructive) / 0.25);
}

.deviation-card--critical::before {
  background: var(--red);
}

.deviation-card--medium {
  border-color: #ead8a6;
}

.deviation-card--medium::before {
  background: #d0a11f;
}

.deviation-card--high {
  border-color: #e7bf99;
}

.deviation-card--high::before {
  background: #c9751a;
}

.deviation-card--low {
  border-color: #b9d9be;
}

.deviation-card--low::before {
  background: var(--green);
}

.deviation-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.header-main {
  min-width: 0;
  flex: 1;
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

h3 {
  margin: 10px 0 6px;
  font-size: 1.65rem;
  letter-spacing: -0.02em;
}

.description {
  color: var(--text-primary);
  margin: 0;
  font-size: 1.14rem;
  line-height: 1.35;
  line-clamp: 2;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.meta-line {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  color: var(--text-secondary);
  font-size: 1rem;
}

.header-side {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.time-label {
  color: var(--text-secondary);
  font-size: 1.05rem;
  text-align: right;
}

@media (max-width: 860px) {
  .deviation-card-header {
    flex-direction: column;
  }

  .header-side {
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
