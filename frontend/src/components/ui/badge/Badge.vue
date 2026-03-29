<script setup lang="ts">
import { computed } from "vue"

type BadgeVariant = "default" | "secondary" | "destructive" | "outline"

const props = defineProps<{
  variant?: BadgeVariant
  class?: string
}>()

const classes = computed(() => {
  const parts = ["badge"]
  parts.push(`badge--${props.variant ?? "default"}`)
  if (props.class) parts.push(props.class)
  return parts.join(" ")
})
</script>

<template>
  <div :class="classes">
    <slot />
  </div>
</template>

<style scoped>
.badge {
  display: inline-flex;
  gap: 0.25rem;
  align-items: center;
  border-radius: 0.375rem;
  border: 1px solid transparent;
  padding: 0.125rem 0.625rem;
  font-size: 0.75rem;
  line-height: 1rem;
  font-weight: 600;
  transition: colors 150ms ease;
  outline: none;
}

.badge:focus {
  outline: none;
  box-shadow: 0 0 0 2px hsl(var(--ring, 245 43% 52%)), 0 0 0 4px hsl(var(--ring, 245 43% 52%) / 0.2);
}

/* Variants — use :where() for low specificity so utility classes can override */

:where(.badge--default) {
  border-color: hsl(var(--primary, 245 43% 52%) / 0.3);
  background-color: hsl(var(--primary, 245 43% 52%));
  color: hsl(var(--primary-foreground, 0 0% 100%));
  box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05);
}
:where(.badge--default):hover {
  background-color: hsl(var(--primary, 245 43% 52%) / 0.8);
}

:where(.badge--secondary) {
  border-color: hsl(var(--secondary-foreground, 24 10% 20%) / 0.15);
  background-color: hsl(var(--secondary, 40 20% 93%));
  color: hsl(var(--secondary-foreground, 24 10% 20%));
}
:where(.badge--secondary):hover {
  background-color: hsl(var(--secondary, 40 20% 93%) / 0.8);
}

:where(.badge--destructive) {
  border-color: hsl(var(--destructive, 0 55% 42%) / 0.3);
  background-color: hsl(var(--destructive, 0 55% 42%));
  color: hsl(var(--destructive-foreground, 0 0% 100%));
  box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05);
}
:where(.badge--destructive):hover {
  background-color: hsl(var(--destructive, 0 55% 42%) / 0.8);
}

:where(.badge--outline) {
  border-color: currentColor;
  color: hsl(var(--foreground, 24 10% 10%));
  background-color: transparent;
}
</style>
