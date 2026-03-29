<script setup lang="ts">
import { Check } from "lucide-vue-next"

const props = defineProps<{
  class?: string
  checked?: boolean
  disabled?: boolean
}>()

const emits = defineEmits<{
  (e: "update:checked", value: boolean): void
}>()

function handleClick() {
  if (props.disabled) return
  emits("update:checked", !props.checked)
}
</script>

<template>
  <div
    :class="['dropdown-checkbox-item', disabled && 'dropdown-checkbox-item--disabled', props.class]"
    role="menuitemcheckbox"
    :aria-checked="checked"
    :tabindex="disabled ? -1 : 0"
    @click="handleClick"
  >
    <span class="dropdown-checkbox-indicator">
      <Check v-if="checked" class="dropdown-checkbox-icon" />
    </span>
    <slot />
  </div>
</template>

<style scoped>
.dropdown-checkbox-item {
  position: relative;
  display: flex;
  cursor: default;
  user-select: none;
  align-items: center;
  border-radius: 0.25rem;
  padding: 0.375rem 0.5rem 0.375rem 2rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
  outline: none;
  transition: background-color 150ms ease, color 150ms ease;
}

.dropdown-checkbox-item:hover,
.dropdown-checkbox-item:focus {
  background-color: hsl(var(--accent, 250 40% 95%));
  color: hsl(var(--accent-foreground, 245 43% 42%));
}

.dropdown-checkbox-item--disabled {
  pointer-events: none;
  opacity: 0.5;
}

.dropdown-checkbox-indicator {
  position: absolute;
  left: 0.5rem;
  display: flex;
  width: 0.875rem;
  height: 0.875rem;
  align-items: center;
  justify-content: center;
}

.dropdown-checkbox-icon {
  width: 1rem;
  height: 1rem;
}
</style>
