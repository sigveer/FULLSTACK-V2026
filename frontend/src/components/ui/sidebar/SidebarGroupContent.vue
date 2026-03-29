<script setup lang="ts">
const props = defineProps<{
  class?: string
}>()
</script>

<template>
  <div data-sidebar="group-content" :class="['sidebar-group-content', props.class]">
    <slot />
  </div>
</template>

<style scoped>
.sidebar-group-content {
  width: 100%;
  font-size: 0.875rem;
  line-height: 1.25rem;
}
</style>
