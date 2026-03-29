<script setup lang="ts">
defineProps<{
  class?: string
  disabled?: boolean
}>()
</script>

<template>
  <button
    type="button"
    :disabled="disabled || undefined"
    :class="['input-group-button', $props.class]"
  >
    <slot />
  </button>
</template>

<style scoped>
.input-group-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
  height: 1.5rem;
  width: 1.5rem;
  padding: 0;
  border-radius: calc(var(--radius, 0.75rem) - 5px);
  border: none;
  background: transparent;
  color: hsl(var(--muted-foreground, 24 5% 46%));
  font-size: 0.75rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 150ms ease, color 150ms ease;
  outline: none;
  flex-shrink: 0;
}

.input-group-button:hover {
  background-color: hsl(var(--accent, 250 40% 95%));
  color: hsl(var(--accent-foreground, 245 43% 42%));
}

.input-group-button:active {
  background-color: hsl(var(--accent, 250 40% 95%) / 0.8);
}

.input-group-button:disabled {
  opacity: 0.5;
  pointer-events: none;
}

.input-group-button :deep(svg:not([class*="size-"])) {
  width: 1rem;
  height: 1rem;
}
</style>
