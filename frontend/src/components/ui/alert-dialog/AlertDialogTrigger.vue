<script setup lang="ts">
import { inject } from "vue"

defineProps<{
  asChild?: boolean
}>()

import type { Ref } from "vue"

const { setOpen, isOpen } = inject("alert-dialog") as { setOpen: (v: boolean) => void; isOpen: Ref<boolean> }

function toggle() {
  setOpen(!isOpen.value)
}
</script>

<template>
  <div style="display: contents" @click="toggle">
    <slot />
  </div>
</template>
