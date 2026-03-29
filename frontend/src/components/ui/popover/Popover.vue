<script setup lang="ts">
import { provide, ref, watch } from "vue"

const props = withDefaults(defineProps<{
  open?: boolean
  defaultOpen?: boolean
}>(), {
  open: undefined,
  defaultOpen: false,
})

const emits = defineEmits<{
  (e: "update:open", value: boolean): void
}>()

const isOpen = ref(props.open ?? props.defaultOpen)
const triggerRef = ref<HTMLElement | null>(null)

watch(() => props.open, (v) => {
  if (v !== undefined) isOpen.value = v
})

function toggle() {
  const next = !isOpen.value
  isOpen.value = next
  emits("update:open", next)
}

function close() {
  isOpen.value = false
  emits("update:open", false)
}

provide("popover", { isOpen, triggerRef, toggle, close })
</script>

<template>
  <div class="popover-root">
    <slot />
  </div>
</template>

<style scoped>
.popover-root {
  position: relative;
  display: inline-block;
}
</style>
