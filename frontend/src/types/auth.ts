export interface UserResponse {
  id: number
  email: string
  fullName: string
  phoneNumber: string
  active: boolean
}

export interface MembershipSummary {
  membershipId: number
  organizationId: number
  organizationName: string
  role: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface LoginResponse {
  preAuthToken: string
  user: UserResponse
  memberships: MembershipSummary[]
}

export interface SelectOrgRequest {
  organizationId: number
}

export interface RegisterRequest {
  email: string
  password: string
  fullName: string
  phoneNumber: string
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  expiresIn: number
  user: UserResponse
  organizationId: number
  role: string
}
