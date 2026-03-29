<script setup lang="ts">
import { inject, computed, ref, onMounted, useSlots } from "vue"
import { CheckIcon } from "lucide-vue-next"

const props = defineProps<{
  value: string
  disabled?: boolean
  class?: string
}>()

const { selectedValue, select, registerItem } = inject("select--------------------") as any
const itemRef = ref<HTMLElement | null>(null)

const isSelected = computed(() => selectedValue.value === props.value)

onMounted(() => {
  const text = itemRef.value?.querySelector(".select---------------------item__text")?.textContent?.trim() ?? props.value
  registerItem(props.value, text)
})

function onClick() {
  if (props.disabled) return
  select(props.value)
}
</script>

<template>
  <div
    ref="itemRef"
    role="option"
    :aria-selected="isSelected"
    :data-state="isSelected ? 'checked' : 'unchecked'"
    :data-disabled="disabled || undefined"
    :class="['select-item', { 'select-item--disabled': disabled }, props.class]"
    @click="onClick"
  >
    <span class="select-item__indicator">
      <CheckIcon v-if="isSelected" class="select-item__check" />
    </span>
    <span class="select-item__text">
      <slot />
    </span>
  </div>
</template>

<style scoped>
.select-item {
  position: relative;
  display: flex;
  width: 100%;
  cursor: default;
  user-select: none;
  align-items: center;
  border-radius: 0.25rem;
  padding: 0.375rem 0.5rem 0.375rem 2rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
  outline: none;
  transition: background-color 100ms ease;
}

.select-item:hover {
  background-color: hsl(var(--accent, 250 40% 95%));
  color: hsl(var(--accent-foreground, 245 43% 42%));
}

.select-item--disabled {
  pointer-events: none;
  opacity: 0.5;
}

.select-item__indicator {
  position: absolute;
  left: 0.5rem;
  display: flex;
  height: 0.875rem;
  width: 0.875rem;
  align-items: center;
  justify-content: center;
}

.select-item__check {
  width: 1rem;
  height: 1rem;
}

.select-item__text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
