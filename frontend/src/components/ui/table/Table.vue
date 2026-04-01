<script setup lang="ts">
defineProps<{
  class?: string
}>()
</script>

<template>
  <div class="table-wrapper">
    <table :class="['table', $props.class]">
      <slot />
    </table>
  </div>
</template>

<style scoped>
.table-wrapper {
  position: relative;
  width: 100%;
  overflow: auto;
}

.table {
  width: 100%;
  caption-side: bottom;
  font-size: 0.875rem;
  line-height: 1.25rem;
  border-collapse: collapse;
}
</style>
