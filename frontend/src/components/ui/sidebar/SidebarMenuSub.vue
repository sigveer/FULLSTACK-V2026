<script setup lang="ts">
import { useSidebar } from "./utils"

const props = defineProps<{
  class?: string
}>()

const { state, collapsible } = useSidebar()
</script>

<template>
  <ul
    data-sidebar="menu-sub"
    :class="['sidebar-menu-sub', props.class]"
    :style="{
      display: state === 'collapsed' && collapsible === 'icon' ? 'none' : undefined,
    }"
  >
    <slot />
  </ul>
</template>

<style scoped>
.sidebar-menu-sub {
  margin: 0 0.875rem;
  display: flex;
  min-width: 0;
  translate: 1px 0;
  flex-direction: column;
  gap: 0.25rem;
  border-left: 1px solid hsl(var(--sidebar-border, 35 15% 90%));
  padding: 0.125rem 0 0.125rem 0.625rem;
  list-style: none;
}
</style>
