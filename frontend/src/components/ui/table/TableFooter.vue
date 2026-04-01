<script setup lang="ts">
defineProps<{
  class?: string
}>()
</script>

<template>
  <tfoot :class="['table-footer', $props.class]">
    <slot />
  </tfoot>
</template>

<style scoped>
.table-footer {
  border-top: 1px solid hsl(var(--border, 35 18% 88%));
  background-color: hsl(var(--muted, 40 18% 93%) / 0.5);
  font-weight: 500;
}

.table-footer :deep(tr:last-child) {
  border-bottom: 0;
}
</style>
