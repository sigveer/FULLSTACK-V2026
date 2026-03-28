import { ref } from 'vue'
import axios from 'axios'
import type { AuthResponse } from '@/types/auth'
import { useAuthStore } from '@/stores/auth'

const isInitialized = ref(false)
const isLoading = ref(true)

export function useInitAuth() {
  const auth = useAuthStore()

  async function init() {
    const refreshToken = auth.getRefreshToken()

    if (!refreshToken) {
      isLoading.value = false
      isInitialized.value = true
      return
    }

    try {
      const { data } = await axios.post<AuthResponse>('/api/v1/auth/refresh', {
        refreshToken,
      })
      auth.setAuth(data)
    } catch {
      auth.clearAuth()
    } finally {
      isLoading.value = false
      isInitialized.value = true
    }
  }

  return { init, isInitialized, isLoading }
}
