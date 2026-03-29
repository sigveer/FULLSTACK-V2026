<script setup lang="ts">
import { inject } from "vue"

defineProps<{
  asChild?: boolean
}>()

import type { Ref } from "vue"

const { toggle, isOpen } = inject("collapsible") as { toggle: () => void; isOpen: Ref<boolean> }
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
