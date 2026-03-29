<script setup lang="ts">
import { inject, onMounted, onBeforeUnmount } from "vue"
import { X } from "lucide-vue-next"

type SheetSide = "top" | "bottom" | "left" | "right"

const props = withDefaults(defineProps<{
  side?: SheetSide
  class?: string
}>(), {
  side: "right",
})

import type { Ref } from "vue"

const { isOpen, close } = inject("sheet") as { isOpen: Ref<boolean>; close: () => void }

function onKeydown(e: KeyboardEvent) {
  if (e.key === "Escape") close()
}

onMounted(() => document.addEventListener("keydown", onKeydown))
onBeforeUnmount(() => document.removeEventListener("keydown", onKeydown))
</script>

<template>
  <Teleport to="body">
    <Transition name="sheet-overlay">
      <div v-if="isOpen" class="sheet-overlay" @click="close" />
    </Transition>
    <Transition :name="`sheet-slide-${side}`">
      <div
        v-if="isOpen"
        role="dialog"
        aria-modal="true"
        :class="['sheet-content', `sheet-content--${side}`, props.class]"
      >
        <slot />
        <button class="sheet-close" @click="close">
          <X class="sheet-close__icon" />
        </button>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.sheet-overlay {
  position: fixed;
  inset: 0;
  z-index: 50;
  background-color: rgb(0 0 0 / 0.8);
}

.sheet-content {
  position: fixed;
  z-index: 50;
  gap: 1rem;
  background-color: hsl(var(--background, 43 30% 96%));
  padding: 1.5rem;
  box-shadow: 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);
}

.sheet-content--right {
  inset: 0 0 0 auto;
  height: 100%;
  width: 75%;
  max-width: 24rem;
  border-left: 1px solid hsl(var(--border, 35 15% 90%));
}

.sheet-content--left {
  inset: 0 auto 0 0;
  height: 100%;
  width: 75%;
  max-width: 24rem;
  border-right: 1px solid hsl(var(--border, 35 15% 90%));
}

.sheet-content--top {
  inset: 0 0 auto 0;
  border-bottom: 1px solid hsl(var(--border, 35 15% 90%));
}

.sheet-content--bottom {
  inset: auto 0 0 0;
  border-top: 1px solid hsl(var(--border, 35 15% 90%));
}

.sheet-close {
  position: absolute;
  right: 1rem;
  top: 1rem;
  border-radius: 0.125rem;
  opacity: 0.7;
  transition: opacity 150ms ease;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  outline: none;
}

.sheet-close:hover {
  opacity: 1;
}

.sheet-close:focus-visible {
  outline: none;
  box-shadow: 0 0 0 2px hsl(var(--ring, 245 43% 52%));
}

.sheet-close__icon {
  width: 1rem;
  height: 1rem;
}

/* Overlay transitions */
.sheet-overlay-enter-active { transition: opacity 300ms ease; }
.sheet-overlay-leave-active { transition: opacity 200ms ease; }
.sheet-overlay-enter-from,
.sheet-overlay-leave-to { opacity: 0; }

/* Slide from right */
.sheet-slide-right-enter-active { transition: transform 500ms cubic-bezier(0.32, 0.72, 0, 1); }
.sheet-slide-right-leave-active { transition: transform 300ms cubic-bezier(0.32, 0.72, 0, 1); }
.sheet-slide-right-enter-from,
.sheet-slide-right-leave-to { transform: translateX(100%); }

/* Slide from left */
.sheet-slide-left-enter-active { transition: transform 500ms cubic-bezier(0.32, 0.72, 0, 1); }
.sheet-slide-left-leave-active { transition: transform 300ms cubic-bezier(0.32, 0.72, 0, 1); }
.sheet-slide-left-enter-from,
.sheet-slide-left-leave-to { transform: translateX(-100%); }

/* Slide from top */
.sheet-slide-top-enter-active { transition: transform 500ms cubic-bezier(0.32, 0.72, 0, 1); }
.sheet-slide-top-leave-active { transition: transform 300ms cubic-bezier(0.32, 0.72, 0, 1); }
.sheet-slide-top-enter-from,
.sheet-slide-top-leave-to { transform: translateY(-100%); }

/* Slide from bottom */
.sheet-slide-bottom-enter-active { transition: transform 500ms cubic-bezier(0.32, 0.72, 0, 1); }
.sheet-slide-bottom-leave-active { transition: transform 300ms cubic-bezier(0.32, 0.72, 0, 1); }
.sheet-slide-bottom-enter-from,
.sheet-slide-bottom-leave-to { transform: translateY(100%); }
</style>
