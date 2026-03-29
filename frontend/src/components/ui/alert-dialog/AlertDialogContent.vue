<script setup lang="ts">
import { inject } from "vue"

const props = defineProps<{
  class?: string
}>()

import type { Ref } from "vue"

const { isOpen } = inject("alert-dialog") as { isOpen: Ref<boolean> }
</script>

<template>
  <Teleport to="body">
    <Transition name="alert-overlay">
      <div v-if="isOpen" class="alert-overlay" />
    </Transition>
    <Transition name="alert-content">
      <div
        v-if="isOpen"
        role="alertdialog"
        aria-modal="true"
        :class="['alert-content', props.class]"
      >
        <slot />
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.alert-overlay {
  position: fixed;
  inset: 0;
  z-index: 50;
  background-color: rgb(0 0 0 / 0.8);
}

.alert-content {
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

/* Overlay transitions */
.alert-overlay-enter-active { transition: opacity 200ms ease; }
.alert-overlay-leave-active { transition: opacity 150ms ease; }
.alert-overlay-enter-from,
.alert-overlay-leave-to { opacity: 0; }

/* Content transitions */
.alert-content-enter-active { transition: all 200ms ease; }
.alert-content-leave-active { transition: all 150ms ease; }
.alert-content-enter-from,
.alert-content-leave-to {
  opacity: 0;
  transform: translate(-50%, -48%) scale(0.95);
}
</style>
