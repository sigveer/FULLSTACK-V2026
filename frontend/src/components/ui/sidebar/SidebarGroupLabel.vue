<script setup lang="ts">
import { useSidebar } from "./utils"

const props = defineProps<{
  class?: string
  as?: string
}>()

const { state, collapsible } = useSidebar()
</script>

<template>
  <component
    :is="as || 'div'"
    data-sidebar="group-label"
    :class="['sidebar-group-label', props.class]"
    :style="{
      marginTop: state === 'collapsed' && collapsible === 'icon' ? '-2rem' : undefined,
      opacity: state === 'collapsed' && collapsible === 'icon' ? 0 : undefined,
    }"
  >
    <slot />
  </component>
</template>

<style scoped>
.sidebar-group-label {
  display: flex;
  height: 2rem;
  flex-shrink: 0;
  align-items: center;
  border-radius: 0.375rem;
  padding: 0 0.5rem;
  font-size: 0.75rem;
  line-height: 1rem;
  font-weight: 500;
  color: hsl(var(--sidebar-foreground, 24 10% 10%) / 0.7);
  outline: none;
  transition: margin 200ms linear, opacity 200ms linear;
}

.sidebar-group-label :deep(svg) {
  width: 1rem;
  height: 1rem;
  flex-shrink: 0;
}
</style>
