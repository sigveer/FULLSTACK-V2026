<script setup lang="ts">
defineProps<{
  class?: string
}>()
</script>

<template>
  <th :class="['table-head', $props.class]">
    <slot />
  </th>
</template>

<style scoped>
.table-head {
  height: 2.75rem;
  padding: 0 1rem;
  text-align: left;
  vertical-align: middle;
  font-weight: 500;
  color: hsl(var(--muted-foreground, 24 5% 46%));
}

.table-head:has([role="checkbox"]) {
  padding-right: 0;
}

.table-head > [role="checkbox"] {
  transform: translateY(0.125rem);
}
</style>
