<script setup lang="ts">
import { PanelLeft } from "lucide-vue-next"
import { Button } from "@/components/ui/button--------------------"
import { useSidebar } from "./utils"

const props = defineProps<{
  class?: string
}>()

const { toggleSidebar } = useSidebar()
</script>

<template>
  <Button
    data-sidebar="trigger"
    variant="ghost"
    size="icon"
    :class="props.class"
    style="height: 1.75rem; width: 1.75rem;"
    @click="toggleSidebar"
  >
    <PanelLeft />
    <span class="sr-only">Toggle Sidebar</span>
  </Button>
</template>
