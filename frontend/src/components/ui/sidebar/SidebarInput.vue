<script setup lang="ts">
import { Input } from "@/components/ui/input"

const props = defineProps<{
  class?: string
}>()
</script>

<template>
  <Input
    data-sidebar="input"
    :class="props.class"
    style="height: 2rem; width: 100%; background-color: hsl(var(--background, 43 30% 96%)); box-shadow: none;"
  >
    <slot />
  </Input>
</template>
