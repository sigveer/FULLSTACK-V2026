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

import type { Ref } from "vue"

const { isOpen, triggerRef, close } = inject("dropdown-menu") as {
  isOpen: Ref<boolean>
  triggerRef: Ref<HTMLElement | null>
  close: () => void
}
const contentRef = ref<HTMLElement | null>(null)
const posStyle = ref<Record<string, string>>({})

function updatePosition() {
  const trigger = triggerRef.value
  if (!trigger) return
  const rect = trigger.getBoundingClientRect()
  const s: Record<string, string> = { position: "fixed", zIndex: "999" }
  const isVertical = props.side === "bottom" || props.side === "top"

  // Side axis: which edge of the trigger does the dropdown attach to
  if (props.side === "bottom") {
    s.top = `${rect.bottom + props.sideOffset}px`
  } else if (props.side === "top") {
    s.bottom = `${window.innerHeight - rect.top + props.sideOffset}px`
  } else if (props.side === "right") {
    s.left = `${rect.right + props.sideOffset}px`
  } else if (props.side === "left") {
    s.right = `${window.innerWidth - rect.left + props.sideOffset}px`
  }

  // Align axis: perpendicular to side
  if (isVertical) {
    // Horizontal alignment
    if (props.align === "start") {
      s.left = `${rect.left}px`
    } else if (props.align === "center") {
      s.left = `${rect.left + rect.width / 2}px`
      s.transform = "translateX(-50%)"
    } else if (props.align === "end") {
      s.right = `${window.innerWidth - rect.right}px`
    }
  } else {
    // Vertical alignment for side left/right
    if (props.align === "start") {
      s.top = `${rect.top}px`
    } else if (props.align === "center") {
      s.top = `${rect.top + rect.height / 2}px`
      s.transform = "translateY(-50%)"
    } else if (props.align === "end") {
      s.bottom = `${window.innerHeight - rect.bottom}px`
    }
  }

  posStyle.value = s
}

function onClickOutside(e: MouseEvent) {
  if (!contentRef.value) return
  const trigger = triggerRef.value
  if (trigger && trigger.contains(e.target as Node)) return
  if (!contentRef.value.contains(e.target as Node)) {
    close()
  }
}

function onKeydown(e: KeyboardEvent) {
  if (e.key === "Escape") close()
}

watch(isOpen, async (open: boolean) => {
  if (open) {
    updatePosition()
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
  <Teleport to="body">
    <Transition name="dropdown">
      <div
        v-if="isOpen"
        ref="contentRef"
        :class="['dropdown-content', props.class]"
        :style="posStyle"
        role="menu"
      >
        <slot />
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.dropdown-content {
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
