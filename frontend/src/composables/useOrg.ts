import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useSelectOrg } from '@/composables/useAuth'

export function useOrg() {
  const auth = useAuthStore()
  const selectOrg = useSelectOrg()

  const memberships = computed(() => auth.memberships)
  const hasMemberships = computed(() => memberships.value.length > 0)
  const hasSingleOrg = computed(() => memberships.value.length === 1)

  return {
    memberships,
    hasMemberships,
    hasSingleOrg,
    selectOrg,
  }
}
