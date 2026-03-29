<script setup lang="ts">
import { inject } from "vue"
import { Button } from '@/components/ui/button--------------------'

const props = defineProps<{
  variant?: "default" | "destructive" | "outline" | "secondary" | "ghost" | "link"
  class?: string
}>()

const { close } = inject("alert-dialog") as any
</script>

<template>
  <Button :variant="variant ?? 'default'" :class="props.class" @click="close">
    <slot />
  </Button>
</template>
