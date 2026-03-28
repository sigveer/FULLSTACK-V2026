import { useMutation, useQueryClient } from '@tanstack/vue-query'
import api from '@/lib/api'
import { useAuthStore } from '@/stores/auth'
import type {
  AuthResponse,
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  SelectOrgRequest,
} from '@/types/auth'
import router from '@/router'

export function useLogin() {
  const auth = useAuthStore()

  return useMutation({
    mutationFn: (data: LoginRequest) =>
      api.post<LoginResponse>('/auth/login', data).then((r) => r.data),
    onSuccess: (data) => {
      auth.accessToken = data.preAuthToken
      auth.user = data.user
      auth.setMemberships(data.memberships)
    },
  })
}

export function useRegister() {
  const auth = useAuthStore()

  return useMutation({
    mutationFn: (data: RegisterRequest) =>
      api.post<LoginResponse>('/auth/register', data).then((r) => r.data),
    onSuccess: (data) => {
      auth.accessToken = data.preAuthToken
      auth.user = data.user
      auth.setMemberships(data.memberships)
    },
  })
}

export function useSelectOrg() {
  const auth = useAuthStore()

  return useMutation({
    mutationFn: (data: SelectOrgRequest) =>
      api.post<AuthResponse>('/auth/select-org', data).then((r) => r.data),
    onSuccess: (data) => {
      auth.setAuth(data)
    },
  })
}

export function useSwitchOrg() {
  const auth = useAuthStore()
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: SelectOrgRequest) =>
      api.post<AuthResponse>('/auth/switch-org', data).then((r) => r.data),
    onSuccess: (data) => {
      auth.setAuth(data)
      queryClient.clear()
    },
  })
}

export function useLogout() {
  const auth = useAuthStore()
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: () => api.post('/auth/logout'),
    onSettled: () => {
      auth.clearAuth()
      queryClient.clear()
      router.push('/login')
    },
  })
}
