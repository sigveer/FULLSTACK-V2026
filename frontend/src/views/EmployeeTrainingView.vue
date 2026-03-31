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
  <div class="max-w-3xl mx-auto px-4 sm:px-6 py-7 pb-16">
    <div class="mb-7">
      <h1 class="text-2xl sm:text-3xl font-bold text-gray-900 tracking-tight">Min opplæring</h1>
      <p class="text-sm text-gray-400 mt-0.5">Oversikt over din opplæringsstatus</p>
    </div>

    <div
      v-if="me"
      class="flex items-center gap-4 bg-white border border-stone-200 rounded-2xl px-5 py-4 mb-6"
    >
      <EmployeeAvatar :initials="me.initials" :color="me.color" size="lg" />
      <div>
        <p class="text-lg font-bold text-gray-900">{{ me.name }}</p>
        <p class="text-sm text-gray-400">{{ me.role }}</p>
      </div>
    </div>

    <div class="grid grid-cols-3 gap-3 mb-6">
      <StatCard label="Gyldige" :value="valid.length" value-class="text-emerald-700">
        <Award class="text-emerald-500 mt-1" :size="18" />
      </StatCard>
      <StatCard label="Utløper snart" :value="expiring.length" value-class="text-amber-600">
        <Clock class="text-amber-500 mt-1" :size="18" />
      </StatCard>
      <StatCard label="Mangler" :value="missing.length" value-class="text-red-600">
        <AlertTriangle class="text-red-400 mt-1" :size="18" />
      </StatCard>
    </div>

    <div class="bg-white border border-stone-200 rounded-2xl overflow-hidden">
      <div v-if="!trainings.length" class="py-14 text-center text-sm text-gray-400">
        Ingen opplæring registrert.
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full border-collapse">
          <thead>
          <tr class="border-b border-stone-100">
            <th class="text-left text-xs font-semibold text-gray-400 px-5 py-3">Opplæringstype</th>
            <th class="text-left text-xs font-semibold text-gray-400 px-5 py-3 hidden sm:table-cell">Fullført</th>
            <th class="text-left text-xs font-semibold text-gray-400 px-5 py-3">Utløper</th>
            <th class="text-left text-xs font-semibold text-gray-400 px-5 py-3">Status</th>
          </tr>
          </thead>
          <tbody>
          <tr
            v-for="t in trainings"
            :key="t.id"
            class="border-b border-stone-100 last:border-b-0 hover:bg-stone-50/60 transition-colors"
          >
            <td class="px-5 py-3.5 text-sm font-semibold text-gray-900">{{ t.type }}</td>
            <td class="px-5 py-3.5 text-sm text-gray-600 hidden sm:table-cell">
              {{ t.completed ?? '—' }}
            </td>
            <td
              class="px-5 py-3.5 text-sm"
              :class="t.status === 'Utløper snart' ? 'text-amber-600 font-semibold' : 'text-gray-600'"
            >
              {{ t.expires ?? '—' }}
            </td>
            <td class="px-5 py-3.5">
              <StatusBadge :status="t.status" />
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

  </div>
</template>
