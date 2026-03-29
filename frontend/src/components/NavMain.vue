<script setup lang="ts">
import type { Component } from 'vue'
import { RouterLink, useRoute } from 'vue-router'

import {
  SidebarGroup,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from '@/components/ui/sidebar'

defineProps<{
  sections: {
    label: string
    items: {
      title: string
      url: string
      icon: Component
    }[]
  }[]
}>()

const route = useRoute()
</script>

<template>
  <SidebarGroup v-for="section in sections" :key="section.label">
    <SidebarGroupLabel>{{ section.label }}</SidebarGroupLabel>
    <SidebarMenu>
      <SidebarMenuItem v-for="item in section.items" :key="item.title">
        <SidebarMenuButton
          :as="RouterLink"
          :to="item.url"
          :tooltip="item.title"
          :is-active="route.path === item.url"
        >
          <component :is="item.icon" />
          <span>{{ item.title }}</span>
        </SidebarMenuButton>
      </SidebarMenuItem>
    </SidebarMenu>
  </SidebarGroup>
</template>
