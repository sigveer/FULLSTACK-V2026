<script setup lang="ts">
import { inject } from "vue"

defineProps<{
  asChild?: boolean
}>()

const { setOpen, isOpen } = inject("dialog") as any

function toggle() {
  setOpen(!isOpen.value)
}
</script>

<template>
  <div style="display: contents" @click="toggle">
    <slot />
  </div>
</template>
