<script setup lang="ts">
const props = defineProps<{
  class?: string
}>()
</script>

<template>
  <div :class="['select-label', props.class]">
    <slot />
  </div>
</template>

<style scoped>
.select-label {
  padding: 0.375rem 0.5rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
  font-weight: 600;
}
</style>
