<script setup lang="ts">
import { useSidebar } from "./utils"

const props = defineProps<{
  class?: string
}>()

const { state, collapsible } = useSidebar()
</script>

<template>
  <div
    data-sidebar="content"
    :class="['sidebar-content', props.class]"
    :style="{
      overflow: state === 'collapsed' && collapsible === 'icon' ? 'hidden' : undefined,
    }"
  >
    <slot />
  </div>
</template>

<style scoped>
.sidebar-content {
  display: flex;
  min-height: 0;
  flex: 1;
  flex-direction: column;
  gap: 0.5rem;
  overflow: auto;
}
</style>
