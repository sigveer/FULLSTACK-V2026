<script setup lang="ts">
defineProps<{
  class?: string
}>()
</script>

<template>
  <span :class="['input-group-text', $props.class]">
    <slot />
  </span>
</template>

<style scoped>
.input-group-text {
  color: hsl(var(--muted-foreground, 24 5% 46%));
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
}

.input-group-text :deep(svg:not([class*="size-"])) {
  width: 1rem;
  height: 1rem;
  pointer-events: none;
}
</style>
