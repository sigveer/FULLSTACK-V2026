<script setup lang="ts">
withDefaults(defineProps<{
  label: string
  value: string | number
  subLabel?: string
  valueClass?: string
}>(), { valueClass: 'text-gray-900' })
</script>

<template>
  <div class="bg-white border border-stone-200 rounded-2xl p-5">
    <p class="text-xs text-gray-400 mb-1.5">{{ label }}</p>
    <p :class="['text-4xl font-bold leading-none', valueClass]">{{ value }}</p>
    <slot />
    <p v-if="subLabel" class="text-xs text-amber-600 mt-1">{{ subLabel }}</p>
  </div>
</template>
