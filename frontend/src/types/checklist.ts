export type ChecklistFrequency = 'DAILY' | 'WEEKLY' | 'MONTHLY' | 'YEARLY'

export interface ChecklistItem {
  id: number
  title: string
  description: string | null
  completed: boolean
  completedAt: string | null
}

export type ChecklistStatus = 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED'

export interface Checklist {
  id: number
  name: string
  description: string | null
  frequency: ChecklistFrequency
  active: boolean
  itemCount: number
  completedItemCount: number
  status: ChecklistStatus
  items: ChecklistItem[]
}

export interface ChecklistStats {
  activeChecklists: number
  totalChecklistItems: number
}

export interface CreateChecklistRequest {
  name: string
  description?: string
  frequency: ChecklistFrequency
}

export interface UpdateChecklistRequest {
  name?: string
  description?: string
  frequency?: ChecklistFrequency
  active?: boolean
}

export interface CreateChecklistItemRequest {
  title: string
  description?: string
  completed?: boolean
}

export interface UpdateChecklistItemRequest {
  title?: string
  description?: string
  completed?: boolean
}
