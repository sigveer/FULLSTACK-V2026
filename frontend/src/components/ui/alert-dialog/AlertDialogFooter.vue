<script setup lang="ts">
const props = defineProps<{ class?: string }>()
</script>

<template>
  <div :class="['alert-footer', props.class]">
    <slot />
  </div>
</template>

<style scoped>
.alert-footer {
  display: flex;
  flex-direction: column-reverse;
}

@media (min-width: 640px) {
  .alert-footer {
    flex-direction: row;
    justify-content: flex-end;
    column-gap: 0.5rem;
  }
}
</style>
