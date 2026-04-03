import { useMutation, useQuery, useQueryClient } from '@tanstack/vue-query'
import api from '@/lib/api'
import type {
  CreateFoodDeviationRequest,
  FoodDeviation,
  UpdateFoodDeviationRequest,
} from '@/types/deviation'

export const foodDeviationsQueryKey = ['food-deviations']

export function useFoodDeviationsQuery() {
  return useQuery({
    queryKey: foodDeviationsQueryKey,
    queryFn: () => api.get<FoodDeviation[]>('/deviations/food').then((r) => r.data),
  })
}

export function useCreateFoodDeviationMutation() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: CreateFoodDeviationRequest) =>
      api.post<FoodDeviation>('/deviations/food', payload).then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: foodDeviationsQueryKey }),
  })
}

export function useUpdateFoodDeviationMutation() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: UpdateFoodDeviationRequest }) =>
      api.patch<FoodDeviation>(`/deviations/food/${id}`, payload).then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: foodDeviationsQueryKey }),
  })
}

export function useDeleteFoodDeviationMutation() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => api.delete(`/deviations/food/${id}`),
    onSuccess: () => qc.invalidateQueries({ queryKey: foodDeviationsQueryKey }),
  })
}
