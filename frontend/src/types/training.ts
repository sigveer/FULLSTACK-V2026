export type TrainingStatus = 'COMPLETED' | 'EXPIRES_SOON' | 'EXPIRED' | 'NOT_COMPLETED'

export interface TrainingLog {
  id: number
  organizationId: number
  employeeUserId: number
  employeeUserName: string
  loggedByUserId: number
  loggedByUserName: string
  title: string
  description: string | null
  completedAt: string | null
  expiresAt: string | null
  status: TrainingStatus
  createdAt: string
  updatedAt: string
}

export interface CreateTrainingLogRequest {
  employeeUserId: number
  title: string
  description?: string
  completedAt?: string
  expiresAt?: string
  status: TrainingStatus
}

export interface UpdateTrainingLogRequest {
  employeeUserId?: number
  title?: string
  description?: string
  completedAt?: string
  expiresAt?: string
  status?: TrainingStatus
}
