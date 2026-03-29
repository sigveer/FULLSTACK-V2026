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
    <Transition name="dialog-scroll-overlay">
      <div
        v-if="isOpen"
        class="dialog-scroll-overlay"
        @click.self="close"
      >
        <Transition name="dialog-scroll-content">
          <div
            v-if="isOpen"
            role="dialog"
            aria-modal="true"
            :class="['dialog-scroll-content', props.class]"
          >
            <slot />
            <button class="dialog-scroll-close" @click="close">
              <X class="dialog-scroll-close__icon" />
              <span class="sr-only">Close</span>
            </button>
          </div>
        </Transition>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.dialog-scroll-overlay {
  position: fixed;
  inset: 0;
  z-index: 50;
  display: grid;
  place-items: center;
  overflow-y: auto;
  background-color: rgb(0 0 0 / 0.8);
}

.dialog-scroll-content {
  position: relative;
  z-index: 50;
  display: grid;
  width: 100%;
  max-width: 32rem;
  margin: 2rem 0;
  gap: 1rem;
  border: 1px solid hsl(var(--border, 35 15% 90%));
  background-color: hsl(var(--background, 43 30% 96%));
  padding: 1.5rem;
  box-shadow: 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);
  border-radius: 0.5rem;
}

.dialog-scroll-close {
  position: absolute;
  right: 1rem;
  top: 1rem;
  padding: 0.125rem;
  transition: background-color 150ms ease;
  border-radius: 0.375rem;
  background: none;
  border: none;
  cursor: pointer;
  outline: none;
}

.dialog-scroll-close:hover {
  background-color: hsl(var(--secondary, 40 20% 93%));
}

.dialog-scroll-close__icon {
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

.dialog-scroll-overlay-enter-active { transition: opacity 200ms ease; }
.dialog-scroll-overlay-leave-active { transition: opacity 150ms ease; }
.dialog-scroll-overlay-enter-from,
.dialog-scroll-overlay-leave-to { opacity: 0; }

.dialog-scroll-content-enter-active { transition: all 200ms ease; }
.dialog-scroll-content-leave-active { transition: all 150ms ease; }
.dialog-scroll-content-enter-from,
.dialog-scroll-content-leave-to {
  opacity: 0;
  transform: scale(0.95);
}
</style>
