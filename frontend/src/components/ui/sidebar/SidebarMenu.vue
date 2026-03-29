<script setup lang="ts">
const props = defineProps<{
  class?: string
}>()
</script>

<template>
  <ul data-sidebar="menu" :class="['sidebar-menu', props.class]">
    <slot />
  </ul>
</template>

<style scoped>
.sidebar-menu {
  display: flex;
  width: 100%;
  min-width: 0;
  flex-direction: column;
  gap: 0.25rem;
  list-style: none;
  margin: 0;
  padding: 0;
}
</style>
