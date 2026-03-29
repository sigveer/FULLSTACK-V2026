<script setup lang="ts">
const props = withDefaults(defineProps<{
  as?: string
  size?: "sm" | "md"
  isActive?: boolean
  class?: string
}>(), {
  as: "a",
  size: "md",
})
</script>

<template>
  <component
    :is="as"
    data-sidebar="menu-sub-button"
    :data-size="size"
    :data-active="isActive || undefined"
    :class="[
      'sidebar-menu-sub-btn',
      `sidebar-menu-sub-btn--${size}`,
      isActive && 'sidebar-menu-sub-btn--active',
      props.class,
    ]"
    v-bind="$attrs"
  >
    <slot />
  </component>
</template>

<style scoped>
.sidebar-menu-sub-btn {
  display: flex;
  height: 1.75rem;
  min-width: 0;
  translate: -1px 0;
  align-items: center;
  gap: 0.5rem;
  overflow: hidden;
  border-radius: 0.375rem;
  padding: 0 0.5rem;
  color: hsl(var(--sidebar-foreground, 24 10% 10%));
  outline: none;
  text-decoration: none;
}

.sidebar-menu-sub-btn:hover {
  background-color: hsl(var(--sidebar-accent, 250 40% 95%));
  color: hsl(var(--sidebar-accent-foreground, 245 43% 42%));
}

.sidebar-menu-sub-btn:focus-visible {
  box-shadow: 0 0 0 2px hsl(var(--sidebar-ring, 245 43% 52%));
}

.sidebar-menu-sub-btn:disabled,
.sidebar-menu-sub-btn[aria-disabled="true"] {
  pointer-events: none;
  opacity: 0.5;
}

.sidebar-menu-sub-btn--active {
  background-color: hsl(var(--sidebar-accent, 250 40% 95%));
  color: hsl(var(--sidebar-accent-foreground, 245 43% 42%));
}

.sidebar-menu-sub-btn--sm {
  font-size: 0.75rem;
  line-height: 1rem;
}

.sidebar-menu-sub-btn--md {
  font-size: 0.875rem;
  line-height: 1.25rem;
}

.sidebar-menu-sub-btn :deep(> span:last-child) {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sidebar-menu-sub-btn :deep(> svg) {
  width: 1rem;
  height: 1rem;
  flex-shrink: 0;
  color: hsl(var(--sidebar-accent-foreground, 245 43% 42%));
}
</style>
