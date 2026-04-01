<script setup lang="ts">
import type { Training } from '@/stores/training'
defineProps<{ status: Training['status'] }>()
</script>

<template>
  <span class="badge" :class="{
    'badge-green':  status === 'Gyldig',
    'badge-amber':  status === 'Utløper snart',
    'badge-red':    status === 'Mangler',
  }">
    {{ status }}
  </span>
</template>

<style scoped>
.badge {
  display: inline-block;
  padding: 3px 12px;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 500;
  white-space: nowrap;
  border: 1px solid transparent;
}

.badge-green {
  background: #f0fdf4;
  color: #065f46;
  border-color: #a7f3d0;
}

.badge-amber {
  background: #fffbeb;
  color: #92400e;
  border-color: #fde68a;
}

.badge-red {
  background: #fff1f2;
  color: #9f1239;
  border-color: #fecdd3;
}
</style>
