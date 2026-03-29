<script setup lang="ts">
const props = defineProps<{ class?: string }>()
</script>

<template>
  <div :class="['dialog-header', props.class]">
    <slot />
  </div>
</template>

<style scoped>
.dialog-header {
  display: flex;
  flex-direction: column;
  gap: 0.375rem;
  text-align: center;
}

@media (min-width: 640px) {
  .dialog-header {
    text-align: left;
  }
}
</style>
