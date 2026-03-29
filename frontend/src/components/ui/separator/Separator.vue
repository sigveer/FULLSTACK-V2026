<script setup lang="ts">
const props = withDefaults(defineProps<{
  orientation?: "horizontal" | "vertical"
  decorative?: boolean
  class?: string
}>(), {
  orientation: "horizontal",
  decorative: true,
})
</script>

<template>
  <div
    :role="decorative ? 'none' : 'separator'"
    :aria-orientation="decorative ? undefined : orientation"
    :data-orientation="orientation"
    :class="['separator', `separator--${orientation}`, props.class]"
  />
</template>

<style scoped>
.separator {
  flex-shrink: 0;
  background-color: hsl(var(--border, 35 15% 90%));
}

.separator--horizontal {
  height: 1px;
  width: 100%;
}

.separator--vertical {
  width: 1px;
  height: 100%;
}
</style>
