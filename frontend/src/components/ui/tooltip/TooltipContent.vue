<script setup lang="ts">
import { inject } from "vue"

const props = withDefaults(defineProps<{
  side?: "top" | "bottom" | "left" | "right"
  align?: "start" | "center" | "end"
  hidden?: boolean
  class?: string
}>(), {
  side: "top",
  align: "center",
})

const { isOpen } = inject("tooltip") as any
</script>

<template>
  <Transition name="tooltip">
    <div
      v-if="isOpen && !hidden"
      role="tooltip"
      :class="['tooltip-content', `tooltip-content--${side}`, `tooltip-content--align-${align}`, props.class]"
    >
      <slot />
    </div>
  </Transition>
</template>

<style scoped>
.tooltip-content {
  position: absolute;
  z-index: 50;
  overflow: hidden;
  border-radius: 0.375rem;
  background-color: hsl(var(--primary, 245 43% 52%));
  padding: 0.375rem 0.75rem;
  font-size: 0.75rem;
  line-height: 1rem;
  color: hsl(var(--primary-foreground, 0 0% 100%));
  white-space: nowrap;
  pointer-events: none;
}

/* Side positioning */
.tooltip-content--top {
  bottom: calc(100% + 4px);
}

.tooltip-content--bottom {
  top: calc(100% + 4px);
}

.tooltip-content--left {
  right: calc(100% + 4px);
  top: 50%;
  transform: translateY(-50%);
}

.tooltip-content--right {
  left: calc(100% + 4px);
  top: 50%;
  transform: translateY(-50%);
}

/* Horizontal alignment for top/bottom */
.tooltip-content--top.tooltip-content--align-center,
.tooltip-content--bottom.tooltip-content--align-center {
  left: 50%;
  transform: translateX(-50%);
}

.tooltip-content--top.tooltip-content--align-start,
.tooltip-content--bottom.tooltip-content--align-start {
  left: 0;
}

.tooltip-content--top.tooltip-content--align-end,
.tooltip-content--bottom.tooltip-content--align-end {
  right: 0;
}

/* Vertical alignment for left/right */
.tooltip-content--left.tooltip-content--align-start,
.tooltip-content--right.tooltip-content--align-start {
  top: 0;
  transform: none;
}

.tooltip-content--left.tooltip-content--align-end,
.tooltip-content--right.tooltip-content--align-end {
  top: auto;
  bottom: 0;
  transform: none;
}

/* Transitions */
.tooltip-enter-active {
  transition: opacity 150ms ease, transform 150ms ease;
}
.tooltip-leave-active {
  transition: opacity 100ms ease, transform 100ms ease;
}
.tooltip-enter-from,
.tooltip-leave-to {
  opacity: 0;
  scale: 0.95;
}
</style>
