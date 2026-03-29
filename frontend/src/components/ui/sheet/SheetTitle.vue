<script setup lang="ts">
const props = defineProps<{ class?: string }>()
</script>

<template>
  <h2 :class="['sheet-title', props.class]">
    <slot />
  </h2>
</template>

<style scoped>
.sheet-title {
  font-size: 1.125rem;
  line-height: 1.75rem;
  font-weight: 600;
  color: hsl(var(--foreground, 24 10% 10%));
  margin: 0;
}
</style>
