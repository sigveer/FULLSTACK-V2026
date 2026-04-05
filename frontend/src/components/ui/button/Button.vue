<script setup lang="ts">
type ButtonVariant = "default" | "destructive" | "outline" | "secondary" | "ghost" | "link" | "destructive-ghost"
type ButtonSize = "default" | "xs" | "sm" | "lg" | "icon" | "icon-sm" | "icon-lg"

interface Props {
  as?: string
  variant?: ButtonVariant
  size?: ButtonSize
  disabled?: boolean
  class?: string
}

const props = withDefaults(defineProps<Props>(), {
  as: "button",
  variant: "default",
  size: "default",
})

const classes = computed(() => {
  const parts = ["btn"]
  if (props.variant) parts.push(`btn--${props.variant}`)
  if (props.size && props.size !== "default") parts.push(`btn--${props.size}`)
  if (props.class) parts.push(props.class)
  return parts.join(" ")
})

import { computed } from "vue"
</script>

<template>
  <component
    :is="as"
    :class="classes"
    :disabled="disabled || undefined"
  >
    <slot />
  </component>
</template>

<style scoped>
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  white-space: nowrap;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
  font-weight: 500;
  transition: all 150ms ease;
  outline: none;
  border: none;
  cursor: pointer;
  text-decoration: none;
  height: 2.25rem;
  padding: 0.5rem 1rem;
}

.btn:focus-visible {
  outline: none;
  box-shadow: 0 0 0 1px hsl(var(--ring, 245 43% 52%));
}

.btn:disabled {
  pointer-events: none;
  opacity: 0.5;
}

.btn:active {
  transform: scale(0.97);
}

.btn :deep(svg) {
  pointer-events: none;
  width: 1rem;
  height: 1rem;
  flex-shrink: 0;
}

/* Variants */

.btn--default {
  background-color: hsl(var(--primary, 245 43% 52%));
  color: hsl(var(--primary-foreground, 0 0% 100%));
  box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05);
}
.btn--default:hover {
  background-color: hsl(var(--primary, 245 43% 52%) / 0.85);
}
.btn--default:active {
  background-color: hsl(var(--primary, 245 43% 52%) / 0.75);
}

.btn--destructive {
  background-color: hsl(var(--destructive, 0 55% 42%));
  color: hsl(var(--destructive-foreground, 0 0% 100%));
  box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05);
}
.btn--destructive:hover {
  background-color: hsl(0, 55%, 48%);
}
.btn--destructive:active {
  background-color: hsl(0, 55%, 36%);
}

.btn--destructive-ghost {
  background-color: hsl(0 80% 95%);
  color: hsl(var(--destructive, 0 55% 42%));
  box-shadow: none;
}
.btn--destructive-ghost:hover {
  background-color: hsl(0 80% 90%);
}
.btn--destructive-ghost:active {
  background-color: hsl(0 80% 85%);
}

.btn--outline {
  border: 1px solid hsl(var(--input, 35 15% 85%));
  background-color: hsl(var(--background, 43 30% 96%));
  color: inherit;
  box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05);
}
.btn--outline:hover {
  background-color: hsl(var(--accent, 250 40% 95%));
  color: hsl(var(--accent-foreground, 245 43% 42%));
}
.btn--outline:active {
  background-color: hsl(var(--accent, 250 40% 95%) / 0.8);
}

.btn--secondary {
  background-color: hsl(var(--secondary, 40 20% 93%));
  color: hsl(var(--secondary-foreground, 24 10% 20%));
  box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05);
}
.btn--secondary:hover {
  background-color: hsl(var(--secondary, 40 20% 93%) / 0.7);
}
.btn--secondary:active {
  background-color: hsl(var(--secondary, 40 20% 93%) / 0.6);
}

.btn--ghost {
  background-color: transparent;
  color: inherit;
}
.btn--ghost:hover {
  background-color: hsl(var(--accent, 250 40% 95%));
  color: hsl(var(--accent-foreground, 245 43% 42%));
}
.btn--ghost:active {
  background-color: hsl(var(--accent, 250 40% 95%) / 0.8);
}

.btn--link {
  background-color: transparent;
  color: hsl(var(--primary, 245 43% 52%));
  text-underline-offset: 4px;
  box-shadow: none;
}
.btn--link:hover {
  text-decoration: underline;
  background-color: transparent;
}
.btn--link:active {
  transform: scale(1);
}

/* Sizes */

.btn--xs {
  height: 1.75rem;
  padding: 0 0.5rem;
  border-radius: 0.25rem;
}

.btn--sm {
  height: 2rem;
  padding: 0 0.75rem;
  font-size: 0.75rem;
  line-height: 1rem;
  border-radius: 0.375rem;
}

.btn--lg {
  height: 2.5rem;
  padding: 0 2rem;
  border-radius: 0.375rem;
}

.btn--icon {
  height: 2.25rem;
  width: 2.25rem;
  padding: 0;
}

.btn--icon-sm {
  height: 2rem;
  width: 2rem;
  padding: 0;
}

.btn--icon-lg {
  height: 2.5rem;
  width: 2.5rem;
  padding: 0;
}
</style>
