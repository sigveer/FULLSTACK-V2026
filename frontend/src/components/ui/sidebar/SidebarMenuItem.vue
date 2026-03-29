<script setup lang="ts">
const props = defineProps<{
  class?: string
}>()
</script>

<template>
  <li data-sidebar="menu-item" :class="['sidebar-menu-item', props.class]">
    <slot />
  </li>
</template>

<style scoped>
.sidebar-menu-item {
  position: relative;
  list-style: none;
}
</style>
