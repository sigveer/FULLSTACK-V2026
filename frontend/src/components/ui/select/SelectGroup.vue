<script setup lang="ts">
const props = defineProps<{
  class?: string
}>()
</script>

<template>
  <div role="group" :class="['select-group', props.class]">
    <slot />
  </div>
</template>

<style scoped>
.select-group {
  padding: 0.25rem;
  width: 100%;
}
</style>
