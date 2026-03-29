<script setup lang="ts">
defineProps<{
  class?: string
}>()
</script>

<template>
  <div :class="['dropdown-separator', $props.class]" role="separator" />
</template>

<style scoped>
.dropdown-separator {
  margin: 0.25rem -0.25rem;
  height: 1px;
  background-color: hsl(var(--muted, 40 20% 93%));
}
</style>
