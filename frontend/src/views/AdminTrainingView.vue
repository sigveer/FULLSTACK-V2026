<script setup lang="ts">
import { ref, computed } from 'vue'
import { Plus, Pencil, X, Trash2 } from 'lucide-vue-next'
import type { TrainingRow } from '@/stores/training'
import { useTrainingStore } from '@/stores/training'
import StatCard from '@/components/training/StatCard.vue'
import StatusBadge from '@/components/training/StatusBadge.vue'
import EmployeeAvatar from '@/components/training/EmployeeAvatar.vue'
import FilterPanel from '@/components/training/FilterPanel.vue'
import EditTrainingModal from '@/components/training/EditTrainingModal.vue'
import RegisterTrainingModal from '@/components/training/RegisterTrainingModal.vue'

const store = useTrainingStore()

const filterType   = ref('')
const filterStatus = ref('')

const filtered = computed(() =>
  store.allTrainings.filter(t =>
    (!filterType.value   || t.type   === filterType.value) &&
    (!filterStatus.value || t.status === filterStatus.value)
  )
)

// ── Edit / bulk-delete mode ──────────────────────────────────────────────────
const editMode = ref(false)
const selected = ref<Set<number>>(new Set())

function toggleEditMode(): void {
  editMode.value = !editMode.value
  selected.value = new Set()
}

function toggleSelect(id: number): void {
  const s = new Set(selected.value)
  s.has(id) ? s.delete(id) : s.add(id)
  selected.value = s
}

const allSelected = computed(() =>
  filtered.value.length > 0 && filtered.value.every(r => selected.value.has(r.id))
)

function toggleSelectAll(): void {
  selected.value = allSelected.value
    ? new Set()
    : new Set(filtered.value.map(r => r.id))
}

function deleteSelected(): void {
  selected.value.forEach(id => {
    const row = store.allTrainings.find(r => r.id === id)
    if (row) store.deleteTraining(row.employee.id, row.id)
  })
  selected.value = new Set()
  editMode.value = false
}

// ── Single-row edit / delete ─────────────────────────────────────────────────
const showRegister     = ref(false)
const editModal        = ref(false)
const editRow          = ref<TrainingRow | null>(null)
const deleteConfirmRow = ref<TrainingRow | null>(null)

function openEdit(row: TrainingRow): void {
  editRow.value   = row
  editModal.value = true
}

function confirmDelete(): void {
  if (!deleteConfirmRow.value) return
  store.deleteTraining(deleteConfirmRow.value.employee.id, deleteConfirmRow.value.id)
  deleteConfirmRow.value = null
}
</script>

<template>
  <div class="page">

    <!-- Header -->
    <div class="header">
      <div>
        <h1 class="page-title">Opplæring og sertifiseringer</h1>
        <p class="page-sub">Oversikt over ansattes opplæringsstatus</p>
      </div>
      <div class="header-actions">
        <button v-if="!editMode" class="btn btn-outline" @click="toggleEditMode">
          <Pencil :size="14" /> Rediger
        </button>
        <template v-else>
          <button v-if="selected.size > 0" class="btn btn-danger" @click="deleteSelected">
            <Trash2 :size="14" /> Slett ({{ selected.size }})
          </button>
          <button class="btn btn-outline" @click="toggleEditMode">
            <X :size="14" /> Avbryt
          </button>
        </template>
        <button v-if="!editMode" class="btn btn-register" @click="showRegister = true">
          <Plus :size="15" /> Registrer opplæring
        </button>
      </div>
    </div>

    <!-- Stat cards -->
    <div class="stat-grid">
      <StatCard label="Totalt ansatte" :value="store.totalEmployees" />
      <StatCard
        label="Fullført opplæring"
        :value="`${store.completedCount} / ${store.totalEmployees}`"
        value-class="val-green"
      >
        <div class="progress-track">
          <div
            class="progress-bar"
            :style="{ width: (store.completedCount / store.totalEmployees * 100) + '%' }"
          />
        </div>
      </StatCard>
      <StatCard
        label="Utløper snart"
        :value="store.expiringSoonCount"
        value-class="val-amber"
        sub-label="Innen 30 dager"
      />
    </div>

    <!-- Filter -->
    <FilterPanel
      :types="store.trainingTypes"
      v-model:modelType="filterType"
      v-model:modelStatus="filterStatus"
    />

    <!-- Table -->
    <div class="table-card">
      <div v-if="!filtered.length" class="empty-state">
        Ingen resultater matcher filteret.
      </div>

      <div v-else class="table-scroll">
        <table class="data-table">
          <thead>
          <tr>
            <th v-if="editMode" class="col-check">
              <input
                type="checkbox"
                :checked="allSelected"
                @change="toggleSelectAll"
              />
            </th>
            <th>Ansatt</th>
            <th class="hide-mobile">Opplæringstype</th>
            <th class="hide-mobile">Fullført</th>
            <th>Utløper</th>
            <th>Status</th>
            <th v-if="!editMode" class="col-action" />
          </tr>
          </thead>
          <tbody>
          <tr
            v-for="row in filtered"
            :key="row.id"
            :class="{ 'row-selected': editMode && selected.has(row.id), 'row-clickable': editMode }"
            @click="editMode && toggleSelect(row.id)"
          >
            <td v-if="editMode" class="col-check">
              <input
                type="checkbox"
                :checked="selected.has(row.id)"
                @click.stop
                @change="toggleSelect(row.id)"
              />
            </td>

            <td>
              <div class="employee-cell">
                <EmployeeAvatar :initials="row.employee.initials" :color="row.employee.color" size="sm" />
                <div>
                  <p class="emp-name">{{ row.employee.name }}</p>
                  <p class="emp-role">{{ row.employee.role }}</p>
                </div>
              </div>
            </td>

            <td class="hide-mobile cell-text">{{ row.type }}</td>
            <td class="hide-mobile cell-text">{{ row.completed ?? '—' }}</td>

            <td :class="['cell-text', row.status === 'Utløper snart' ? 'expires-soon' : '']">
              {{ row.expires ?? '—' }}
            </td>

            <td><StatusBadge :status="row.status" /></td>

            <td v-if="!editMode" class="col-action">
              <button class="icon-btn" aria-label="Rediger" @click.stop="openEdit(row)">
                <Pencil :size="14" />
              </button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modals -->
    <EditTrainingModal
      v-model="editModal"
      :training="editRow"
      :employee-id="editRow?.employee?.id"
    />
    <RegisterTrainingModal v-model="showRegister" />

    <!-- Delete confirm -->
    <Teleport to="body">
      <div v-if="deleteConfirmRow" class="overlay" @click.self="deleteConfirmRow = null">
        <div class="dialog">
          <h2 class="dialog-title">Slett opplæring</h2>
          <p class="dialog-body">
            Er du sikker på at du vil slette
            <strong>{{ deleteConfirmRow.type }}</strong> for
            <strong>{{ deleteConfirmRow.employee.name }}</strong>?
            Dette kan ikke angres.
          </p>
          <div class="dialog-actions">
            <button class="btn btn-outline" @click="deleteConfirmRow = null">Avbryt</button>
            <button class="btn btn-danger" @click="confirmDelete">Slett</button>
          </div>
        </div>
      </div>
    </Teleport>

  </div>
