<script setup lang="ts">
const props = defineProps<{
  class?: string
}>()
</script>

<template>
  <div :class="['select-separator', props.class]" />
</template>

<style scoped>
.select-separator {
  margin: 0.25rem -0.25rem;
  height: 1px;
  background-color: hsl(var(--muted, 40 15% 92%));
}
</style>
