import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import type { AuthResponse, MembershipSummary, UserResponse } from '@/types/auth'

const REFRESH_TOKEN_KEY = 'refresh_token'

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref<string | null>(null)
  const user = ref<UserResponse | null>(null)
  const organizationId = ref<number | null>(null)
  const role = ref<string | null>(null)
  const memberships = ref<MembershipSummary[]>([])

  const isAuthenticated = computed(() => !!accessToken.value && !!organizationId.value)

  function setAuth(response: AuthResponse) {
    accessToken.value = response.accessToken
    user.value = response.user
    organizationId.value = response.organizationId
    role.value = response.role
    localStorage.setItem(REFRESH_TOKEN_KEY, response.refreshToken)
  }

  function setMemberships(list: MembershipSummary[]) {
    memberships.value = list
  }

  function getRefreshToken(): string | null {
    return localStorage.getItem(REFRESH_TOKEN_KEY)
  }

  function clearAuth() {
    accessToken.value = null
    user.value = null
    organizationId.value = null
    role.value = null
    memberships.value = []
    localStorage.removeItem(REFRESH_TOKEN_KEY)
  }

  return {
    accessToken,
    user,
    organizationId,
    role,
    memberships,
    isAuthenticated,
    setAuth,
    setMemberships,
    getRefreshToken,
    clearAuth,
  }
})
