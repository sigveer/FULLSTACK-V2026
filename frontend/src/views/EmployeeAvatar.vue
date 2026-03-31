<script setup lang="ts">
withDefaults(defineProps<{
  initials: string
  color: string
  size?: 'sm' | 'lg'
}>(), { size: 'sm' })
</script>

<template>
  <div
    :class="[
      'rounded-full flex items-center justify-center text-white font-semibold flex-shrink-0',
      size === 'lg' ? 'w-13 h-13 text-lg' : 'w-9 h-9 text-xs'
    ]"
    :style="{ background: color }"
  >
    {{ initials }}
  </div>
</template>
