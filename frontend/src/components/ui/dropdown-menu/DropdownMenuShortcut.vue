<script setup lang="ts">
defineProps<{
  class?: string
}>()
</script>

<template>
  <span :class="['dropdown-shortcut', $props.class]">
    <slot />
  </span>
</template>

<style scoped>
.dropdown-shortcut {
  margin-left: auto;
  font-size: 0.75rem;
  line-height: 1rem;
  letter-spacing: 0.1em;
  opacity: 0.6;
}
</style>
