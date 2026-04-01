import { useMutation, useQuery, useQueryClient } from '@tanstack/vue-query'
import api from '@/lib/api'
import type {
  CreateDeviationRequest,
  Deviation,
  UpdateDeviationRequest,
  UpdateDeviationStatusRequest,
} from '@/types/deviation'

export { useMembersQuery as useOrganizationMembersQuery } from './useMembers'

const deviationsQueryKey = ['deviations']

export function useDeviationsQuery() {
  return useQuery({
    queryKey: deviationsQueryKey,
    queryFn: () => api.get<Deviation[]>('/deviations').then((r) => r.data),
  })
}

export function useCreateDeviationMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (payload: CreateDeviationRequest) =>
      api.post<Deviation>('/deviations', payload).then((r) => r.data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: deviationsQueryKey })
    },
  })
}

export function useUpdateDeviationMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: UpdateDeviationRequest }) =>
      api.patch<Deviation>(`/deviations/${id}`, payload).then((r) => r.data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: deviationsQueryKey })
    },
  })
}

export function useUpdateDeviationStatusMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: UpdateDeviationStatusRequest }) =>
      api.patch<Deviation>(`/deviations/${id}/status`, payload).then((r) => r.data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: deviationsQueryKey })
    },
  })
}

export function useDeleteDeviationMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => api.delete(`/deviations/${id}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: deviationsQueryKey })
    },
  })
}
