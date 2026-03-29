<script setup lang="ts">
import { useVModel } from "@vueuse/core"

const props = defineProps<{
  defaultValue?: string | number
  modelValue?: string | number
  class?: string
}>()

const emits = defineEmits<{
  (e: "update:modelValue", payload: string | number): void
}>()

const modelValue = useVModel(props, "modelValue", emits, {
  passive: true,
  defaultValue: props.defaultValue,
})
</script>

<template>
  <input
    v-model="modelValue"
    data-slot="input-group-control"
    :class="['input-group-input', props.class]"
    v-bind="$attrs"
  >
</template>

<style scoped>
.input-group-input {
  flex: 1;
  height: auto;
  min-width: 0;
  border: none;
  border-radius: 0;
  background: transparent;
  padding: 0.5rem 0.75rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
  color: inherit;
  outline: none;
  box-shadow: none;
  transition: none;
}

.input-group-input::placeholder {
  color: hsl(var(--muted-foreground, 24 5% 46%));
}

.input-group-input:focus {
  outline: none;
  box-shadow: none;
  border: none;
}

.input-group-input:focus-visible {
  outline: none;
  box-shadow: none;
  border: none;
}
</style>
