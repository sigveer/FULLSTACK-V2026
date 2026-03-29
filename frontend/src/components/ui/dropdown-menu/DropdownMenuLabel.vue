<script setup lang="ts">
defineProps<{
  class?: string
  inset?: boolean
}>()
</script>

<template>
  <div :class="['dropdown-label', inset && 'dropdown-label--inset', $props.class]">
    <slot />
  </div>
</template>

<style scoped>
.dropdown-label {
  padding: 0.375rem 0.5rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
  font-weight: 600;
}

.dropdown-label--inset {
  padding-left: 2rem;
}
</style>