</template>

<style scoped>
/* ── Layout ─────────────────────────────────────────────────────────────── */
.page {
  max-width: 960px;
  margin: 0 auto;
  padding: 28px 24px 64px;
  font-family: inherit;
}

/* ── Header ─────────────────────────────────────────────────────────────── */
.header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 28px;
}

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

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

/* ── Buttons ────────────────────────────────────────────────────────────── */
.btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 9px 16px;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s;
  border: none;
}

.btn-outline {
  background: #fff;
  border: 1px solid #e7e5e4;
  color: #4b5563;
}
.btn-outline:hover { background: #f5f5f4; }

.btn-register {
  background: #fff;
  border: 1px solid #e7e5e4;
  color: #4f46e5;
}
.btn-register:hover { background: #eef2ff; }

.btn-danger {
  background: #dc2626;
  border: 1px solid #dc2626;
  color: #fff;
}
.btn-danger:hover { background: #b91c1c; }

/* ── Stat grid ──────────────────────────────────────────────────────────── */
.stat-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}

@media (max-width: 600px) {
  .stat-grid { grid-template-columns: repeat(2, 1fr); }
}

/* Progress bar (inside StatCard slot) */
.progress-track {
  margin-top: 10px;
  height: 6px;
  background: #f0fdf4;
  border-radius: 999px;
  overflow: hidden;
}
.progress-bar {
  height: 100%;
  background: #059669;
  border-radius: 999px;
  transition: width 0.5s ease;
}

/* ── Table card ─────────────────────────────────────────────────────────── */
.table-card {
  background: #fff;
  border: 1px solid #e7e5e4;
  border-radius: 16px;
  overflow: hidden;
}

.table-scroll { overflow-x: auto; }

.empty-state {
  padding: 64px 0;
  text-align: center;
  font-size: 0.875rem;
  color: #9ca3af;
}

/* ── Table ──────────────────────────────────────────────────────────────── */
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

/* Checkbox column */
.col-check {
  width: 44px;
  padding-left: 16px;
  padding-right: 8px;
}
.col-check input[type="checkbox"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
  accent-color: #059669;
}

/* Action column */
.col-action { width: 40px; padding: 0 12px; }

/* Selected row */
.row-selected { background: #fff1f2 !important; }
.row-clickable { cursor: pointer; }

/* Cell helpers */
.cell-text { font-size: 0.875rem; color: #4b5563; }
.expires-soon { color: #d97706; font-weight: 600; }

/* Employee cell */
.employee-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}
.emp-name {
  font-size: 0.875rem;
  font-weight: 600;
  color: #111827;
  margin: 0;
}
.emp-role {
  font-size: 0.75rem;
  color: #9ca3af;
  margin: 0;
}

/* Icon button */
.icon-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #d1d5db;
  cursor: pointer;
  transition: background 0.12s, color 0.12s;
}
.icon-btn:hover { background: #f5f5f4; color: #374151; }

/* Hide on mobile */
@media (max-width: 768px) {
  .hide-mobile { display: none; }
}

/* ── Overlay / Dialog ───────────────────────────────────────────────────── */
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 50;
  padding: 16px;
}

.dialog {
  background: #fff;
  border-radius: 16px;
  width: 100%;
  max-width: 380px;
  padding: 24px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
}

.dialog-title {
  font-size: 1rem;
  font-weight: 700;
  color: #111827;
  margin: 0 0 6px;
}

.dialog-body {
  font-size: 0.875rem;
  color: #6b7280;
  margin: 0 0 20px;
  line-height: 1.5;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
