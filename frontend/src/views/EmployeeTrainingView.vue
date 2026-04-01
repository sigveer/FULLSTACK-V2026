<script setup lang="ts">
import { computed } from 'vue'
import { Award, Clock, AlertTriangle } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import { useTrainingStore } from '@/stores/training'
import EmployeeAvatar from '@/components/training/EmployeeAvatar.vue'
import StatusBadge from '@/components/training/StatusBadge.vue'
import StatCard from '@/components/training/StatCard.vue'

const auth  = useAuthStore()
const store = useTrainingStore()

const me        = computed(() => store.employees.find(e => e.id === auth.user?.id) ?? store.employees[4])
const trainings = computed(() => me.value?.trainings ?? [])
const valid     = computed(() => trainings.value.filter(t => t.status === 'Gyldig'))
const expiring  = computed(() => trainings.value.filter(t => t.status === 'Utløper snart'))
const missing   = computed(() => trainings.value.filter(t => t.status === 'Mangler'))
</script>

<template>
  <div class="page">
    <div class="header">
      <h1 class="page-title">Min opplæring</h1>
      <p class="page-sub">Oversikt over din opplæringsstatus</p>
    </div>

    <div v-if="me" class="profile-card">
      <EmployeeAvatar :initials="me.initials" :color="me.color" size="lg" />
      <div>
        <p class="profile-name">{{ me.name }}</p>
        <p class="profile-role">{{ me.role }}</p>
      </div>
    </div>

    <div class="stat-grid">
      <StatCard label="Gyldige" :value="valid.length" value-class="val-green">
        <Award class="stat-icon icon-green" :size="18" />
      </StatCard>
      <StatCard label="Utløper snart" :value="expiring.length" value-class="val-amber">
        <Clock class="stat-icon icon-amber" :size="18" />
      </StatCard>
      <StatCard label="Mangler" :value="missing.length" value-class="val-red">
        <AlertTriangle class="stat-icon icon-red" :size="18" />
      </StatCard>
    </div>

    <div class="table-card">
      <div v-if="!trainings.length" class="empty-state">
        Ingen opplæring registrert.
      </div>

      <div v-else class="table-scroll">
        <table class="data-table">
          <thead>
          <tr>
            <th>Opplæringstype</th>
            <th class="hide-mobile">Fullført</th>
            <th>Utløper</th>
            <th>Status</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="t in trainings" :key="t.id">
            <td class="cell-bold">{{ t.type }}</td>
            <td class="hide-mobile cell-text">{{ t.completed ?? '—' }}</td>
            <td :class="['cell-text', t.status === 'Utløper snart' ? 'expires-soon' : '']">
              {{ t.expires ?? '—' }}
            </td>
            <td><StatusBadge :status="t.status" /></td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

  </div>
</template>

<style scoped>
.page {
  max-width: 720px;
  margin: 0 auto;
  padding: 28px 24px 64px;
  font-family: inherit;
}

.header { margin-bottom: 28px; }

.page-title {
  font-size: 1.6rem;
  font-weight: 700;
  color: #111827;
  margin: 0;
  letter-spacing: -0.02em;
}

.page-sub {
  font-size: 0.85rem;
  color: #9ca3af;
  margin: 2px 0 0;
}

.profile-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #fff;
  border: 1px solid #e7e5e4;
  border-radius: 16px;
  padding: 16px 20px;
  margin-bottom: 20px;
}

.profile-name {
  font-size: 1.05rem;
  font-weight: 700;
  color: #111827;
  margin: 0;
}

.profile-role {
  font-size: 0.85rem;
  color: #9ca3af;
  margin: 2px 0 0;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}

.stat-icon { margin-top: 4px; }
.icon-green { color: #10b981; }
.icon-amber { color: #f59e0b; }
.icon-red   { color: #f87171; }

.table-card {
  background: #fff;
  border: 1px solid #e7e5e4;
  border-radius: 16px;
  overflow: hidden;
}

.table-scroll { overflow-x: auto; }

.empty-state {
  padding: 56px 0;
  text-align: center;
  font-size: 0.875rem;
  color: #9ca3af;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table thead tr {
  border-bottom: 1px solid #f5f5f4;
}

.data-table th {
  text-align: left;
  font-size: 0.75rem;
  font-weight: 600;
  color: #9ca3af;
  padding: 12px 20px;
  white-space: nowrap;
}

.data-table tbody tr {
  border-bottom: 1px solid #fafaf9;
  transition: background 0.12s;
}
.data-table tbody tr:last-child { border-bottom: none; }
.data-table tbody tr:hover { background: #fafaf9; }

.data-table td {
  padding: 14px 20px;
  vertical-align: middle;
}

.cell-bold { font-size: 0.875rem; font-weight: 600; color: #111827; }
.cell-text { font-size: 0.875rem; color: #4b5563; }
.expires-soon { color: #d97706; font-weight: 600; }

@media (max-width: 640px) {
  .hide-mobile { display: none; }
}
</style>
