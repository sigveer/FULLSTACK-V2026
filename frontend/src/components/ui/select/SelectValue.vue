<script setup lang="ts">
import { inject, computed } from "vue"

const props = defineProps<{
  placeholder?: string
}>()

const { selectedValue, selectedLabel } = inject("select--------------------") as any

const showPlaceholder = computed(() => !selectedValue.value)
</script>

<template>
  <span :class="['select-value', { 'select-value--placeholder': showPlaceholder }]">
    <template v-if="showPlaceholder">{{ placeholder }}</template>
    <template v-else>{{ selectedLabel }}</template>
  </span>
</template>

<style scoped>
.select-value {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.select-value--placeholder {
  color: hsl(var(--muted-foreground, 24 5% 46%));
}
</style>
