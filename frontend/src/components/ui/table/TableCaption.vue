<script setup lang="ts">
defineProps<{
  class?: string
}>()
</script>

<template>
  <caption :class="['table-caption', $props.class]">
    <slot />
  </caption>
</template>

<style scoped>
.table-caption {
  margin-top: 1rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
  color: hsl(var(--muted-foreground, 24 5% 46%));
}
</style>
