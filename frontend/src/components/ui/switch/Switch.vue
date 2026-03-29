<script setup lang="ts">
import { computed } from "vue"

const props = withDefaults(defineProps<{
  checked?: boolean
  defaultChecked?: boolean
  disabled?: boolean
  id?: string
  name?: string
  value?: string
  class?: string
}>(), {
  checked: undefined,
  defaultChecked: false,
})

const emits = defineEmits<{
  (e: "update:checked", value: boolean): void
}>()

const isChecked = computed(() => props.checked ?? props.defaultChecked)

function toggle() {
  if (props.disabled) return
  emits("update:checked", !isChecked.value)
}
</script>

<template>
  <button
    type="button"
    role="switch"
    :id="id"
    :aria-checked="isChecked"
    :data-state="isChecked ? 'checked' : 'unchecked'"
    :disabled="disabled || undefined"
    :class="['switch', props.class]"
    @click="toggle"
  >
    <span
      class="switch__thumb"
      :data-state="isChecked ? 'checked' : 'unchecked'"
    >
      <slot name="thumb" />
    </span>
  </button>
</template>

<style scoped>
.switch {
  display: inline-flex;
  height: 1.25rem;
  width: 2.25rem;
  flex-shrink: 0;
  cursor: pointer;
  align-items: center;
  border-radius: 9999px;
  border: 2px solid transparent;
  box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05);
  transition: background-color 150ms ease;
  outline: none;
  padding: 0;
  background-color: hsl(var(--input, 35 15% 85%));
}

.switch[data-state="checked"] {
  background-color: hsl(var(--primary, 245 43% 52%));
}

.switch:focus-visible {
  outline: none;
  box-shadow: 0 0 0 2px hsl(var(--background, 43 30% 96%)), 0 0 0 4px hsl(var(--ring, 245 43% 52%));
}

.switch:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.switch__thumb {
  pointer-events: none;
  display: block;
  height: 1rem;
  width: 1rem;
  border-radius: 9999px;
  background-color: hsl(var(--background, 43 30% 96%));
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
  transition: transform 150ms ease;
  transform: translateX(0);
}

.switch[data-state="checked"] .switch__thumb {
  transform: translateX(1rem);
}
</style>
