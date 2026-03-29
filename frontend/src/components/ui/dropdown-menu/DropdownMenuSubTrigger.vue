<script setup lang="ts">
import { inject } from "vue"
import { ChevronRight } from "lucide-vue-next"

defineProps<{
  class?: string
}>()

import type { Ref } from "vue"

const { openSub, closeSub, isSubOpen } = inject("dropdown-sub") as { openSub: () => void; closeSub: () => void; isSubOpen: Ref<boolean> }

function handleMouseEnter() {
  openSub()
}

function handleMouseLeave() {
  closeSub()
}
</script>

<template>
  <div
    :class="['dropdown-sub-trigger', isSubOpen && 'dropdown-sub-trigger--open', $props.class]"
    @mouseenter="handleMouseEnter"
    @mouseleave="handleMouseLeave"
  >
    <slot />
    <ChevronRight class="dropdown-sub-chevron" />
  </div>
</template>

<style scoped>
.dropdown-sub-trigger {
  display: flex;
  cursor: default;
  user-select: none;
  align-items: center;
  border-radius: 0.25rem;
  padding: 0.375rem 0.5rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
  outline: none;
  transition: background-color 150ms ease;
}

.dropdown-sub-trigger:hover,
.dropdown-sub-trigger:focus,
.dropdown-sub-trigger--open {
  background-color: hsl(var(--accent, 250 40% 95%));
}

.dropdown-sub-chevron {
  margin-left: auto;
  width: 1rem;
  height: 1rem;
}
</style>
