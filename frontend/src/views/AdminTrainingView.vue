<script setup lang="ts">
import { ref, computed } from 'vue'
import { Plus } from 'lucide-vue-next'
import type { TrainingRow } from '@/stores/training.ts'
import { useTrainingStore } from '@/stores/training.ts'
import StatCard from '@/components/training/StatCard.vue'
import FilterPanel from '@/components/training/FilterPanel.vue'
import TrainingTable from '@/components/training/TrainingTable.vue'
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

const grouped = computed(() => {
  const g: Record<string, TrainingRow[]> = {}
  filtered.value.forEach(t => { (g[t.type] ??= []).push(t) })
  return g
})

const showRegister   = ref(false)
const editModal      = ref(false)
const editRow        = ref<TrainingRow | null>(null)
const deleteConfirmRow = ref<TrainingRow | null>(null)

function openEdit(row: TrainingRow): void {
  editRow.value  = row
  editModal.value = true
}

function openDeleteConfirm(row: TrainingRow): void {
  deleteConfirmRow.value = row
}

function confirmDelete(): void {
  if (!deleteConfirmRow.value) return
  store.deleteTraining(deleteConfirmRow.value.employee.id, deleteConfirmRow.value.id)
  deleteConfirmRow.value = null
}
</script>

<template>
  <div class="max-w-5xl mx-auto px-4 sm:px-6 py-7 pb-16">

    <div class="flex items-start justify-between flex-wrap gap-3 mb-7">
      <div>
        <h1 class="text-2xl sm:text-3xl font-bold text-gray-900 tracking-tight">Opplæring og sertifiseringer</h1>
        <p class="text-sm text-gray-400 mt-0.5">Oversikt over ansattes opplæringsstatus</p>
      </div>
      <button
        class="flex items-center gap-1.5 bg-emerald-700 text-white rounded-xl px-4 py-2.5 text-sm font-semibold hover:bg-emerald-800 transition-colors"
        @click="showRegister = true"
      >
        <Plus :size="16" /> Registrer opplæring
      </button>
    </div>

    <div class="grid grid-cols-2 sm:grid-cols-3 gap-3 mb-6">
      <StatCard label="Totalt ansatte" :value="store.totalEmployees" />
      <StatCard label="Fullført opplæring" :value="`${store.completedCount} / ${store.totalEmployees}`" value-class="text-emerald-700">
        <div class="mt-2.5 h-1.5 bg-emerald-100 rounded-full overflow-hidden">
          <div class="h-full bg-emerald-600 rounded-full transition-all duration-500" :style="{ width: (store.completedCount / store.totalEmployees * 100) + '%' }" />
        </div>
      </StatCard>
      <StatCard
        label="Utløper snart"
        :value="store.expiringSoonCount"
        value-class="text-amber-600"
        sub-label="Innen 30 dager"
        class="col-span-2 sm:col-span-1"
      />
    </div>

    <FilterPanel
      :types="store.trainingTypes"
      v-model:modelType="filterType"
      v-model:modelStatus="filterStatus"
    />

    <TrainingTable
      :grouped-trainings="grouped"
      @edit="openEdit"
      @delete="openDeleteConfirm"
    />

    <EditTrainingModal
      v-model="editModal"
      :training="editRow"
      :employee-id="editRow?.employee?.id"
    />

    <RegisterTrainingModal v-model="showRegister" />

    <Teleport to="body">
      <div v-if="deleteConfirmRow" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4" @click.self="deleteConfirmRow = null">
        <div class="bg-white rounded-2xl w-full max-w-sm shadow-2xl p-6">
          <h2 class="text-base font-bold text-gray-900 mb-1">Slett opplæring</h2>
          <p class="text-sm text-gray-500 mb-5">
            Er du sikker på at du vil slette <strong>{{ deleteConfirmRow.type }}</strong> for
            <strong>{{ deleteConfirmRow.employee.name }}</strong>? Dette kan ikke angres.
          </p>
          <div class="flex justify-end gap-2">
            <button class="border border-stone-200 rounded-lg px-4 py-2 text-sm text-gray-500 hover:bg-stone-50 transition-colors" @click="deleteConfirmRow = null">Avbryt</button>
            <button class="bg-red-600 text-white rounded-lg px-4 py-2 text-sm font-semibold hover:bg-red-700 transition-colors" @click="confirmDelete">Slett</button>
          </div>
        </div>
      </div>
    </Teleport>

  </div>
</template>
