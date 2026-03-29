<script setup lang="ts">
const props = defineProps<{ class?: string }>()
</script>

<template>
  <div :class="['alert-header', props.class]">
    <slot />
  </div>
</template>

<style scoped>
.alert-header {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  text-align: center;
}

@media (min-width: 640px) {
  .alert-header {
    text-align: left;
  }
}
</style>
