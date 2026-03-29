<script setup lang="ts">
import { provide, ref, watch } from "vue"

const props = withDefaults(defineProps<{
  open?: boolean
  defaultOpen?: boolean
  asChild?: boolean
  class?: string
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

function toggle() {
  const next = !isOpen.value
  isOpen.value = next
  emits("update:open", next)
}

provide("collapsible", { isOpen, toggle })
</script>

<template>
  <div :class="props.class" v-bind="$attrs">
    <slot :open="isOpen" />
  </div>
</template>
