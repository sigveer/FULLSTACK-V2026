<script setup lang="ts">
defineProps<{
  class?: string
}>()
</script>

<template>
  <tbody :class="['table-body', $props.class]">
    <slot />
  </tbody>
</template>

<style scoped>
.table-body :deep(tr:last-child) {
  border-bottom: 0;
}
</style>
