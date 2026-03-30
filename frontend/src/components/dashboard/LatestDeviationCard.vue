<script setup lang="ts">
import StatusPill from '../ui/StatusPill.vue'

defineProps<{
  deviations: Array<{
    id: number
    title: string
    dateLabel: string
    statusLabel: 'Åpen' | 'Under behandling' | 'Løst' | 'Lukket'
  }>
}>()

const statusTone: Record<'Åpen' | 'Under behandling' | 'Løst' | 'Lukket', 'danger' | 'warning' | 'ok' | 'neutral'> = {
  Åpen: 'danger',
  'Under behandling': 'warning',
  Løst: 'ok',
  Lukket: 'neutral',
}
</script>

<template>
  <section class="panel">
    <h2>Siste avvik</h2>

    <article v-for="item in deviations" :key="item.id" class="entry">
      <div>
        <h3>{{ item.title }}</h3>
        <p>{{ item.dateLabel }}</p>
      </div>
      <StatusPill :label="item.statusLabel" :tone="statusTone[item.statusLabel]" />
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
