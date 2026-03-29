<script setup lang="ts">
const props = defineProps<{ class?: string }>()
</script>

<template>
  <p :class="['sheet-description', props.class]">
    <slot />
  </p>
</template>

<style scoped>
.sheet-description {
  font-size: 0.875rem;
  line-height: 1.25rem;
  color: hsl(var(--muted-foreground, 24 5% 46%));
  margin: 0;
}
</style>
