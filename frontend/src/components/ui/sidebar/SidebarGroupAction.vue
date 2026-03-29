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
    :is="as || 'button'"
    data-sidebar="group-action"
    :class="['sidebar-group-action', props.class]"
    :style="{
      display: state === 'collapsed' && collapsible === 'icon' ? 'none' : undefined,
    }"
  >
    <slot />
  </component>
</template>

<style scoped>
.sidebar-group-action {
  position: absolute;
  right: 0.75rem;
  top: 0.875rem;
  display: flex;
  aspect-ratio: 1;
  width: 1.25rem;
  align-items: center;
  justify-content: center;
  border-radius: 0.375rem;
  padding: 0;
  border: none;
  background: none;
  cursor: pointer;
  color: hsl(var(--sidebar-foreground, 24 10% 10%));
  outline: none;
  transition: transform 150ms ease;
}

.sidebar-group-action:hover {
  background-color: hsl(var(--sidebar-accent, 250 40% 95%));
  color: hsl(var(--sidebar-accent-foreground, 245 43% 42%));
}

.sidebar-group-action:focus-visible {
  box-shadow: 0 0 0 2px hsl(var(--sidebar-ring, 245 43% 52%));
}

.sidebar-group-action :deep(svg) {
  width: 1rem;
  height: 1rem;
  flex-shrink: 0;
}
</style>
