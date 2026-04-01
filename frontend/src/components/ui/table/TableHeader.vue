<script setup lang="ts">
defineProps<{
  class?: string
}>()
</script>

<template>
  <thead :class="['table-header', $props.class]">
    <slot />
  </thead>
</template>

<style scoped>
.table-header :deep(tr) {
  border-bottom: 1px solid hsl(var(--border, 35 18% 88%));
}
</style>
