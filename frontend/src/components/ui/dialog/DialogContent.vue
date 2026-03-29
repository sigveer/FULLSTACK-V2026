<script setup lang="ts">
import { inject, onMounted, onBeforeUnmount } from "vue"
import { X } from "lucide-vue-next"

const props = defineProps<{
  class?: string
}>()

const { isOpen, close } = inject("dialog") as any

function onKeydown(e: KeyboardEvent) {
  if (e.key === "Escape") close()
}

onMounted(() => document.addEventListener("keydown", onKeydown))
onBeforeUnmount(() => document.removeEventListener("keydown", onKeydown))
</script>

<template>
  <Teleport to="body">
    <Transition name="dialog-overlay">
      <div v-if="isOpen" class="dialog-overlay" @click="close" />
    </Transition>
    <Transition name="dialog-content">
      <div
        v-if="isOpen"
        role="dialog"
        aria-modal="true"
        :class="['dialog-content', props.class]"
      >
        <slot />
        <button class="dialog-close" @click="close">
          <X class="dialog-close__icon" />
          <span class="sr-only">Close</span>
        </button>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.dialog-overlay {
  position: fixed;
  inset: 0;
  z-index: 50;
  background-color: rgb(0 0 0 / 0.8);
}

.dialog-content {
  position: fixed;
  left: 50%;
  top: 50%;
  z-index: 50;
  display: grid;
  width: 100%;
  max-width: 32rem;
  transform: translate(-50%, -50%);
  gap: 1rem;
  border: 1px solid hsl(var(--border, 35 15% 90%));
  background-color: hsl(var(--card, 40 25% 98%));
  padding: 1.5rem;
  box-shadow: 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);
  border-radius: 0.75rem;
}

.dialog-close {
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

.dialog-close:hover {
  opacity: 1;
}

.dialog-close:focus-visible {
  outline: none;
  box-shadow: 0 0 0 2px hsl(var(--ring, 245 43% 52%));
}

.dialog-close__icon {
  width: 1rem;
  height: 1rem;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
}

/* Overlay transitions */
.dialog-overlay-enter-active { transition: opacity 200ms ease; }
.dialog-overlay-leave-active { transition: opacity 150ms ease; }
.dialog-overlay-enter-from,
.dialog-overlay-leave-to { opacity: 0; }

/* Content transitions */
.dialog-content-enter-active { transition: all 200ms ease; }
.dialog-content-leave-active { transition: all 150ms ease; }
.dialog-content-enter-from,
.dialog-content-leave-to {
  opacity: 0;
  transform: translate(-50%, -48%) scale(0.95);
}
</style>
