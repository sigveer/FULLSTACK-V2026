<script setup lang="ts">
import { useSidebar } from "./utils"

const props = withDefaults(defineProps<{
  showOnHover?: boolean
  class?: string
  as?: string
}>(), {
  as: "button",
})

const { state, collapsible } = useSidebar()
</script>

<template>
  <component
    :is="as"
    data-sidebar="menu-action"
    :class="[
      'sidebar-menu-action',
      showOnHover && 'sidebar-menu-action--hover-only',
      props.class,
    ]"
    :style="{
      display: state === 'collapsed' && collapsible === 'icon' ? 'none' : undefined,
    }"
  >
    <slot />
  </component>
</template>

<style scoped>
.sidebar-menu-action {
  position: absolute;
  right: 0.25rem;
  top: 0.375rem;
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

.sidebar-menu-action:hover {
  background-color: hsl(var(--sidebar-accent, 250 40% 95%));
  color: hsl(var(--sidebar-accent-foreground, 245 43% 42%));
}

.sidebar-menu-action:focus-visible {
  box-shadow: 0 0 0 2px hsl(var(--sidebar-ring, 245 43% 52%));
}

.sidebar-menu-action :deep(svg) {
  width: 1rem;
  height: 1rem;
  flex-shrink: 0;
}

@media (min-width: 768px) {
  .sidebar-menu-action--hover-only {
    opacity: 0;
  }
}

.sidebar-menu-item:hover .sidebar-menu-action--hover-only,
.sidebar-menu-item:focus-within .sidebar-menu-action--hover-only {
  opacity: 1;
}
</style>
