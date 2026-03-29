<script setup lang="ts">
import { inject, ref, onMounted } from "vue"

defineProps<{
  asChild?: boolean
}>()

const { toggle, triggerRef } = inject("dropdown-menu") as any
const elRef = ref<HTMLElement | null>(null)

onMounted(() => {
  if (elRef.value) {
    triggerRef.value = elRef.value
  }
})
</script>

<template>
  <div ref="elRef" style="display: contents" @click="toggle">
    <slot />
  </div>
</template>
