<script setup lang="ts">
import {
  LayoutDashboard,
  ClipboardCheck,
  Thermometer,
  GraduationCap,
  Award,
  AlertTriangle,
  LifeBuoy,
  Send,
} from 'lucide-vue-next'

import NavMain from '@/components/NavMain.vue'
import NavSecondary from '@/components/NavSecondary.vue'
import NavUser from '@/components/NavUser.vue'
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarSeparator,
} from '@/components/ui/sidebar'

const data = {
  user: {
    name: 'Leder Ledersen',
    email: 'leder@everest.no',
    avatar: '',
  },
  navMain: [
    {
      title: 'Oversikt',
      url: '#',
      icon: LayoutDashboard,
      isActive: true,
      items: [
        { title: 'Dashboard', url: '#' },
      ],
    },
    {
      title: 'IK-Mat',
      url: '#',
      icon: ClipboardCheck,
      items: [
        { title: 'Sjekklister', url: '#' },
        { title: 'Temperaturlogg', url: '#' },
      ],
    },
    {
      title: 'IK-Alkohol',
      url: '#',
      icon: GraduationCap,
      items: [
        { title: 'Opplaering', url: '#' },
        { title: 'Bevilling', url: '#' },
      ],
    },
    {
      title: 'Felles',
      url: '#',
      icon: AlertTriangle,
      items: [
        { title: 'Avvik', url: '#' },
      ],
    },
  ],
  navSecondary: [
    { title: 'Support', url: '#', icon: LifeBuoy },
    { title: 'Feedback', url: '#', icon: Send },
  ],
}
</script>

<template>
  <Sidebar variant="inset" collapsible="icon">
    <SidebarHeader>
      <SidebarMenu>
        <SidebarMenuItem>
          <SidebarMenuButton size="lg" as="a" href="#">
            <div class="sidebar-brand-icon">
              IK
            </div>
            <div class="sidebar-brand-text">
              <span class="sidebar-brand-name">IK-Komplett</span>
              <span class="sidebar-brand-sub">Everest Sushi & Fusion</span>
            </div>
          </SidebarMenuButton>
        </SidebarMenuItem>
      </SidebarMenu>
    </SidebarHeader>
    <SidebarContent>
      <NavMain :items="data.navMain" />
      <NavSecondary :items="data.navSecondary" />
    </SidebarContent>
    <SidebarSeparator />
    <SidebarFooter>
      <NavUser :user="data.user" />
    </SidebarFooter>
  </Sidebar>
</template>

<style scoped>
.sidebar-brand-icon {
  display: flex;
  aspect-ratio: 1;
  width: 2rem;
  height: 2rem;
  align-items: center;
  justify-content: center;
  border-radius: 0.5rem;
  background-color: hsl(var(--primary, 245 43% 52%));
  color: hsl(var(--primary-foreground, 0 0% 100%));
  font-weight: 700;
  font-size: 0.75rem;
}

.sidebar-brand-text {
  display: grid;
  flex: 1;
  text-align: left;
  font-size: 0.875rem;
  line-height: 1.25;
}

.sidebar-brand-name {
  font-weight: 600;
  color: hsl(var(--primary, 245 43% 52%));
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sidebar-brand-sub {
  font-size: 0.75rem;
  color: hsl(var(--muted-foreground, 24 10% 40%));
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
