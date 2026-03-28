<script setup lang="ts">
const props = withDefaults(defineProps<{
  align?: "inline-start" | "inline-end" | "block-start" | "block-end"
  class?: string
}>(), {
  align: "inline-start",
})

function handleClick(e: MouseEvent) {
  const target = e.target as HTMLElement | null
  if (target && target.closest("button")) return
  const el = e.currentTarget as HTMLElement | null
  el?.parentElement?.querySelector("input")?.focus()
}
</script>

<template>
  <div
    role="group"
    data-slot="input-group-addon"
    :data-align="props.align"
    :class="['input-group-addon', `input-group-addon--${props.align}`, props.class]"
    @click="handleClick"
  >
    <slot />
  </div>
</template>

<style scoped>
.input-group-addon {
  color: hsl(var(--muted-foreground, 24 5% 46%));
  display: flex;
  height: auto;
  cursor: text;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding-top: 0.375rem;
  padding-bottom: 0.375rem;
  font-size: 0.875rem;
  font-weight: 500;
  user-select: none;
}

.input-group-addon :deep(svg:not([class*="size-"])) {
  width: 1rem;
  height: 1rem;
}

.input-group-addon--inline-start {
  order: -1;
  padding-left: 0.75rem;
}

.input-group-addon--inline-end {
  order: 999;
  padding-right: 0.75rem;
}

.input-group-addon--block-start {
  order: -1;
  width: 100%;
  justify-content: flex-start;
  padding-left: 0.75rem;
  padding-right: 0.75rem;
  padding-top: 0.75rem;
}

.input-group-addon--block-end {
  order: 999;
  width: 100%;
  justify-content: flex-start;
  padding-left: 0.75rem;
  padding-right: 0.75rem;
  padding-bottom: 0.75rem;
}
</style>
