import { computed, type Ref } from 'vue'
import { useMutation, useQuery, useQueryClient } from '@tanstack/vue-query'
import api from '@/lib/api'
import type { OrganizationMember, OrganizationInfo, UpdateMemberRoleRequest } from '@/types/member'

export const membersQueryKey = ['members']

export function useMembersQuery(enabled?: Ref<boolean>) {
  return useQuery({
    queryKey: membersQueryKey,
    queryFn: () => api.get<OrganizationMember[]>('/users').then((r) => r.data),
    ...(enabled ? { enabled: computed(() => enabled.value) } : {}),
  })
}

export function useOrganizationQuery(id: Ref<number | null>) {
  const enabled = computed(() => id.value !== null)

  return useQuery({
    queryKey: computed(() => ['organization', id.value]),
    queryFn: () => api.get<OrganizationInfo>(`/organizations/${id.value}`).then((r) => r.data),
    enabled,
  })
}

export function useUpdateMemberRoleMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ userId, payload }: { userId: number; payload: UpdateMemberRoleRequest }) =>
      api.patch<OrganizationMember>(`/users/${userId}/role`, payload).then((r) => r.data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: membersQueryKey })
    },
  })
}

export function useRemoveMemberMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (userId: number) => api.delete(`/users/${userId}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: membersQueryKey })
    },
  })
}
