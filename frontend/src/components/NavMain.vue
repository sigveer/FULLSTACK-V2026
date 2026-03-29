<script setup lang="ts">
import { ChevronRight } from 'lucide-vue-next'
import type { Component } from 'vue'

import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from '@/components/ui/collapsible'
import {
  SidebarGroup,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuAction,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarMenuSub,
  SidebarMenuSubButton,
  SidebarMenuSubItem,
} from '@/components/ui/sidebar'

defineProps<{
  items: {
    title: string
    url: string
    icon: Component
    isActive?: boolean
    items?: {
      title: string
      url: string
    }[]
  }[]
}>()
</script>

<template>
  <SidebarGroup>
    <SidebarGroupLabel>Navigasjon</SidebarGroupLabel>
    <SidebarMenu>
      <SidebarMenuItem
        v-for="item in items"
        :key="item.title"
      >
        <Collapsible :default-open="true">
          <SidebarMenuButton as="a" :href="item.url" :tooltip="item.title">
            <component :is="item.icon" />
            <span>{{ item.title }}</span>
          </SidebarMenuButton>
          <template v-if="item.items?.length">
            <CollapsibleTrigger as-child>
              <SidebarMenuAction class="nav-chevron">
                <ChevronRight />
                <span class="sr-only">Toggle</span>
              </SidebarMenuAction>
            </CollapsibleTrigger>
            <CollapsibleContent>
              <SidebarMenuSub>
                <SidebarMenuSubItem v-for="subItem in item.items" :key="subItem.title">
                  <SidebarMenuSubButton :href="subItem.url">
                    <span>{{ subItem.title }}</span>
                  </SidebarMenuSubButton>
                </SidebarMenuSubItem>
              </SidebarMenuSub>
            </CollapsibleContent>
          </template>
        </Collapsible>
      </SidebarMenuItem>
    </SidebarMenu>
  </SidebarGroup>
</template>

<style scoped>
.nav-chevron :deep(svg) {
  transition: transform 200ms ease;
}

[data-state="open"] > .nav-chevron :deep(svg) {
  transform: rotate(90deg);
}
</style>
