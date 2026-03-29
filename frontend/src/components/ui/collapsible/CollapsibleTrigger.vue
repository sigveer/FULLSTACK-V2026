<script setup lang="ts">
import { inject } from "vue"

defineProps<{
  asChild?: boolean
}>()

const { toggle, isOpen } = inject("collapsible") as any
</script>

<template>
  <div
    style="display: contents"
    :data-state="isOpen ? 'open' : 'closed'"
    @click="toggle"
  >
    <slot />
  </div>
</template>
