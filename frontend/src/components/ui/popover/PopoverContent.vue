<script setup lang="ts">
import { inject, ref, onBeforeUnmount, watch, nextTick } from "vue"

const props = withDefaults(defineProps<{
  class?: string
  align?: "start" | "center" | "end"
}>(), {
  align: "center",
})

import type { Ref } from "vue"

const { isOpen, close } = inject("popover") as { isOpen: Ref<boolean>; close: () => void }
const contentRef = ref<HTMLElement | null>(null)

function onClickOutside(e: MouseEvent) {
  if (!contentRef.value) return
  const root = contentRef.value.closest(".popover-root")
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
  <Transition name="popover">
    <div
      v-if="isOpen"
      ref="contentRef"
      :class="['popover-content', `popover-content--${align}`, props.class]"
    >
      <slot />
    </div>
  </Transition>
</template>

<style scoped>
.popover-content {
  position: absolute;
  z-index: 50;
  top: calc(100% + 4px);
  width: 18rem;
  border-radius: 0.5rem;
  border: 1px solid hsl(var(--border, 35 15% 90%));
  background-color: hsl(var(--card, 40 25% 98%));
  color: hsl(var(--card-foreground, 24 10% 10%));
  padding: 1rem;
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
  outline: none;
}

.popover-content--center {
  left: 50%;
  transform: translateX(-50%);
}

.popover-content--start {
  left: 0;
}

.popover-content--end {
  right: 0;
}

.popover-enter-active {
  animation: popover-in 150ms ease-out;
}

.popover-leave-active {
  animation: popover-out 100ms ease-in;
}

@keyframes popover-in {
  from {
    opacity: 0;
    transform: scale(0.95) translateY(-4px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@keyframes popover-out {
  from {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
  to {
    opacity: 0;
    transform: scale(0.95) translateY(-4px);
  }
}

/* Override transform for centered alignment during animation */
.popover-content--center.popover-enter-active,
.popover-content--center.popover-leave-active {
  left: 50%;
}

.popover-content--center.popover-enter-active {
  animation: popover-in-center 150ms ease-out;
}

.popover-content--center.popover-leave-active {
  animation: popover-out-center 100ms ease-in;
}

@keyframes popover-in-center {
  from {
    opacity: 0;
    transform: translateX(-50%) scale(0.95) translateY(-4px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) scale(1) translateY(0);
  }
}

@keyframes popover-out-center {
  from {
    opacity: 1;
    transform: translateX(-50%) scale(1) translateY(0);
  }
  to {
    opacity: 0;
    transform: translateX(-50%) scale(0.95) translateY(-4px);
  }
}
</style>
