<script setup lang="ts">
import { provide } from "vue"

const props = withDefaults(defineProps<{
  delayDuration?: number
}>(), {
  delayDuration: 700,
})

provide("tooltip-provider", { delayDuration: props.delayDuration })
</script>

<template>
  <slot />
</template>
