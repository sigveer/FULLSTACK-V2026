<script setup lang="ts">
const props = defineProps<{
  class?: string
}>()
</script>

<template>
  <div data-sidebar="footer" :class="['sidebar-footer', props.class]">
    <slot />
  </div>
</template>

<style scoped>
.sidebar-footer {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 0.5rem;
}
</style>
