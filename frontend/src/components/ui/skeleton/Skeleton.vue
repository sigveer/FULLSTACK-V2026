<script setup lang="ts">
const props = defineProps<{
  class?: string
}>()
</script>

<template>
  <div :class="['skeleton', props.class]" />
</template>

<style scoped>
.skeleton {
  border-radius: 0.375rem;
  background-color: hsl(var(--primary, 245 43% 52%) / 0.1);
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}
</style>
