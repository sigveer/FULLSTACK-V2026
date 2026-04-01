<script setup lang="ts">
withDefaults(defineProps<{
  label: string
  value: string | number
  subLabel?: string
  valueClass?: string
}>(), { valueClass: '' })
</script>

<template>
  <div class="stat-card">
    <p class="stat-label">{{ label }}</p>
    <p class="stat-value" :class="valueClass">{{ value }}</p>
    <slot />
    <p v-if="subLabel" class="stat-sub">{{ subLabel }}</p>
  </div>
</template>

<style scoped>
.stat-card {
  background: #fff;
  border: 1px solid #e7e5e4;
  border-radius: 16px;
  padding: 20px;
}

.stat-label {
  font-size: 0.78rem;
  color: #9ca3af;
  margin: 0 0 6px;
}

.stat-value {
  font-size: 2.2rem;
  font-weight: 700;
  line-height: 1;
  color: #111827;
  margin: 0;
}

:deep(.val-green), .val-green { color: #059669; }
:deep(.val-amber), .val-amber { color: #d97706; }
:deep(.val-red),   .val-red   { color: #dc2626; }

:deep(.text-emerald-700) { color: #059669; }
:deep(.text-amber-600)   { color: #d97706; }
:deep(.text-red-600)     { color: #dc2626; }
:deep(.text-gray-900)    { color: #111827; }

.stat-sub {
  font-size: 0.78rem;
  color: #d97706;
  margin: 4px 0 0;
}
</style>
