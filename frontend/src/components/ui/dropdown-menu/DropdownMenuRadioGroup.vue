<script setup lang="ts">
import { provide } from "vue"

const props = defineProps<{
  modelValue?: string
}>()

const emits = defineEmits<{
  (e: "update:modelValue", value: string): void
}>()

provide("dropdown-radio-group", {
  modelValue: () => props.modelValue,
  select: (value: string) => emits("update:modelValue", value),
})
</script>

<template>
  <div role="group">
    <slot />
  </div>
</template>
