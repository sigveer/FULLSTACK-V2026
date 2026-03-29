<script setup lang="ts">
import { inject, computed } from "vue"
import { Circle } from "lucide-vue-next"

const props = defineProps<{
  class?: string
  value: string
  disabled?: boolean
}>()

const group = inject("dropdown-radio-group") as any
const isChecked = computed(() => group.modelValue() === props.value)

function handleClick() {
  if (props.disabled) return
  group.select(props.value)
}
</script>

<template>
  <div
    :class="['dropdown-radio-item', disabled && 'dropdown-radio-item--disabled', props.class]"
    role="menuitemradio"
    :aria-checked="isChecked"
    :tabindex="disabled ? -1 : 0"
    @click="handleClick"
  >
    <span class="dropdown-radio-indicator">
      <Circle v-if="isChecked" class="dropdown-radio-icon" />
    </span>
    <slot />
  </div>
</template>

<style scoped>
.dropdown-radio-item {
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

.dropdown-radio-item:hover,
.dropdown-radio-item:focus {
  background-color: hsl(var(--accent, 250 40% 95%));
  color: hsl(var(--accent-foreground, 245 43% 42%));
}

.dropdown-radio-item--disabled {
  pointer-events: none;
  opacity: 0.5;
}

.dropdown-radio-indicator {
  position: absolute;
  left: 0.5rem;
  display: flex;
  width: 0.875rem;
  height: 0.875rem;
  align-items: center;
  justify-content: center;
}

.dropdown-radio-icon {
  width: 0.5rem;
  height: 0.5rem;
  fill: currentColor;
}
</style>
