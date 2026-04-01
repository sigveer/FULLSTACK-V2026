<script setup lang="ts">
defineProps<{
  class?: string
}>()
</script>

<template>
  <tr :class="['table-row', $props.class]">
    <slot />
  </tr>
</template>

<style scoped>
.table-row {
  border-bottom: 1px solid hsl(var(--border, 35 18% 88%));
  transition: background-color 150ms ease;
}

.table-row:hover {
  background-color: hsl(var(--muted, 40 18% 93%) / 0.5);
}

.table-row[data-state="selected"] {
  background-color: hsl(var(--muted, 40 18% 93%));
}
</style>
