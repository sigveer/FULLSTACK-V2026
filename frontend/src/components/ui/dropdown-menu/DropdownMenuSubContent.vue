<script setup lang="ts">
import { inject, ref, onBeforeUnmount, watch, nextTick } from "vue"

defineProps<{
  class?: string
}>()

const { isSubOpen, closeSub } = inject("dropdown-sub") as any
const contentRef = ref<HTMLElement | null>(null)

function onClickOutside(e: MouseEvent) {
  if (!contentRef.value) return
  const sub = contentRef.value.closest(".dropdown-sub")
  if (sub && sub.contains(e.target as Node)) return
  if (!contentRef.value.contains(e.target as Node)) {
    closeSub()
  }
}

watch(isSubOpen, async (open: boolean) => {
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
  <Transition name="dropdown-sub">
    <div
      v-if="isSubOpen"
      ref="contentRef"
      :class="['dropdown-sub-content', $props.class]"
      role="menu"
      @mouseenter="isSubOpen = true"
      @mouseleave="closeSub()"
    >
      <slot />
    </div>
  </Transition>
</template>

<style scoped>
.dropdown-sub-content {
  position: absolute;
  left: 100%;
  top: 0;
  z-index: 50;
  min-width: 8rem;
  overflow: hidden;
  border-radius: 0.5rem;
  border: 1px solid hsl(var(--border, 35 15% 90%));
  background-color: hsl(var(--card, 40 25% 98%));
  color: hsl(var(--card-foreground, 24 10% 10%));
  padding: 0.25rem;
  margin-left: 0.25rem;
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
}

.dropdown-sub-enter-active {
  animation: dropdown-sub-in 150ms ease-out;
}

.dropdown-sub-leave-active {
  animation: dropdown-sub-out 100ms ease-in;
}

@keyframes dropdown-sub-in {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes dropdown-sub-out {
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
