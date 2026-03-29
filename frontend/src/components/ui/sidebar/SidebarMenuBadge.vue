<script setup lang="ts">
import { useSidebar } from "./utils"

const props = defineProps<{
  class?: string
}>()

const { state, collapsible } = useSidebar()
</script>

<template>
  <div
    data-sidebar="menu-badge"
    :class="['sidebar-menu-badge', props.class]"
    :style="{
      display: state === 'collapsed' && collapsible === 'icon' ? 'none' : undefined,
    }"
  >
    <slot />
  </div>
</template>

<style scoped>
.sidebar-menu-badge {
  position: absolute;
  right: 0.25rem;
  display: flex;
  height: 1.25rem;
  min-width: 1.25rem;
  align-items: center;
  justify-content: center;
  border-radius: 0.375rem;
  padding: 0 0.25rem;
  font-size: 0.75rem;
  line-height: 1rem;
  font-weight: 500;
  font-variant-numeric: tabular-nums;
  color: hsl(var(--sidebar-foreground, 24 10% 10%));
  user-select: none;
  pointer-events: none;
  top: 0.375rem;
}
</style>
