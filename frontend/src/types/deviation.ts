// ── Shared enums ────────────────────────────────────────────
export type DeviationModule = 'IK_MAT' | 'IK_ALKOHOL'
export type DeviationSeverity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'

// ── Food deviation types ────────────────────────────────────
export type FoodDeviationType =
  | 'TEMPERATUR'
  | 'RENHOLD'
  | 'PERSONLIG_HYGIENE'
  | 'ALLERGEN'
  | 'SKADEDYR'
  | 'MOTTAKSKONTROLL'
  | 'ANNET'

export type FoodDeviationStatus = 'OPEN' | 'UNDER_TREATMENT' | 'CLOSED'

export interface FoodDeviation {
  id: number
  organizationId: number
  reportedAt: string
  reportedByUserId: number
  reportedByUserName: string
  deviationType: FoodDeviationType
  severity: DeviationSeverity
  description: string
  immediateAction: string | null
  immediateActionByUserId: number | null
  immediateActionByUserName: string | null
  immediateActionAt: string | null
  cause: string | null
  preventiveMeasures: string | null
  preventiveResponsibleUserId: number | null
  preventiveResponsibleUserName: string | null
  preventiveDeadline: string | null
  status: FoodDeviationStatus
  createdAt: string
  updatedAt: string
}

export interface CreateFoodDeviationRequest {
  reportedAt?: string
  deviationType: FoodDeviationType
  severity: DeviationSeverity
  description: string
  immediateAction?: string
  immediateActionByUserId?: number
  immediateActionAt?: string
  cause?: string
  preventiveMeasures?: string
  preventiveResponsibleUserId?: number
  preventiveDeadline?: string
}

export interface UpdateFoodDeviationRequest {
  reportedAt?: string
  deviationType?: FoodDeviationType
  severity?: DeviationSeverity
  description?: string
  immediateAction?: string
  immediateActionByUserId?: number
  immediateActionAt?: string
  cause?: string
  preventiveMeasures?: string
  preventiveResponsibleUserId?: number
  preventiveDeadline?: string
  status?: FoodDeviationStatus
}

// ── Alcohol deviation types ─────────────────────────────────
export type AlcoholReportSource = 'EGENRAPPORT' | 'SJENKEKONTROLL' | 'POLITIRAPPORT'

export type AlcoholDeviationType =
  | 'SKJENKING_MINDREAARIGE'
  | 'BRUDD_BISTANDSPLIKT'
  | 'UFORSVARLIG_DRIFT'
  | 'HINDRING_KONTROLL'
  | 'SKJENKING_APENBART_BERUSET'
  | 'BRUDD_SJENKETIDER'
  | 'BRENNEVIN_18_19'
  | 'BERUSET_PERSON_I_LOKALET'
  | 'MANGLER_IK_SYSTEM'
  | 'MANGLER_STYRER_STEDFORTREDER'
  | 'NARKOTIKA'
  | 'ALKOHOLFRI_ALTERNATIV_MANGLER'
  | 'MEDBRAKT_ALKOHOL'
  | 'REKLAMEBRUDD'
  | 'VILKAARSBRUDD'

export type AlcoholCausalAnalysis =
  | 'MANGLENDE_OPPLAERING'
  | 'RUTINE_IKKE_FULGT'
  | 'RUTINE_MANGLER'
  | 'HOYT_TRYKK_STRESS'
  | 'UNDERBEMANNING'
  | 'KOMMUNIKASJON'
  | 'ANNET'

export type AlcoholDeviationStatus = 'OPEN' | 'UNDER_TREATMENT' | 'CLOSED'

export interface AlcoholDeviation {
  id: number
  organizationId: number
  reportedAt: string
  reportedByUserId: number
  reportedByUserName: string
  reportSource: AlcoholReportSource
  deviationType: AlcoholDeviationType
  description: string
  immediateAction: string | null
  causalAnalysis: AlcoholCausalAnalysis | null
  causalExplanation: string | null
  preventiveMeasures: string | null
  preventiveDeadline: string | null
  preventiveResponsibleUserId: number | null
  preventiveResponsibleUserName: string | null
  status: AlcoholDeviationStatus
  createdAt: string
  updatedAt: string
}

export interface CreateAlcoholDeviationRequest {
  reportedAt?: string
  reportSource: AlcoholReportSource
  deviationType: AlcoholDeviationType
  description: string
  immediateAction?: string
  causalAnalysis?: AlcoholCausalAnalysis
  causalExplanation?: string
  preventiveMeasures?: string
  preventiveDeadline?: string
  preventiveResponsibleUserId?: number
}

export interface UpdateAlcoholDeviationRequest {
  reportedAt?: string
  reportSource?: AlcoholReportSource
  deviationType?: AlcoholDeviationType
  description?: string
  immediateAction?: string
  causalAnalysis?: AlcoholCausalAnalysis
  causalExplanation?: string
  preventiveMeasures?: string
  preventiveDeadline?: string
  preventiveResponsibleUserId?: number
  status?: AlcoholDeviationStatus
}

// ── Penalty points types ────────────────────────────────────
export interface PenaltyPoint {
  id: number
  organizationId: number
  alcoholDeviationId: number | null
  points: number
  violationType: AlcoholDeviationType
  description: string | null
  createdAt: string
}

export interface PenaltyPointSummary {
  organizationId: number
  totalPoints: number
  entries: PenaltyPoint[]
}

export interface CreatePenaltyPointRequest {
  violationType: AlcoholDeviationType
  description?: string
}

// ── Combined deviation for unified list ─────────────────────
export type CombinedDeviation =
  | { _type: 'food'; data: FoodDeviation }
  | { _type: 'alcohol'; data: AlcoholDeviation }

// ── Penalty point helpers ───────────────────────────────────
export const PENALTY_POINTS_MAP: Record<AlcoholDeviationType, number> = {
  SKJENKING_MINDREAARIGE: 8,
  BRUDD_BISTANDSPLIKT: 8,
  UFORSVARLIG_DRIFT: 8,
  HINDRING_KONTROLL: 8,
  SKJENKING_APENBART_BERUSET: 4,
  BRUDD_SJENKETIDER: 4,
  BRENNEVIN_18_19: 4,
  BERUSET_PERSON_I_LOKALET: 2,
  MANGLER_IK_SYSTEM: 2,
  MANGLER_STYRER_STEDFORTREDER: 2,
  NARKOTIKA: 2,
  ALKOHOLFRI_ALTERNATIV_MANGLER: 1,
  MEDBRAKT_ALKOHOL: 1,
  REKLAMEBRUDD: 1,
  VILKAARSBRUDD: 1,
}

export type { OrganizationMember } from './member'
