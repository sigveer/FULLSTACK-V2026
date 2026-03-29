<script setup lang="ts">
import { inject, ref, onBeforeUnmount, watch, nextTick } from "vue"

const props = withDefaults(defineProps<{
  class?: string
  align?: "start" | "center" | "end"
  side?: "bottom" | "top" | "left" | "right"
  sideOffset?: number
}>(), {
  align: "start",
  side: "bottom",
  sideOffset: 4,
})

const { isOpen, close } = inject("dropdown-menu") as any
const contentRef = ref<HTMLElement | null>(null)

function onClickOutside(e: MouseEvent) {
  if (!contentRef.value) return
  const root = contentRef.value.closest(".dropdown-menu-root")
  if (root && root.contains(e.target as Node)) return
  if (!contentRef.value.contains(e.target as Node)) {
    close()
  }
}

function onKeydown(e: KeyboardEvent) {
  if (e.key === "Escape") close()
}

watch(isOpen, async (open: boolean) => {
  if (open) {
    await nextTick()
    document.addEventListener("mousedown", onClickOutside)
    document.addEventListener("keydown", onKeydown)
  } else {
    document.removeEventListener("mousedown", onClickOutside)
    document.removeEventListener("keydown", onKeydown)
  }
})

onBeforeUnmount(() => {
  document.removeEventListener("mousedown", onClickOutside)
  document.removeEventListener("keydown", onKeydown)
})
</script>

<template>
  <Transition name="dropdown">
    <div
      v-if="isOpen"
      ref="contentRef"
      :class="['dropdown-content', `dropdown-content--${align}`, `dropdown-content--${side}`, props.class]"
      :style="{ '--side-offset': `${sideOffset}px` }"
      role="menu"
    >
      <slot />
    </div>
  </Transition>
</template>

<style scoped>
.dropdown-content {
  position: absolute;
  z-index: 50;
  min-width: 14rem;
  overflow: hidden;
  border-radius: 0.5rem;
  border: 1px solid hsl(var(--border, 35 15% 90%));
  background-color: hsl(var(--card, 40 25% 98%));
  color: hsl(var(--card-foreground, 24 10% 10%));
  padding: 0.25rem;
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
  outline: none;
}

/* Side positioning */
.dropdown-content--bottom {
  top: calc(100% + var(--side-offset));
}

.dropdown-content--top {
  bottom: calc(100% + var(--side-offset));
}

.dropdown-content--right {
  left: calc(100% + var(--side-offset));
  top: 0;
}

.dropdown-content--left {
  right: calc(100% + var(--side-offset));
  top: 0;
}

/* Align */
.dropdown-content--start {
  left: 0;
}

.dropdown-content--center {
  left: 50%;
  transform: translateX(-50%);
}

.dropdown-content--end {
  right: 0;
}

/* For side left/right, align applies to vertical axis */
.dropdown-content--right.dropdown-content--start,
.dropdown-content--left.dropdown-content--start {
  top: 0;
  left: unset;
  right: unset;
}

.dropdown-content--right.dropdown-content--start {
  left: calc(100% + var(--side-offset));
}

.dropdown-content--left.dropdown-content--start {
  right: calc(100% + var(--side-offset));
}

.dropdown-content--right.dropdown-content--end,
.dropdown-content--left.dropdown-content--end {
  top: unset;
  bottom: 0;
}

.dropdown-content--right.dropdown-content--end {
  left: calc(100% + var(--side-offset));
  right: unset;
}

.dropdown-content--left.dropdown-content--end {
  right: calc(100% + var(--side-offset));
  left: unset;
}

/* Animation */
.dropdown-enter-active {
  animation: dropdown-in 150ms ease-out;
}

.dropdown-leave-active {
  animation: dropdown-out 100ms ease-in;
}

@keyframes dropdown-in {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes dropdown-out {
  from {
    opacity: 1;
    transform: scale(1);
  }
  to {
    opacity: 0;
    transform: scale(0.95);
  }
}
</style>
