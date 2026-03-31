<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import AppLayout from '@/components/layout/AppLayout.vue'
import { SidebarTrigger } from '@/components/ui/sidebar'
import AdminTrainingView from './AdminTrainingView.vue'
import EmployeeTrainingView from './EmployeeTrainingView.vue'

const auth = useAuthStore()
const canSeeAll = computed(() =>
  ['admin', 'leder', 'styrer'].includes(auth.role ?? '')
)
</script>

<template>
  <AppLayout>
    <template #header>
      <div class="flex items-center gap-3 px-4 py-3 border-b border-stone-200 bg-stone-50">
        <SidebarTrigger />
        <span class="text-xs font-medium bg-emerald-50 text-emerald-700 border border-emerald-200 rounded-full px-3 py-1">
          IK-Alkohol
        </span>
      </div>
    </template>

    <AdminTrainingView    v-if="canSeeAll" />
    <EmployeeTrainingView v-else />
  </AppLayout>
</template>
