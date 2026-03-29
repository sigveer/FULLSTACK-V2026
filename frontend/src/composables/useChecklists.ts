import { useMutation, useQuery, useQueryClient } from '@tanstack/vue-query'
import api from '@/lib/api'
import type {
  Checklist,
  ChecklistStats,
  CreateChecklistItemRequest,
  CreateChecklistRequest,
  UpdateChecklistItemRequest,
  UpdateChecklistRequest,
} from '@/types/checklist'

const checklistsQueryKey = ['checklists']
const checklistStatsQueryKey = ['checklists', 'stats']

export function useChecklistsQuery() {
  return useQuery({
    queryKey: checklistsQueryKey,
    queryFn: () => api.get<Checklist[]>('/checklists').then((r) => r.data),
  })
}

export function useChecklistStatsQuery() {
  return useQuery({
    queryKey: checklistStatsQueryKey,
    queryFn: () => api.get<ChecklistStats>('/checklists/stats').then((r) => r.data),
  })
}

export function useCreateChecklistMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (payload: CreateChecklistRequest) =>
      api.post<Checklist>('/checklists', payload).then((r) => r.data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: checklistsQueryKey })
      queryClient.invalidateQueries({ queryKey: checklistStatsQueryKey })
    },
  })
}

export function useUpdateChecklistMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ checklistId, payload }: { checklistId: number; payload: UpdateChecklistRequest }) =>
      api.patch<Checklist>(`/checklists/${checklistId}`, payload).then((r) => r.data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: checklistsQueryKey })
      queryClient.invalidateQueries({ queryKey: checklistStatsQueryKey })
    },
  })
}

export function useDeleteChecklistMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (checklistId: number) => api.delete(`/checklists/${checklistId}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: checklistsQueryKey })
      queryClient.invalidateQueries({ queryKey: checklistStatsQueryKey })
    },
  })
}

export function useSetChecklistCompletionMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ checklistId, completed }: { checklistId: number; completed: boolean }) =>
      api.patch(`/checklists/${checklistId}/completion`, { completed }).then((r) => r.data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: checklistsQueryKey })
      queryClient.invalidateQueries({ queryKey: checklistStatsQueryKey })
    },
  })
}

export function useCreateChecklistItemMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ checklistId, payload }: { checklistId: number; payload: CreateChecklistItemRequest }) =>
      api.post(`/checklists/${checklistId}/items`, payload).then((r) => r.data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: checklistsQueryKey })
      queryClient.invalidateQueries({ queryKey: checklistStatsQueryKey })
    },
  })
}

export function useUpdateChecklistItemMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({
      checklistId,
      itemId,
      payload,
    }: {
      checklistId: number
      itemId: number
      payload: UpdateChecklistItemRequest
    }) => api.patch(`/checklists/${checklistId}/items/${itemId}`, payload).then((r) => r.data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: checklistsQueryKey })
      queryClient.invalidateQueries({ queryKey: checklistStatsQueryKey })
    },
  })
}

export function useDeleteChecklistItemMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ checklistId, itemId }: { checklistId: number; itemId: number }) =>
      api.delete(`/checklists/${checklistId}/items/${itemId}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: checklistsQueryKey })
      queryClient.invalidateQueries({ queryKey: checklistStatsQueryKey })
    },
  })
}
