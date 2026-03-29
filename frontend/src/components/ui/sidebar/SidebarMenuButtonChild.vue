<script setup lang="ts">
import type { Component } from "vue"
import { useSidebar } from "./utils"

export interface SidebarMenuButtonProps {
  as?: string | Component
  variant?: "default" | "outline"
  size?: "default" | "sm" | "lg"
  isActive?: boolean
  class?: string | Record<string, unknown> | unknown[] | null | false
}

const props = withDefaults(defineProps<SidebarMenuButtonProps>(), {
  as: "button",
  variant: "default",
  size: "default",
})

const { state, collapsible } = useSidebar()
</script>

<template>
  <component
    :is="as"
    data-sidebar="menu-button"
    :data-size="size"
    :data-active="isActive || undefined"
    :class="[
      'sidebar-menu-btn',
      `sidebar-menu-btn--${variant}`,
      `sidebar-menu-btn--${size}`,
      isActive && 'sidebar-menu-btn--active',
      state === 'collapsed' && collapsible === 'icon' && 'sidebar-menu-btn--icon-only',
      props.class,
    ]"
    v-bind="$attrs"
  >
    <slot />
  </component>
</template>

<style scoped>
.sidebar-menu-btn {
  display: flex;
  width: 100%;
  align-items: center;
  gap: 0.5rem;
  overflow: hidden;
  border-radius: 0.375rem;
  padding: 0.5rem;
  text-align: left;
  font-size: 0.875rem;
  line-height: 1.25rem;
  outline: none;
  border: none;
  background: none;
  cursor: pointer;
  color: inherit;
  text-decoration: none;
  transition: width 200ms ease, height 200ms ease, padding 200ms ease;
}

.sidebar-menu-btn:hover {
  background-color: hsl(var(--sidebar-accent, 250 40% 95%));
  color: hsl(var(--sidebar-accent-foreground, 245 43% 42%));
}

.sidebar-menu-btn:focus-visible {
  box-shadow: 0 0 0 2px hsl(var(--sidebar-ring, 245 43% 52%));
}

.sidebar-menu-btn:disabled,
.sidebar-menu-btn[aria-disabled="true"] {
  pointer-events: none;
  opacity: 0.5;
}

.sidebar-menu-btn--active {
  background-color: hsl(var(--sidebar-accent, 250 40% 95%));
  font-weight: 500;
  color: hsl(var(--sidebar-accent-foreground, 245 43% 42%));
}

/* Variants */
.sidebar-menu-btn--outline {
  background-color: hsl(var(--background, 43 30% 96%));
  box-shadow: 0 0 0 1px hsl(var(--sidebar-border, 35 15% 90%));
}

.sidebar-menu-btn--outline:hover {
  background-color: hsl(var(--sidebar-accent, 250 40% 95%));
  color: hsl(var(--sidebar-accent-foreground, 245 43% 42%));
  box-shadow: 0 0 0 1px hsl(var(--sidebar-accent, 250 40% 95%));
}

/* Sizes */
.sidebar-menu-btn--default {
  height: 2rem;
}

.sidebar-menu-btn--sm {
  height: 1.75rem;
  font-size: 0.75rem;
  line-height: 1rem;
}

.sidebar-menu-btn--lg {
  height: 3rem;
}

/* Icon-only collapsed state */
.sidebar-menu-btn--icon-only {
  width: 2rem !important;
  height: 2rem !important;
  padding: 0.5rem !important;
}

.sidebar-menu-btn--icon-only.sidebar-menu-btn--lg {
  padding: 0 !important;
}

/* Truncate last span */
.sidebar-menu-btn :deep(> span:last-child) {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* SVG sizing */
.sidebar-menu-btn :deep(svg) {
  width: 1rem;
  height: 1rem;
  flex-shrink: 0;
}
</style>
