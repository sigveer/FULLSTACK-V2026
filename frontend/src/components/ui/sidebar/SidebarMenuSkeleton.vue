<script setup lang="ts">
import { computed } from "vue"
import { Skeleton } from "@/components/ui/skeleton--------------------"

const props = defineProps<{
  showIcon?: boolean
  class?: string
}>()

const width = computed(() => `${Math.floor(Math.random() * 40) + 50}%`)
</script>

<template>
  <div data-sidebar="menu-skeleton" :class="['sidebar-menu-skeleton', props.class]">
    <Skeleton
      v-if="showIcon"
      data-sidebar="menu-skeleton-icon"
      style="width: 1rem; height: 1rem; border-radius: 0.375rem;"
    />
    <Skeleton
      data-sidebar="menu-skeleton-text"
      :style="{ height: '1rem', flex: 1, maxWidth: width }"
    />
  </div>
</template>

<style scoped>
.sidebar-menu-skeleton {
  border-radius: 0.375rem;
  height: 2rem;
  display: flex;
  gap: 0.5rem;
  padding: 0 0.5rem;
  align-items: center;
}
</style>
