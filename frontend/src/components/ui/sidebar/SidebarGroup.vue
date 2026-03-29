<script setup lang="ts">
const props = defineProps<{
  class?: string
}>()
</script>

<template>
  <div data-sidebar="group" :class="['sidebar-group', props.class]">
    <slot />
  </div>
</template>

<style scoped>
.sidebar-group {
  position: relative;
  display: flex;
  width: 100%;
  min-width: 0;
  flex-direction: column;
  padding: 0.5rem;
}
</style>
