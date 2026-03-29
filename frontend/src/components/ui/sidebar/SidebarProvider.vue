<script setup lang="ts">
import { useEventListener, useMediaQuery } from "@vueuse/core"
import { computed, ref, watch } from "vue"
import {
  provideSidebarContext,
  SIDEBAR_COOKIE_MAX_AGE,
  SIDEBAR_COOKIE_NAME,
  SIDEBAR_KEYBOARD_SHORTCUT,
  SIDEBAR_WIDTH,
  SIDEBAR_WIDTH_ICON,
} from "./utils"

const props = withDefaults(defineProps<{
  defaultOpen?: boolean
  open?: boolean
  class?: string
}>(), {
  defaultOpen: typeof document !== "undefined"
    ? !document.cookie.includes(`${SIDEBAR_COOKIE_NAME}=false`)
    : true,
  open: undefined,
})

const emits = defineEmits<{
  "update:open": [open: boolean]
}>()

const isMobile = useMediaQuery("(max-width: 768px)")
const openMobile = ref(false)
const open = ref(props.open ?? props.defaultOpen ?? true)

watch(() => props.open, (v) => {
  if (v !== undefined) open.value = v
})

function setOpen(value: boolean) {
  open.value = value
  emits("update:open", value)
  document.cookie = `${SIDEBAR_COOKIE_NAME}=${value}; path=/; max-age=${SIDEBAR_COOKIE_MAX_AGE}`
}

function setOpenMobile(value: boolean) {
  openMobile.value = value
}

function toggleSidebar() {
  return isMobile.value ? setOpenMobile(!openMobile.value) : setOpen(!open.value)
}

useEventListener("keydown", (event: KeyboardEvent) => {
  if (event.key === SIDEBAR_KEYBOARD_SHORTCUT && (event.metaKey || event.ctrlKey)) {
    event.preventDefault()
    toggleSidebar()
  }
})

const state = computed(() => open.value ? "expanded" : "collapsed")

provideSidebarContext({
  state,
  open,
  setOpen,
  isMobile,
  openMobile,
  setOpenMobile,
  toggleSidebar,
  variant: "sidebar",
  collapsible: "offcanvas",
  side: "left",
})
</script>

<template>
  <div
    class="sidebar-wrapper"
    :style="{
      '--sidebar-width': SIDEBAR_WIDTH,
      '--sidebar-width-icon': SIDEBAR_WIDTH_ICON,
    }"
    :class="props.class"
  >
    <slot />
  </div>
</template>

<style scoped>
.sidebar-wrapper {
  display: flex;
  min-height: 100svh;
  width: 100%;
}

.sidebar-wrapper:has([data-variant="inset"]) {
  background-color: hsl(var(--sidebar-background, 40 25% 98%));
}
</style>
