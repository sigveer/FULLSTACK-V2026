<script setup lang="ts">
import type { Training } from '@/stores/training'

defineProps<{ status: Training['status'] }>()

const config: Record<Training['status'], string> = {
  'Gyldig':        'bg-emerald-50 text-emerald-700 border border-emerald-200',
  'Utløper snart': 'bg-amber-50  text-amber-700  border border-amber-200',
  'Mangler':       'bg-red-50    text-red-700    border border-red-200',
}
</script>

<template>
  <span :class="['inline-block px-3 py-0.5 rounded-full text-xs font-medium whitespace-nowrap', config[status]]">
    {{ status }}
  </span>
</template>
