<script setup lang="ts">
import { inject, ref, watch, nextTick } from "vue"

const props = defineProps<{
  class?: string
}>()

import type { Ref } from "vue"

const { isOpen } = inject("collapsible") as { isOpen: Ref<boolean> }
const contentRef = ref<HTMLElement | null>(null)
const height = ref("0px")

watch(isOpen, async (open: boolean) => {
  await nextTick()
  if (contentRef.value) {
    if (open) {
      height.value = contentRef.value.scrollHeight + "px"
      // After transition, set to auto for dynamic content
      setTimeout(() => {
        if (isOpen.value) height.value = "auto"
      }, 200)
    } else {
      // Set explicit height first for transition
      height.value = contentRef.value.scrollHeight + "px"
      // Force reflow
      void contentRef.value.offsetHeight
      requestAnimationFrame(() => {
        height.value = "0px"
      })
    }
  }
}, { immediate: true })
</script>

<template>
  <div
    ref="contentRef"
    :data-state="isOpen ? 'open' : 'closed'"
    :class="['collapsible-content', props.class]"
    :style="{ height }"
  >
    <slot />
  </div>
</template>

<style scoped>
.collapsible-content {
  overflow: hidden;
  transition: height 200ms ease;
}
</style>
