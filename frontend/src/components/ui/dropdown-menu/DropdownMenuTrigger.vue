<script setup lang="ts">
import { inject, ref, onMounted } from "vue"

defineProps<{
  asChild?: boolean
}>()

import type { Ref } from "vue"

const { toggle, triggerRef } = inject("dropdown-menu") as { toggle: () => void; triggerRef: Ref<HTMLElement | null> }
const elRef = ref<HTMLElement | null>(null)

onMounted(() => {
  if (elRef.value) {
    // display:contents has no box — use first child element for positioning
    const child = elRef.value.firstElementChild as HTMLElement | null
    triggerRef.value = child ?? elRef.value
  }
})
</script>

<template>
  <div ref="elRef" style="display: contents" @click="toggle">
    <slot />
  </div>
</template>
