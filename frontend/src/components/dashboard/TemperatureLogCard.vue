<script setup lang="ts">
import StatusPill from '../ui/StatusPill.vue'

defineProps<{
  rows: Array<{
    location: string
    temperature: string
    limit: string
    status: 'OK' | 'Avvik'
  }>
}>()
</script>

<template>
  <section class="panel">
    <h2>Dagens temperaturlogg</h2>

    <table>
      <thead>
        <tr>
          <th>Plassering</th>
          <th>Temp</th>
          <th>Grense</th>
          <th class="status-col">Status</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in rows" :key="row.location">
          <td>{{ row.location }}</td>
          <td :class="{ 'danger-text': row.status === 'Avvik' }">{{ row.temperature }}</td>
          <td>{{ row.limit }}</td>
          <td class="status-col">
            <StatusPill :label="row.status" :tone="row.status === 'OK' ? 'ok' : 'danger'" />
          </td>
        </tr>
      </tbody>
    </table>
  </section>
</template>

<style scoped>
.panel {
  margin-top: 22px;
  border: 1px solid var(--border);
  border-radius: var(--radius-xl);
  background: var(--card-bg);
  padding: 20px;
}

h2 {
  margin: 0 0 12px;
  font-size: 2rem;
  letter-spacing: -0.02em;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 1.1rem;
}

th,
td {
  text-align: left;
  padding: 11px 8px;
  border-bottom: 1px solid #d5d5d2;
}

th {
  color: #3f444a;
  font-size: 1.03rem;
  font-weight: 700;
}

tbody tr:last-child td {
  border-bottom: none;
}

.status-col {
  text-align: right;
}

.danger-text {
  color: var(--red);
}

@media (max-width: 720px) {
  table,
  thead,
  tbody,
  tr,
  th,
  td {
    display: block;
  }

  thead {
    display: none;
  }

  tr {
    border-bottom: 1px solid #d5d5d2;
    padding: 8px 0;
  }

  tr:last-child {
    border-bottom: none;
  }

  td {
    border: none;
    padding: 5px 0;
  }

  .status-col {
    text-align: left;
  }
}
</style>
