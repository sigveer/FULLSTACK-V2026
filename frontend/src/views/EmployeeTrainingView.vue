<script setup lang="ts">
import { computed } from 'vue'
import { Award, Clock, AlertTriangle } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import { useTrainingStore } from '@/stores/training.ts'
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
  <div class="max-w-2xl mx-auto px-4 sm:px-6 py-7 pb-16">

    <div class="mb-6">
      <h1 class="text-2xl sm:text-3xl font-bold text-gray-900 tracking-tight">Min opplæring</h1>
      <p class="text-sm text-gray-400 mt-0.5">Oversikt over din opplæringsstatus</p>
    </div>

    <div class="flex items-center gap-4 bg-white border border-stone-200 rounded-2xl px-5 py-4 mb-6">
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
      <div
        v-for="t in trainings"
        :key="t.id"
        class="flex items-center justify-between gap-3 px-5 py-4 border-b border-stone-100 last:border-b-0 flex-wrap"
      >
        <p class="text-sm font-semibold text-gray-900 flex-1 min-w-36">{{ t.type }}</p>
        <div class="flex gap-4 text-xs text-gray-500 flex-wrap">
          <span v-if="t.completed">Fullført: <strong class="text-gray-700">{{ t.completed }}</strong></span>
          <span v-else class="text-gray-300">Ikke fullført</span>
          <span
            v-if="t.expires"
            :class="t.status === 'Utløper snart' ? 'text-amber-600 font-semibold' : ''"
          >
            Utløper: <strong>{{ t.expires }}</strong>
          </span>
        </div>
        <StatusBadge :status="t.status" />
      </div>
    </div>

  </div>
</template>
