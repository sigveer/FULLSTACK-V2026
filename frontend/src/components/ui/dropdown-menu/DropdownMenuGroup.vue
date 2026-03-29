<script setup lang="ts">
defineProps<{
  class?: string
}>()
</script>

<template>
  <div :class="$props.class" role="group">
    <slot />
  </div>
</template>
