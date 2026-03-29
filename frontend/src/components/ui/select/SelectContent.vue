<script setup lang="ts">
import { inject, ref, onMounted, onBeforeUnmount, nextTick, watch } from "vue"
import { Teleport } from "vue"

const props = defineProps<{
  class?: string
}>()

const { isOpen, close } = inject("select--------------------") as any

const contentRef = ref<HTMLElement | null>(null)

function onClickOutside(e: MouseEvent) {
  if (!contentRef.value) return
  const target = e.target as Node
  // Check if click is inside the content or the trigger
  const root = contentRef.value.closest(".select---------------------root") ?? contentRef.value.parentElement
  if (root && root.contains(target)) return
  if (!contentRef.value.contains(target)) {
    close()
  }
}

watch(isOpen, async (open: boolean) => {
  if (open) {
    await nextTick()
    document.addEventListener("mousedown", onClickOutside)
  } else {
    document.removeEventListener("mousedown", onClickOutside)
  }
})

onBeforeUnmount(() => {
  document.removeEventListener("mousedown", onClickOutside)
})
</script>

<template>
  <Transition name="select-content">
    <div
      v-if="isOpen"
      ref="contentRef"
      :class="['select-content', props.class]"
    >
      <div class="select-viewport">
        <slot />
      </div>
    </div>
  </Transition>
</template>

<style scoped>
.select-content {
  position: absolute;
  z-index: 50;
  top: calc(100% + 4px);
  left: 0;
  max-height: 24rem;
  min-width: 8rem;
  width: 100%;
  overflow: hidden;
  border-radius: 0.5rem;
  border: 1px solid hsl(var(--border, 35 15% 90%));
  background-color: hsl(var(--card, 40 25% 98%));
  color: hsl(var(--card-foreground, 24 10% 10%));
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
}

.select-viewport {
  padding: 0.25rem;
  overflow-y: auto;
  max-height: 24rem;
}

.select-content-enter-active {
  animation: select-in 150ms ease-out;
}

.select-content-leave-active {
  animation: select-out 100ms ease-in;
}

@keyframes select-in {
  from {
    opacity: 0;
    transform: scale(0.95) translateY(-4px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@keyframes select-out {
  from {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
  to {
    opacity: 0;
    transform: scale(0.95) translateY(-4px);
  }
}
</style>
