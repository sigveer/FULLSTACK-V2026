<script setup lang="ts">
import { Separator } from "@/components/ui/separator--------------------"

const props = defineProps<{
  class?: string
}>()
</script>

<template>
  <Separator
    data-sidebar="separator"
    :class="props.class"
    style="margin: 0 0.5rem; width: auto; background-color: hsl(var(--sidebar-border, 35 15% 90%));"
  >
    <slot />
  </Separator>
</template>
