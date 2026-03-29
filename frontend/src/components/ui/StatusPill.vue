<script setup lang="ts">
withDefaults(
  defineProps<{
    label: string
    tone?: 'neutral' | 'ok' | 'danger' | 'warning' | 'brand'
  }>(),
  {
    tone: 'neutral',
  },
)
</script>

<template>
  <span class="pill" :class="`pill--${tone}`">{{ label }}</span>
</template>

<style scoped>
.pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-pill);
  padding: 6px 12px;
  font-size: 0.95rem;
  font-weight: 600;
  line-height: 1;
  white-space: nowrap;
}

.pill--neutral {
  background: #e9e9e8;
  color: #464b52;
}

.pill--ok {
  background: var(--green-soft);
  color: #2f6f34;
}

.pill--danger {
  background: var(--red-soft);
  color: #902324;
}

.pill--warning {
  background: var(--amber-soft);
  color: #8e5713;
}

.pill--brand {
  background: var(--brand-soft);
  color: #413da9;
}
</style>
