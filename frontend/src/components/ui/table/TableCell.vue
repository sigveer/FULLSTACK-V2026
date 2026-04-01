<script setup lang="ts">
defineProps<{
  class?: string
  colspan?: number
}>()
</script>

<template>
  <td :class="['table-cell', $props.class]" :colspan="colspan">
    <slot />
  </td>
</template>

<style scoped>
.table-cell {
  padding: 0.75rem 1rem;
  vertical-align: middle;
}

.table-cell:has([role="checkbox"]) {
  padding-right: 0;
}

.table-cell > [role="checkbox"] {
  transform: translateY(0.125rem);
}
</style>
