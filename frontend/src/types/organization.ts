export interface CreateOrgRequest {
  name: string
  orgNumber?: string
}

export interface CreateOrgResponse {
  id: number
  name: string
  orgNumber: string | null
}
