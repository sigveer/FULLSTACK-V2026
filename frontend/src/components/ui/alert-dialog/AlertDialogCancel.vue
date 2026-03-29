<script setup lang="ts">
import { computed, inject } from "vue"
import { Button } from '@/components/ui/button'

const props = defineProps<{
  class?: string
}>()

const { close } = inject("alert-dialog") as { close: () => void }

const buttonClass = computed(() =>
  ["alert-cancel", props.class].filter(Boolean).join(" "),
)
</script>

<template>
  <Button variant="outline" :class="buttonClass" @click="close">
    <slot />
  </Button>
</template>

<style scoped>
.alert-cancel {
  margin-top: 0.5rem;
}

@media (min-width: 640px) {
  .alert-cancel {
    margin-top: 0;
  }
}
</style>
