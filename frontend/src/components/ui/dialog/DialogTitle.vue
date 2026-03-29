<script setup lang="ts">
const props = defineProps<{ class?: string }>()
</script>

<template>
  <h2 :class="['dialog-title', props.class]">
    <slot />
  </h2>
</template>

<style scoped>
.dialog-title {
  font-size: 1.125rem;
  line-height: 1.75rem;
  font-weight: 600;
  letter-spacing: -0.01em;
  margin: 0;
}
</style>
