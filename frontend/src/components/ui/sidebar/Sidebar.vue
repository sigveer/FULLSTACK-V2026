<script setup lang="ts">
import { Sheet, SheetContent } from "@/components/ui/sheet--------------------"
import { SIDEBAR_WIDTH_MOBILE, useSidebar } from "./utils"

defineOptions({
  inheritAttrs: false,
})

const props = withDefaults(defineProps<{
  side?: "left" | "right"
  variant?: "sidebar" | "floating" | "inset"
  collapsible?: "offcanvas" | "icon" | "none"
  class?: string
}>(), {
  side: "left",
  variant: "sidebar",
  collapsible: "offcanvas",
})

const ctx = useSidebar()
// Update context with this sidebar's props
ctx.variant = props.variant
ctx.collapsible = props.collapsible
ctx.side = props.side

const { isMobile, state, openMobile, setOpenMobile } = ctx
</script>

<template>
  <!-- Non-collapsible -->
  <div
    v-if="collapsible === 'none'"
    class="sidebar-static"
    :class="props.class"
    v-bind="$attrs"
  >
    <slot />
  </div>

  <!-- Mobile sheet -->
  <Sheet
    v-else-if="isMobile"
    :open="openMobile"
    v-bind="$attrs"
    @update:open="setOpenMobile"
  >
    <SheetContent
      data-sidebar="sidebar"
      data-mobile="true"
      :side="side"
      class="sidebar-mobile-sheet"
      :style="{ '--sidebar-width': SIDEBAR_WIDTH_MOBILE }"
    >
      <div class="sidebar-mobile-inner">
        <slot />
      </div>
    </SheetContent>
  </Sheet>

  <!-- Desktop sidebar -->
  <div
    v-else
    class="sidebar-desktop"
    :data-state="state"
    :data-collapsible="state === 'collapsed' ? collapsible : ''"
    :data-variant="variant"
    :data-side="side"
  >
    <!-- Spacer -->
    <div
      class="sidebar-spacer"
      :class="{
        'sidebar-spacer--offcanvas': state === 'collapsed' && collapsible === 'offcanvas',
        'sidebar-spacer--icon': state === 'collapsed' && collapsible === 'icon',
        'sidebar-spacer--icon-inset': (variant === 'floating' || variant === 'inset') && state === 'collapsed' && collapsible === 'icon',
      }"
    />
    <!-- Fixed sidebar -->
    <div
      class="sidebar-fixed"
      :class="[{
        'sidebar-fixed--left': side === 'left',
        'sidebar-fixed--right': side === 'right',
        'sidebar-fixed--offcanvas-left': side === 'left' && state === 'collapsed' && collapsible === 'offcanvas',
        'sidebar-fixed--offcanvas-right': side === 'right' && state === 'collapsed' && collapsible === 'offcanvas',
        'sidebar-fixed--inset': variant === 'floating' || variant === 'inset',
        'sidebar-fixed--inset-icon': (variant === 'floating' || variant === 'inset') && state === 'collapsed' && collapsible === 'icon',
        'sidebar-fixed--bordered': variant === 'sidebar',
        'sidebar-fixed--bordered-left': variant === 'sidebar' && side === 'left',
        'sidebar-fixed--bordered-right': variant === 'sidebar' && side === 'right',
        'sidebar-fixed--icon': state === 'collapsed' && collapsible === 'icon' && variant === 'sidebar',
      }, props.class]"
      v-bind="$attrs"
    >
      <div
        data-sidebar="sidebar"
        class="sidebar-inner"
        :class="{
          'sidebar-inner--floating': variant === 'floating',
        }"
      >
        <slot />
      </div>
    </div>
  </div>
</template>

<style scoped>
.sidebar-static {
  display: flex;
  height: 100%;
  width: var(--sidebar-width);
  flex-direction: column;
  background-color: hsl(var(--sidebar-background, 40 25% 98%));
  color: hsl(var(--sidebar-foreground, 24 10% 10%));
}

.sidebar-desktop {
  display: none;
}

@media (min-width: 768px) {
  .sidebar-desktop {
    display: block;
  }
}

.sidebar-spacer {
  position: relative;
  height: 100svh;
  width: var(--sidebar-width);
  background: transparent;
  transition: width 200ms linear;
}

.sidebar-spacer--offcanvas {
  width: 0;
}

.sidebar-spacer--icon {
  width: var(--sidebar-width-icon);
}

.sidebar-spacer--icon-inset {
  width: calc(var(--sidebar-width-icon) + 1rem);
}

.sidebar-fixed {
  position: fixed;
  inset-block: 0;
  z-index: 10;
  display: none;
  height: 100svh;
  width: var(--sidebar-width);
  transition: left 200ms linear, right 200ms linear, width 200ms linear;
}

@media (min-width: 768px) {
  .sidebar-fixed {
    display: flex;
  }
}

.sidebar-fixed--left {
  left: 0;
}

.sidebar-fixed--right {
  right: 0;
}

.sidebar-fixed--offcanvas-left {
  left: calc(var(--sidebar-width) * -1);
}

.sidebar-fixed--offcanvas-right {
  right: calc(var(--sidebar-width) * -1);
}

.sidebar-fixed--inset {
  padding: 0.5rem;
}

.sidebar-fixed--inset-icon {
  width: calc(var(--sidebar-width-icon) + 1rem + 2px);
}

.sidebar-fixed--bordered-left {
  border-right: 1px solid hsl(var(--sidebar-border, 35 15% 90%));
}

.sidebar-fixed--bordered-right {
  border-left: 1px solid hsl(var(--sidebar-border, 35 15% 90%));
}

.sidebar-fixed--icon {
  width: var(--sidebar-width-icon);
}

.sidebar-inner {
  display: flex;
  height: 100%;
  width: 100%;
  flex-direction: column;
  background-color: hsl(var(--sidebar-background, 40 25% 98%));
  color: hsl(var(--sidebar-foreground, 24 10% 10%));
}

.sidebar-inner--floating {
  border-radius: 0.5rem;
  border: 1px solid hsl(var(--sidebar-border, 35 15% 90%));
  box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05);
}

.sidebar-mobile-sheet {
  width: var(--sidebar-width);
  background-color: hsl(var(--sidebar-background, 40 25% 98%));
  padding: 0;
  color: hsl(var(--sidebar-foreground, 24 10% 10%));
}

.sidebar-mobile-sheet :deep(.sheet-close) {
  display: none;
}

.sidebar-mobile-inner {
  display: flex;
  height: 100%;
  width: 100%;
  flex-direction: column;
}
</style>
