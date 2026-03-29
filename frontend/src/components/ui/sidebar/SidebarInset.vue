<script setup lang="ts">
import { useSidebar } from "./utils"

const props = defineProps<{
  class?: string
}>()

const { variant } = useSidebar()
</script>

<template>
  <main
    :class="['sidebar-inset', variant === 'inset' && 'sidebar-inset--inset', props.class]"
  >
    <slot />
  </main>
</template>

<style scoped>
.sidebar-inset {
  position: relative;
  display: flex;
  min-height: 100svh;
  flex: 1;
  flex-direction: column;
  background-color: hsl(var(--background, 43 30% 96%));
}

@media (min-width: 768px) {
  .sidebar-inset--inset {
    margin: 0.5rem;
    margin-left: 0;
    border-radius: 0.75rem;
    box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1);
    min-height: calc(100svh - 1rem);
  }
}
</style>
