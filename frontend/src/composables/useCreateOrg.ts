import { useMutation } from '@tanstack/vue-query'
import api from '@/lib/api'
import type { CreateOrgRequest, CreateOrgResponse } from '@/types/organization'

export function useCreateOrg() {
  return useMutation({
    mutationFn: (data: CreateOrgRequest) =>
      api.post<CreateOrgResponse>('/organizations', data).then((r) => r.data),
  })
}
