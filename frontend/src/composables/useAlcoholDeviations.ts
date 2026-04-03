import { useMutation, useQuery, useQueryClient } from '@tanstack/vue-query'
import api from '@/lib/api'
import { penaltyPointsQueryKey } from '@/composables/usePenaltyPoints'
import type {
  AlcoholDeviation,
  CreateAlcoholDeviationRequest,
  UpdateAlcoholDeviationRequest,
} from '@/types/deviation'

export const alcoholDeviationsQueryKey = ['alcohol-deviations']

export function useAlcoholDeviationsQuery() {
  return useQuery({
    queryKey: alcoholDeviationsQueryKey,
    queryFn: () => api.get<AlcoholDeviation[]>('/deviations/alcohol').then((r) => r.data),
  })
}

export function useCreateAlcoholDeviationMutation() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: CreateAlcoholDeviationRequest) =>
      api.post<AlcoholDeviation>('/deviations/alcohol', payload).then((r) => r.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: alcoholDeviationsQueryKey })
      qc.invalidateQueries({ queryKey: penaltyPointsQueryKey })
    },
  })
}

export function useUpdateAlcoholDeviationMutation() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: UpdateAlcoholDeviationRequest }) =>
      api.patch<AlcoholDeviation>(`/deviations/alcohol/${id}`, payload).then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: alcoholDeviationsQueryKey }),
  })
}

export function useDeleteAlcoholDeviationMutation() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => api.delete(`/deviations/alcohol/${id}`),
    onSuccess: () => qc.invalidateQueries({ queryKey: alcoholDeviationsQueryKey }),
  })
}
