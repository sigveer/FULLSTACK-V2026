<script setup lang="ts">
defineProps<{
  class?: string
  disabled?: boolean
}>()
</script>

<template>
  <div
    data-slot="input-group"
    role="group"
    :data-disabled="disabled || undefined"
    :class="['input-group', $props.class]"
  >
    <slot />
  </div>
</template>

<style scoped>
.input-group {
  position: relative;
  display: flex;
  width: 100%;
  align-items: center;
  border-radius: 0.5rem;
  border: 1px solid hsl(var(--input, 35 15% 85%));
  background-color: hsl(var(--card, 40 25% 98%));
  outline: none;
  box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05);
  height: 2.25rem;
  min-width: 0;
  transition: border-color 150ms ease, box-shadow 150ms ease;
}

.input-group:has(textarea) {
  height: auto;
}

.input-group:has([data-slot="input-group-control"]:focus-visible) {
  box-shadow: 0 0 0 2px hsl(var(--ring, 245 43% 52%) / 0.2);
  border-color: hsl(var(--primary, 245 43% 52%) / 0.5);
}

.input-group[data-disabled="true"] {
  opacity: 0.5;
  pointer-events: none;
}
</style>
