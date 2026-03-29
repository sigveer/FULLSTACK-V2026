<script setup lang="ts">
import { inject } from "vue"

const props = defineProps<{
  class?: string
  inset?: boolean
  disabled?: boolean
}>()

const { close } = inject("dropdown-menu") as any

function handleClick(e: MouseEvent) {
  if (props.disabled) {
    e.preventDefault()
    return
  }
  close()
}
</script>

<template>
  <div
    :class="['dropdown-item', inset && 'dropdown-item--inset', disabled && 'dropdown-item--disabled', props.class]"
    role="menuitem"
    :tabindex="disabled ? -1 : 0"
    @click="handleClick"
  >
    <slot />
  </div>
</template>

<style scoped>
.dropdown-item {
  position: relative;
  display: flex;
  cursor: default;
  user-select: none;
  align-items: center;
  gap: 0.5rem;
  border-radius: 0.25rem;
  padding: 0.375rem 0.5rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
  outline: none;
  transition: background-color 150ms ease, color 150ms ease;
}

.dropdown-item:hover,
.dropdown-item:focus {
  background-color: hsl(var(--accent, 250 40% 95%));
  color: hsl(var(--accent-foreground, 245 43% 42%));
}

.dropdown-item--disabled {
  pointer-events: none;
  opacity: 0.5;
}

.dropdown-item--inset {
  padding-left: 2rem;
}

.dropdown-item :deep(svg) {
  width: 1rem;
  height: 1rem;
  flex-shrink: 0;
}
</style>
