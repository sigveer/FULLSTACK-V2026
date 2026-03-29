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

watch(() => props.open, (v) => {
  if (v !== undefined) isOpen.value = v
})

function setOpen(value: boolean) {
  isOpen.value = value
  emits("update:open", value)
}

function close() {
  setOpen(false)
}

provide("dialog", { isOpen, setOpen, close })
</script>

<template>
  <slot />
</template>
