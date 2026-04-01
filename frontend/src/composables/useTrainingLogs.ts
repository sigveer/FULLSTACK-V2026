import { useMutation, useQuery, useQueryClient } from '@tanstack/vue-query'
import api from '@/lib/api'
import type {
  CreateTrainingLogRequest,
  TrainingLog,
  UpdateTrainingLogRequest,
} from '@/types/training'

export { useMembersQuery as useOrganizationMembersQuery } from './useMembers'

const trainingLogsQueryKey = ['training-logs']

export function useTrainingLogsQuery() {
  return useQuery({
    queryKey: trainingLogsQueryKey,
    queryFn: () => api.get<TrainingLog[]>('/training-logs').then((r) => r.data),
  })
}

export function useCreateTrainingLogMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (payload: CreateTrainingLogRequest) =>
      api.post<TrainingLog>('/training-logs', payload).then((r) => r.data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: trainingLogsQueryKey })
    },
  })
}

export function useUpdateTrainingLogMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: UpdateTrainingLogRequest }) =>
      api.patch<TrainingLog>(`/training-logs/${id}`, payload).then((r) => r.data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: trainingLogsQueryKey })
    },
  })
}

export function useDeleteTrainingLogMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => api.delete(`/training-logs/${id}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: trainingLogsQueryKey })
    },
  })
}
