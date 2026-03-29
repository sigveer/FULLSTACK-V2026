<script setup lang="ts">
import StatusPill from '../ui/StatusPill.vue'

defineProps<{
  deviations: Array<{
    id: number
    title: string
    meta: string
    severity: 'Kritisk' | 'Middels' | 'Løst'
  }>
}>()

const severityTone: Record<'Kritisk' | 'Middels' | 'Løst', 'danger' | 'warning' | 'ok'> = {
  Kritisk: 'danger',
  Middels: 'warning',
  Løst: 'ok',
}

const severityRailClass: Record<'Kritisk' | 'Middels' | 'Løst', string> = {
  Kritisk: 'entry--critical',
  Middels: 'entry--medium',
  Løst: 'entry--resolved',
}
</script>

<template>
  <section class="panel">
    <h2>Siste avvik</h2>

    <article v-for="item in deviations" :key="item.id" class="entry" :class="severityRailClass[item.severity]">
      <div>
        <h3>{{ item.title }}</h3>
        <p>{{ item.meta }}</p>
      </div>
      <StatusPill :label="item.severity" :tone="severityTone[item.severity]" />
    </article>
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

.entry--medium {
  border-left-color: #b5781d;
}

.entry--resolved {
  border-left-color: #158856;
}

@media (max-width: 760px) {
  .entry {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
