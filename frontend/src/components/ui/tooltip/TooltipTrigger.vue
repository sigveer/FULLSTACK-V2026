<script setup lang="ts">
import { inject } from "vue"

defineProps<{
  asChild?: boolean
}>()

const { show, hide } = inject("tooltip") as any
</script>

<template>
  <div style="display: contents" @mouseenter="show" @mouseleave="hide" @focus="show" @blur="hide">
    <slot />
  </div>
</template>
