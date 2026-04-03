import { useMutation, useQuery, useQueryClient } from '@tanstack/vue-query'
import api from '@/lib/api'
import type { CreatePenaltyPointRequest, PenaltyPointSummary, PenaltyPoint } from '@/types/deviation'

export const penaltyPointsQueryKey = ['penalty-points']

export function usePenaltyPointsQuery() {
  return useQuery({
    queryKey: penaltyPointsQueryKey,
    queryFn: () => api.get<PenaltyPointSummary>('/penalty-points').then((r) => r.data),
  })
}

export function useAddPenaltyPointMutation() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: CreatePenaltyPointRequest) =>
      api.post<PenaltyPoint>('/penalty-points', payload).then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: penaltyPointsQueryKey }),
  })
}

export function useDeletePenaltyPointMutation() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => api.delete(`/penalty-points/${id}`),
    onSuccess: () => qc.invalidateQueries({ queryKey: penaltyPointsQueryKey }),
  })
}
