export interface OrganizationMember {
  id: number
  userId: number
  userEmail: string
  userFullName: string
  organizationId: number
  role: string
}

export interface UpdateMemberRoleRequest {
  role: string
}

export interface OrganizationInfo {
  id: number
  name: string
  orgNumber: string | null
}
