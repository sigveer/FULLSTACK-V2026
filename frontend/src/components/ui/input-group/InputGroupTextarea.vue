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
  <textarea
    v-model="modelValue"
    data-slot="input-group-control"
    :class="['input-group-textarea', props.class]"
    v-bind="$attrs"
  />
</template>

<style scoped>
.input-group-textarea {
  flex: 1;
  resize: none;
  min-height: 60px;
  border: none;
  border-radius: 0;
  background: transparent;
  padding: 0.75rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
  color: inherit;
  outline: none;
  box-shadow: none;
}

.input-group-textarea::placeholder {
  color: hsl(var(--muted-foreground, 24 5% 46%));
}

.input-group-textarea:focus,
.input-group-textarea:focus-visible {
  outline: none;
  box-shadow: none;
  border: none;
}
</style>
