<script setup lang="ts">
import { useVModel } from "@vueuse/core"

const props = defineProps<{
  class?: string
  defaultValue?: string | number
  modelValue?: string | number
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
    :class="['textarea', props.class]"
    v-bind="$attrs"
  />
</template>

<style scoped>
.textarea {
  display: flex;
  min-height: 60px;
  width: 100%;
  border-radius: 0.5rem;
  border: 1px solid hsl(var(--input, 35 15% 85%));
  background-color: hsl(var(--card, 40 25% 98%));
  padding: 0.5rem 0.75rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
  color: inherit;
  box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05);
  transition: all 150ms ease;
  outline: none;
  resize: vertical;
  font-family: inherit;
}

.textarea::placeholder {
  color: hsl(var(--muted-foreground, 24 5% 46%));
}

.textarea:focus-visible {
  outline: none;
  box-shadow: 0 0 0 2px hsl(var(--ring, 245 43% 52%) / 0.2);
  border-color: hsl(var(--primary, 245 43% 52%) / 0.5);
}

.textarea:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}
</style>
