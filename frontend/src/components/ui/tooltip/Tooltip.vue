<script setup lang="ts">
import { provide, ref, inject } from "vue"

const providerCtx = inject("tooltip-provider", { delayDuration: 700 }) as { delayDuration: number }

const isOpen = ref(false)
let openTimeout: ReturnType<typeof setTimeout> | null = null

function show() {
  openTimeout = setTimeout(() => {
    isOpen.value = true
  }, providerCtx.delayDuration)
}

function hide() {
  if (openTimeout) {
    clearTimeout(openTimeout)
    openTimeout = null
  }
  isOpen.value = false
}

provide("tooltip", { isOpen, show, hide })
</script>

<template>
  <div class="tooltip-root">
    <slot />
  </div>
</template>

<style scoped>
.tooltip-root {
  position: relative;
  display: inline-block;
}
</style>
