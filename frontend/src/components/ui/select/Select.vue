<script setup lang="ts">
import { provide, ref, reactive, computed, watch, onMounted, onBeforeUnmount } from "vue"

const props = defineProps<{
  modelValue?: string
  defaultValue?: string
}>()

const emits = defineEmits<{
  (e: "update:modelValue", value: string): void
}>()

const isOpen = ref(false)
const selectedValue = ref(props.modelValue ?? props.defaultValue ?? "")
const itemLabels = reactive<Record<string, string>>({})

watch(() => props.modelValue, (v) => {
  if (v !== undefined) selectedValue.value = v
})

function select(value: string) {
  selectedValue.value = value
  emits("update:modelValue", value)
  isOpen.value = false
}

function registerItem(value: string, label: string) {
  itemLabels[value] = label
}

function toggle() {
  isOpen.value = !isOpen.value
}

function close() {
  isOpen.value = false
}

function onKeydown(e: KeyboardEvent) {
  if (e.key === "Escape") close()
}

onMounted(() => document.addEventListener("keydown", onKeydown))
onBeforeUnmount(() => document.removeEventListener("keydown", onKeydown))

provide("select", {
  isOpen,
  selectedValue: computed(() => selectedValue.value),
  selectedLabel: computed(() => itemLabels[selectedValue.value] ?? ""),
  select,
  registerItem,
  toggle,
  close,
})
</script>

<template>
  <div class="select-root" v-bind="$attrs">
    <slot />
  </div>
</template>

<style scoped>
.select-root {
  position: relative;
  display: inline-block;
  width: 100%;
}
</style>
