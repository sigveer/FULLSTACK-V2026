<script setup lang="ts">
withDefaults(defineProps<{
  label: string
  value: string | number
  variant?: 'neutral' | 'open' | 'in-progress' | 'resolved'
  subLabel?: string
  valueClass?: string
}>(), { 
  variant: 'neutral',
  valueClass: '' 
})
</script>

<template>
  <div :class="['overview-card', `overview-card--${variant}`]">
    <p class="card-label">{{ label }}</p>
    <p class="card-value" :class="valueClass">{{ value }}</p>
    <slot />
    <p v-if="subLabel" class="card-sub">{{ subLabel }}</p>
  </div>
</template>

<style scoped>
.overview-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
  border-radius: var(--radius-lg);
  padding: 16px;
  border: 2px solid hsl(var(--border));
  background: hsl(var(--card));
  min-height: 6.25rem;
}

.card-label {
  font-size: 0.875rem;
  color: hsl(var(--muted-foreground));
  margin: 0;
  font-weight: 500;
}

.card-value {
  font-size: 2rem;
  font-weight: 700;
  line-height: 1;
  color: #111827;
  margin: 0;
}

.card-sub {
  font-size: 0.75rem;
  color: hsl(var(--muted-foreground));
  margin: 4px 0 0;
}

/* Variants */
.overview-card--neutral {
  background: #ffffff;
  border-color: #d1d5db;
}

.overview-card--neutral .card-value {
  color: #111827;
}

.overview-card--open {
  background: #f5e8ea;
  border-color: #e0aeb5;
}

.overview-card--open .card-value {
  color: #a62929;
}

.overview-card--in-progress {
  background: #f1e7d6;
  border-color: #e0bf81;
}

.overview-card--in-progress .card-value {
  color: #946013;
}

.overview-card--resolved {
  background: #e4eddc;
  border-color: #b7d18e;
}

.overview-card--resolved .card-value {
  color: #3c8f2c;
}

/* Support for color classes from props */
:deep(.val-green), .val-green { color: #059669; }
:deep(.val-amber), .val-amber { color: #d97706; }
:deep(.val-red), .val-red { color: #dc2626; }

:deep(.text-emerald-700) { color: #059669; }
:deep(.text-amber-600) { color: #d97706; }
:deep(.text-red-600) { color: #dc2626; }
:deep(.text-gray-900) { color: #111827; }
</style>
