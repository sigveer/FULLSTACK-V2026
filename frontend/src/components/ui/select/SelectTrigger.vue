<script setup lang="ts">
import { inject } from "vue"
import { ChevronDown } from "lucide-vue-next"

const props = defineProps<{
  disabled?: boolean
  class?: string
}>()

const { isOpen, toggle } = inject("select--------------------") as any
</script>

<template>
  <button
    type="button"
    role="combobox"
    :aria-expanded="isOpen"
    :disabled="disabled || undefined"
    :class="['select-trigger', props.class]"
    @click="toggle"
  >
    <span class="select-trigger__content">
      <slot />
    </span>
    <ChevronDown class="select-trigger__icon" />
  </button>
</template>

<style scoped>
.select-trigger {
  display: flex;
  height: 2.5rem;
  width: 100%;
  align-items: center;
  justify-content: space-between;
  white-space: nowrap;
  border-radius: 0.5rem;
  border: 1px solid hsl(var(--input, 35 15% 85%));
  background-color: hsl(var(--card, 40 25% 98%));
  padding: 0.5rem 0.75rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
  color: inherit;
  box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05);
  outline: none;
  cursor: pointer;
  text-align: start;
  font-family: inherit;
}

.select-trigger:focus {
  outline: none;
  box-shadow: 0 0 0 2px hsl(var(--ring, 245 43% 52%) / 0.2);
  border-color: hsl(var(--primary, 245 43% 52%) / 0.5);
}

.select-trigger:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.select-trigger__content {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
  min-width: 0;
}

.select-trigger__icon {
  width: 1rem;
  height: 1rem;
  opacity: 0.5;
  flex-shrink: 0;
  margin-left: 0.5rem;
}
</style>
