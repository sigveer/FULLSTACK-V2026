<script setup lang="ts">
import { useSidebar } from "./utils"

const props = defineProps<{
  class?: string
}>()

const { toggleSidebar } = useSidebar()
</script>

<template>
  <button
    data-sidebar="rail"
    aria-label="Toggle Sidebar"
    :tabindex="-1"
    title="Toggle Sidebar"
    :class="['sidebar-rail', props.class]"
    @click="toggleSidebar"
  >
    <slot />
  </button>
</template>

<style scoped>
.sidebar-rail {
  position: absolute;
  inset-block: 0;
  z-index: 20;
  display: none;
  width: 1rem;
  translate: -50% 0;
  border: none;
  background: transparent;
  cursor: pointer;
  padding: 0;
  transition: all 200ms linear;
}

@media (min-width: 640px) {
  .sidebar-rail {
    display: flex;
  }
}

.sidebar-rail::after {
  content: "";
  position: absolute;
  inset-block: 0;
  left: 50%;
  width: 2px;
}

.sidebar-rail:hover::after {
  background-color: hsl(var(--sidebar-border, 35 15% 90%));
}
</style>
