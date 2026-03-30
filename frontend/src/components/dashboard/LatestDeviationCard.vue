<script setup lang="ts">
import StatusPill from '../ui/StatusPill.vue'

defineProps<{
  deviations: Array<{
    id: number
    title: string
    moduleLabel: string
    reportedBy: string
    relativeTime: string
    severityLabel: 'Lav' | 'Middels' | 'Høy' | 'Kritisk'
  }>
}>()

const severityTone: Record<'Lav' | 'Middels' | 'Høy' | 'Kritisk', 'ok' | 'warning' | 'danger'> = {
  Lav: 'ok',
  Middels: 'warning',
  Høy: 'warning',
  Kritisk: 'danger',
}

const severityRailClass: Record<'Lav' | 'Middels' | 'Høy' | 'Kritisk', string> = {
  Lav: 'entry--low',
  Middels: 'entry--medium',
  Høy: 'entry--high',
  Kritisk: 'entry--critical',
}
</script>

<template>
  <section class="panel">
    <h2>Siste avvik</h2>

    <article v-for="item in deviations" :key="item.id" class="entry" :class="severityRailClass[item.severityLabel]">
      <div>
        <h3>{{ item.title }}</h3>
        <p>{{ item.moduleLabel }} · Rapportert av {{ item.reportedBy }} · {{ item.relativeTime }}</p>
      </div>
      <StatusPill :label="item.severityLabel" :tone="severityTone[item.severityLabel]" />
    </article>

    <p v-if="deviations.length === 0" class="empty-state">Ingen registrerte avvik ennå.</p>
  </section>
</template>

<style scoped>
.panel {
  margin-top: 18px;
  border: 1px solid var(--border);
  border-radius: var(--radius-xl);
  background: var(--card-bg);
  padding: 20px;
}

h2 {
  margin: 0 0 8px;
  font-size: 2rem;
  letter-spacing: -0.02em;
}

.entry {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 10px;
  border-left: 4px solid transparent;
}

.entry + .entry {
  margin-top: 8px;
}

.entry h3 {
  margin: 0;
  font-size: 1.8rem;
  line-height: 1.2;
}

.entry p {
  margin: 5px 0 0;
  color: #4b5158;
  font-size: 1.14rem;
}

.entry--critical {
  border-left-color: #ab3030;
}

.entry--high {
  border-left-color: #c9751a;
}

.entry--medium {
  border-left-color: #d0a11f;
}

.entry--low {
  border-left-color: #158856;
}

.empty-state {
  margin-top: 8px;
  color: var(--text-secondary);
  font-size: 1.05rem;
}

@media (max-width: 760px) {
  .entry {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
