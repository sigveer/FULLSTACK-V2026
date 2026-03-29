<script setup lang="ts">
import { inject } from "vue"

defineProps<{
  asChild?: boolean
}>()

import type { Ref } from "vue"

const { isOpen, setOpen } = inject("sheet") as { isOpen: Ref<boolean>; setOpen: (v: boolean) => void }

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
