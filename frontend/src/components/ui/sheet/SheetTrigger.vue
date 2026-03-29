<script setup lang="ts">
import { inject } from "vue"

defineProps<{
  asChild?: boolean
}>()

const { isOpen, setOpen } = inject("sheet") as any

function toggle() {
  setOpen(!isOpen.value)
}
</script>

<template>
  <slot v-if="asChild" :onClick="toggle" />
  <button v-else type="button" @click="toggle">
    <slot />
  </button>
</template>
