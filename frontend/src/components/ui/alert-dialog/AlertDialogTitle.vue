<script setup lang="ts">
const props = defineProps<{ class?: string }>()
</script>

<template>
  <h2 :class="['alert-title', props.class]">
    <slot />
  </h2>
</template>

<style scoped>
.alert-title {
  font-size: 1.125rem;
  line-height: 1.75rem;
  font-weight: 600;
  margin: 0;
}
</style>
