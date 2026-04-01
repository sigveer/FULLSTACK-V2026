<script setup lang="ts">
import {
  ChevronsUpDown,
  LogOut,
  Settings,
  Building2,
  Check,
} from 'lucide-vue-next'
import { computed } from 'vue'

import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import {
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  useSidebar,
} from '@/components/ui/sidebar'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useLogout, useMemberships, useSwitchOrg } from '@/composables/useAuth'

const router = useRouter()
const auth = useAuthStore()
const { mutate: logout } = useLogout()
const { mutate: switchOrg } = useSwitchOrg()
useMemberships()
const { isMobile } = useSidebar()

const userName = computed(() => auth.user?.fullName ?? 'Bruker')
const userEmail = computed(() => auth.user?.email ?? '')

const initials = computed(() => {
  const parts = userName.value.split(' ').filter(Boolean)
  if (parts.length >= 2) {
    return ((parts[0]?.[0] ?? '') + (parts[parts.length - 1]?.[0] ?? '')).toUpperCase()
  }
  return parts[0]?.[0]?.toUpperCase() ?? '?'
})

function handleSwitchOrg(organizationId: number) {
  switchOrg({ organizationId })
}
</script>

<template>
  <SidebarMenu>
    <SidebarMenuItem>
      <DropdownMenu>
        <DropdownMenuTrigger as-child>
          <SidebarMenuButton size="lg">
            <div class="nav-user-avatar">
              {{ initials }}
            </div>
            <div class="nav-user-info">
              <span class="nav-user-name">{{ userName }}</span>
              <span class="nav-user-email">{{ userEmail }}</span>
            </div>
            <ChevronsUpDown class="nav-user-chevron" />
          </SidebarMenuButton>
        </DropdownMenuTrigger>
        <DropdownMenuContent
          :side="isMobile ? 'bottom' : 'right'"
          align="end"
          :side-offset="4"
        >
          <DropdownMenuLabel>
            <div class="nav-user-dropdown-header">
              <div class="nav-user-avatar">
                {{ initials }}
              </div>
              <div class="nav-user-info">
                <span class="nav-user-name">{{ userName }}</span>
                <span class="nav-user-email">{{ userEmail }}</span>
              </div>
            </div>
          </DropdownMenuLabel>

          <template v-if="auth.memberships.length > 1">
            <DropdownMenuSeparator />
            <DropdownMenuLabel class="nav-org-label">
              Organisasjoner
            </DropdownMenuLabel>
            <DropdownMenuItem
              v-for="membership in auth.memberships"
              :key="membership.membershipId"
              class="nav-org-item"
              @click="membership.organizationId !== auth.organizationId && handleSwitchOrg(membership.organizationId)"
            >
              <Building2 />
              <span>{{ membership.organizationName }}</span>
              <Check
                v-if="membership.organizationId === auth.organizationId"
                class="nav-org-check"
              />
            </DropdownMenuItem>
          </template>

          <DropdownMenuSeparator />
          <DropdownMenuItem @click="router.push('/innstillinger')">
            <Settings />
            Innstillinger
          </DropdownMenuItem>
          <DropdownMenuSeparator />
          <DropdownMenuItem @click="logout()">
            <LogOut />
            Logg ut
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </SidebarMenuItem>
  </SidebarMenu>
</template>

<style scoped>
.nav-user-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  border-radius: 0.5rem;
  background-color: hsl(var(--primary, 245 43% 52%));
  color: hsl(var(--primary-foreground, 0 0% 100%));
  font-weight: 600;
  font-size: 0.75rem;
  flex-shrink: 0;
}

.nav-user-info {
  display: grid;
  flex: 1;
  text-align: left;
  font-size: 0.875rem;
  line-height: 1.25;
  min-width: 0;
}

.nav-user-name {
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.nav-user-email {
  font-size: 0.75rem;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: hsl(var(--muted-foreground, 24 10% 40%));
}

.nav-user-chevron {
  margin-left: auto;
  width: 1rem;
  height: 1rem;
  flex-shrink: 0;
}

.nav-user-dropdown-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.nav-org-label {
  font-size: 0.75rem;
  color: hsl(var(--muted-foreground, 24 10% 40%));
  font-weight: 500;
}

.nav-org-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.nav-org-check {
  margin-left: auto;
  width: 1rem;
  height: 1rem;
  color: hsl(var(--primary, 245 43% 52%));
}
</style>
