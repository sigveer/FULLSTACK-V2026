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
    role="checkbox"
    :id="id"
    :aria-checked="isChecked"
    :data-state="isChecked ? 'checked' : 'unchecked'"
    :disabled="disabled || undefined"
    :class="['checkbox', props.class]"
    @click="toggle"
  >
    <span v-if="isChecked" class="checkbox__indicator">
      <slot>
        <svg class="checkbox__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="20 6 9 17 4 12" />
        </svg>
      </slot>
    </span>
  </button>
</template>

<style scoped>
.checkbox {
  display: grid;
  place-content: center;
  height: 18px;
  width: 18px;
  flex-shrink: 0;
  border-radius: 0.25rem;
  border: 1px solid hsl(var(--input, 35 15% 85%));
  background-color: transparent;
  box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05);
  cursor: pointer;
  padding: 0;
  outline: none;
  transition: all 150ms ease;
  color: hsl(var(--primary-foreground, 0 0% 100%));
}

.checkbox:focus-visible {
  outline: none;
  box-shadow: 0 0 0 1px hsl(var(--ring, 245 43% 52%));
}

.checkbox:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.checkbox[data-state="checked"] {
  background-color: hsl(var(--primary, 245 43% 52%));
  border-color: hsl(var(--primary, 245 43% 52%));
  color: hsl(var(--primary-foreground, 0 0% 100%));
}

.checkbox__indicator {
  display: grid;
  place-content: center;
  color: currentColor;
}

.checkbox__icon {
  height: 1rem;
  width: 1rem;
}
</style>
