<script setup lang="ts">
import axios from 'axios'
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Calendar, Clock, AlertTriangle, Pencil, Trash2 } from 'lucide-vue-next'
import { toast } from 'vue-sonner'
import AppLayout from '@/components/layout/AppLayout.vue'
import Button from '@/components/ui/button/Button.vue'
import { Separator } from '@/components/ui/separator'
import { SidebarTrigger } from '@/components/ui/sidebar'
import Badge from '@/components/ui/badge/Badge.vue'
import AlertDialog from '@/components/ui/alert-dialog/AlertDialog.vue'
import AlertDialogAction from '@/components/ui/alert-dialog/AlertDialogAction.vue'
import AlertDialogCancel from '@/components/ui/alert-dialog/AlertDialogCancel.vue'
import AlertDialogContent from '@/components/ui/alert-dialog/AlertDialogContent.vue'
import AlertDialogDescription from '@/components/ui/alert-dialog/AlertDialogDescription.vue'
import AlertDialogFooter from '@/components/ui/alert-dialog/AlertDialogFooter.vue'
import AlertDialogHeader from '@/components/ui/alert-dialog/AlertDialogHeader.vue'
import AlertDialogTitle from '@/components/ui/alert-dialog/AlertDialogTitle.vue'
import FoodDeviationFormDialog from '@/components/deviations/FoodDeviationFormDialog.vue'
import AlcoholDeviationFormDialog from '@/components/deviations/AlcoholDeviationFormDialog.vue'
import { useAuthStore } from '@/stores/auth'
import { useMembersQuery } from '@/composables/useMembers'
import {
  useFoodDeviationsQuery,
  useUpdateFoodDeviationMutation,
  useDeleteFoodDeviationMutation,
} from '@/composables/useFoodDeviations'
import {
  useAlcoholDeviationsQuery,
  useUpdateAlcoholDeviationMutation,
  useDeleteAlcoholDeviationMutation,
} from '@/composables/useAlcoholDeviations'
import { usePenaltyPointsQuery } from '@/composables/usePenaltyPoints'
import type {
  FoodDeviation,
  AlcoholDeviation,
  FoodDeviationType,
  FoodDeviationStatus,
  AlcoholDeviationStatus,
  AlcoholReportSource,
  AlcoholDeviationType,
  AlcoholCausalAnalysis,
  DeviationSeverity,
  UpdateFoodDeviationRequest,
  UpdateAlcoholDeviationRequest,
} from '@/types/deviation'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const canManage = computed(() => auth.role === 'ADMIN' || auth.role === 'MANAGER')
const membersQuery = useMembersQuery(canManage)

const deviationModule = computed(() => route.params.module as 'mat' | 'alkohol')
const deviationId = computed(() => Number(route.params.id))

const foodQuery = useFoodDeviationsQuery()
const alcoholQuery = useAlcoholDeviationsQuery()
const penaltyQuery = usePenaltyPointsQuery()
const updateFood = useUpdateFoodDeviationMutation()
const deleteFood = useDeleteFoodDeviationMutation()
const updateAlcohol = useUpdateAlcoholDeviationMutation()
const deleteAlcohol = useDeleteAlcoholDeviationMutation()

const editFoodOpen = ref(false)
const editAlcoholOpen = ref(false)
const deleteDialogOpen = ref(false)

const isLoading = computed(() =>
  deviationModule.value === 'mat' ? foodQuery.isLoading.value : alcoholQuery.isLoading.value,
)

const foodDeviation = computed<FoodDeviation | null>(() =>
  deviationModule.value === 'mat'
    ? (foodQuery.data.value ?? []).find((d) => d.id === deviationId.value) ?? null
    : null,
)

const alcoholDeviation = computed<AlcoholDeviation | null>(() =>
  deviationModule.value === 'alkohol'
    ? (alcoholQuery.data.value ?? []).find((d) => d.id === deviationId.value) ?? null
    : null,
)

const notFound = computed(() =>
  !isLoading.value && !foodDeviation.value && !alcoholDeviation.value,
)

const memberOptions = computed(() => {
  if (canManage.value) {
    return (membersQuery.data.value ?? []).map((m) => ({
      userId: m.userId,
      label: `${m.userFullName} (${m.role})`,
    }))
  }
  if (!auth.user) return []
  return [{ userId: auth.user.id, label: `${auth.user.fullName} (Deg)` }]
})

const foodTypeLabel: Record<FoodDeviationType, string> = {
  TEMPERATUR: 'Temperatur', RENHOLD: 'Renhold', PERSONLIG_HYGIENE: 'Personlig hygiene',
  ALLERGEN: 'Allergen', SKADEDYR: 'Skadedyr', MOTTAKSKONTROLL: 'Mottakskontroll', ANNET: 'Annet',
}

const severityLabel: Record<DeviationSeverity, string> = {
  LOW: 'Lav', MEDIUM: 'Middels', HIGH: 'Kritisk', CRITICAL: 'Kritisk',
}

const foodStatusLabel: Record<FoodDeviationStatus, string> = {
  OPEN: 'Åpen', UNDER_TREATMENT: 'Under behandling', CLOSED: 'Lukket',
}

const alcoholStatusLabel: Record<AlcoholDeviationStatus, string> = {
  OPEN: 'Åpen', UNDER_TREATMENT: 'Under behandling', CLOSED: 'Lukket',
}

const sourceLabel: Record<AlcoholReportSource, string> = {
  EGENRAPPORT: 'Egenrapport', SJENKEKONTROLL: 'Skjenkekontroll', POLITIRAPPORT: 'Politirapport',
}

const alcoholTypeLabel: Record<AlcoholDeviationType, string> = {
  SKJENKING_MINDREAARIGE: 'Skjenking til mindreårige', BRUDD_BISTANDSPLIKT: 'Brudd på bistandsplikt',
  UFORSVARLIG_DRIFT: 'Uforsvarlig drift', HINDRING_KONTROLL: 'Hindring av kontroll',
  SKJENKING_APENBART_BERUSET: 'Skjenking til åpenbart beruset', BRUDD_SJENKETIDER: 'Brudd på skjenketider',
  BRENNEVIN_18_19: 'Brennevin til 18-19-åringer', BERUSET_PERSON_I_LOKALET: 'Beruset person i lokalet',
  MANGLER_IK_SYSTEM: 'Mangler IK-system', MANGLER_STYRER_STEDFORTREDER: 'Mangler styrer/stedfortreder',
  NARKOTIKA: 'Narkotika', ALKOHOLFRI_ALTERNATIV_MANGLER: 'Alkoholfri alternativ mangler',
  MEDBRAKT_ALKOHOL: 'Medbrakt alkohol', REKLAMEBRUDD: 'Reklamebrudd', VILKAARSBRUDD: 'Vilkårsbrudd',
}

const causalLabel: Record<AlcoholCausalAnalysis, string> = {
  MANGLENDE_OPPLAERING: 'Manglende opplæring', RUTINE_IKKE_FULGT: 'Rutine ikke fulgt',
  RUTINE_MANGLER: 'Rutine mangler', HOYT_TRYKK_STRESS: 'Høyt trykk / stress',
  UNDERBEMANNING: 'Underbemanning', KOMMUNIKASJON: 'Kommunikasjon', ANNET: 'Annet',
}

function formatDate(value: string | null): string {
  if (!value) return '-'
  return new Date(value).toLocaleString('nb-NO', {
    day: 'numeric', month: 'long', year: 'numeric', hour: '2-digit', minute: '2-digit',
  })
}

function getInitials(name: string | null): string {
  if (!name) return '?'
  const parts = name.split(' ').filter(Boolean)
  if (parts.length >= 2) return ((parts[0]?.[0] ?? '') + (parts[parts.length - 1]?.[0] ?? '')).toUpperCase()
  return parts[0]?.[0]?.toUpperCase() ?? '?'
}

async function handleUpdateFood(p: { id: number; data: UpdateFoodDeviationRequest }) {
  try {
    await updateFood.mutateAsync({ id: p.id, payload: p.data })
    editFoodOpen.value = false
    toast.success('Avvik oppdatert')
  } catch (err) { handleError(err, 'Kunne ikke oppdatere avvik') }
}

async function handleUpdateAlcohol(p: { id: number; data: UpdateAlcoholDeviationRequest }) {
  try {
    await updateAlcohol.mutateAsync({ id: p.id, payload: p.data })
    editAlcoholOpen.value = false
    toast.success('Avvik oppdatert')
  } catch (err) { handleError(err, 'Kunne ikke oppdatere avvik') }
}

async function handleDelete() {
  try {
    if (foodDeviation.value) await deleteFood.mutateAsync(foodDeviation.value.id)
    else if (alcoholDeviation.value) await deleteAlcohol.mutateAsync(alcoholDeviation.value.id)
    toast.success('Avvik slettet')
    router.push('/avvik')
  } catch (err) { handleError(err, 'Kunne ikke slette avvik') }
}

function handleError(error: unknown, fallback: string) {
  if (axios.isAxiosError(error)) {
    const msg = error.response?.data?.error?.message
    if (typeof msg === 'string' && msg.trim().length > 0) { toast.error(msg); return }
  }
  toast.error(fallback)
}
</script>

<template>
  <AppLayout>
    <header class="page-header">
      <div class="page-header-inner">
        <SidebarTrigger />
        <Separator orientation="vertical" class="header-separator" />
        <span class="page-title">Avvik</span>
      </div>
    </header>

    <div class="page-content">
      <Button variant="ghost" class="back-button" @click="router.push('/avvik')">
        <ArrowLeft :size="18" /><span>Tilbake til oversikt</span>
      </Button>

      <div v-if="isLoading" class="state-line">Laster...</div>

      <div v-else-if="notFound" class="empty-state">
        <div class="empty-state-bg" />
        <div class="empty-state-inner">
          <div class="empty-state-icon"><AlertTriangle :stroke-width="1.5" /></div>
          <div class="empty-state-text">
            <h3>Avvik ikke funnet</h3>
            <p>Avviket du leter etter finnes ikke eller har blitt slettet.</p>
          </div>
          <Button @click="router.push('/avvik')">Tilbake til oversikt</Button>
        </div>
      </div>

      <!-- Food detail -->
      <template v-else-if="foodDeviation">
        <div class="detail-layout">
          <div class="detail-main">
            <div class="tag-row">
              <Badge tone="neutral">{{ foodTypeLabel[foodDeviation.deviationType] }}</Badge>
              <Badge :tone="foodDeviation.severity === 'HIGH' || foodDeviation.severity === 'CRITICAL' ? 'danger' : foodDeviation.severity === 'MEDIUM' ? 'warning' : 'ok'">
                {{ severityLabel[foodDeviation.severity] }}
              </Badge>
              <Badge tone="brand">IK-Mat</Badge>
              <Badge :tone="foodDeviation.status === 'OPEN' ? 'danger' : foodDeviation.status === 'UNDER_TREATMENT' ? 'warning' : 'ok'">
                {{ foodStatusLabel[foodDeviation.status] }}
              </Badge>
            </div>

            <section class="content-section">
              <h2>Beskrivelse</h2>
              <p class="detail-body">{{ foodDeviation.description }}</p>
            </section>

            <section v-if="foodDeviation.immediateAction" class="content-section">
              <h2>Umiddelbar handling</h2>
              <p class="detail-body">{{ foodDeviation.immediateAction }}</p>
              <div v-if="foodDeviation.immediateActionByUserName" class="meta-inline">
                Utført av: {{ foodDeviation.immediateActionByUserName }}
                <span v-if="foodDeviation.immediateActionAt"> · {{ formatDate(foodDeviation.immediateActionAt) }}</span>
              </div>
            </section>

            <section v-if="foodDeviation.cause" class="content-section">
              <h2>Årsak</h2>
              <p class="detail-body">{{ foodDeviation.cause }}</p>
            </section>

            <section v-if="foodDeviation.preventiveMeasures" class="content-section">
              <h2>Forebyggende tiltak</h2>
              <p class="detail-body">{{ foodDeviation.preventiveMeasures }}</p>
              <div v-if="foodDeviation.preventiveResponsibleUserName" class="meta-inline">
                Ansvarlig: {{ foodDeviation.preventiveResponsibleUserName }}
                <span v-if="foodDeviation.preventiveDeadline"> · Frist: {{ formatDate(foodDeviation.preventiveDeadline) }}</span>
              </div>
            </section>

            <div class="details-card">
              <div class="details-people">
                <div class="person-row">
                  <div class="avatar">{{ getInitials(foodDeviation.reportedByUserName) }}</div>
                  <div class="person-info">
                    <span class="person-label">Rapportert av</span>
                    <span class="person-name">{{ foodDeviation.reportedByUserName }}</span>
                  </div>
                </div>
              </div>
              <div class="details-dates">
                <div class="date-row"><Calendar :size="15" class="date-icon" /><span class="date-label">Rapportert</span><span class="date-value">{{ formatDate(foodDeviation.reportedAt) }}</span></div>
                <div class="date-row"><Clock :size="15" class="date-icon" /><span class="date-label">Oppdatert</span><span class="date-value">{{ formatDate(foodDeviation.updatedAt) }}</span></div>
              </div>
            </div>

            <div v-if="canManage" class="detail-actions">
              <Button variant="secondary" @click="editFoodOpen = true"><Pencil /> Rediger</Button>
              <Button variant="destructive" class="delete-btn" @click="deleteDialogOpen = true"><Trash2 /> Slett</Button>
            </div>
          </div>
        </div>
      </template>

      <!-- Alcohol detail -->
      <template v-else-if="alcoholDeviation">
        <div class="detail-layout">
          <div class="detail-main">
            <div class="tag-row">
              <Badge :tone="alcoholDeviation.reportSource === 'EGENRAPPORT' ? 'neutral' : alcoholDeviation.reportSource === 'SJENKEKONTROLL' ? 'warning' : 'danger'">
                {{ sourceLabel[alcoholDeviation.reportSource] }}
              </Badge>
              <Badge tone="neutral">{{ alcoholTypeLabel[alcoholDeviation.deviationType] }}</Badge>
              <Badge tone="brand">IK-Alkohol</Badge>
              <Badge :tone="alcoholDeviation.status === 'OPEN' ? 'danger' : alcoholDeviation.status === 'UNDER_TREATMENT' ? 'warning' : 'ok'">
                {{ alcoholStatusLabel[alcoholDeviation.status] }}
              </Badge>
            </div>

            <section class="content-section">
              <h2>Beskrivelse</h2>
              <p class="detail-body">{{ alcoholDeviation.description }}</p>
            </section>

            <section v-if="alcoholDeviation.immediateAction" class="content-section">
              <h2>Umiddelbar handling</h2>
              <p class="detail-body">{{ alcoholDeviation.immediateAction }}</p>
            </section>

            <section v-if="alcoholDeviation.causalAnalysis" class="content-section">
              <h2>Årsaksanalyse</h2>
              <Badge tone="neutral">{{ causalLabel[alcoholDeviation.causalAnalysis] }}</Badge>
              <p v-if="alcoholDeviation.causalExplanation" class="detail-body" style="margin-top: 8px;">{{ alcoholDeviation.causalExplanation }}</p>
            </section>

            <section v-if="alcoholDeviation.preventiveMeasures" class="content-section">
              <h2>Forebyggende tiltak</h2>
              <p class="detail-body">{{ alcoholDeviation.preventiveMeasures }}</p>
              <div v-if="alcoholDeviation.preventiveResponsibleUserName" class="meta-inline">
                Ansvarlig: {{ alcoholDeviation.preventiveResponsibleUserName }}
                <span v-if="alcoholDeviation.preventiveDeadline"> · Frist: {{ formatDate(alcoholDeviation.preventiveDeadline) }}</span>
              </div>
            </section>

            <div class="details-card">
              <div class="details-people">
                <div class="person-row">
                  <div class="avatar">{{ getInitials(alcoholDeviation.reportedByUserName) }}</div>
                  <div class="person-info">
                    <span class="person-label">Rapportert av</span>
                    <span class="person-name">{{ alcoholDeviation.reportedByUserName }}</span>
                  </div>
                </div>
              </div>
              <div class="details-dates">
                <div class="date-row"><Calendar :size="15" class="date-icon" /><span class="date-label">Rapportert</span><span class="date-value">{{ formatDate(alcoholDeviation.reportedAt) }}</span></div>
                <div class="date-row"><Clock :size="15" class="date-icon" /><span class="date-label">Oppdatert</span><span class="date-value">{{ formatDate(alcoholDeviation.updatedAt) }}</span></div>
              </div>
            </div>

            <div v-if="canManage" class="detail-actions">
              <Button variant="secondary" @click="editAlcoholOpen = true"><Pencil /> Rediger</Button>
              <Button variant="destructive" class="delete-btn" @click="deleteDialogOpen = true"><Trash2 /> Slett</Button>
            </div>
          </div>
        </div>
      </template>
    </div>

    <!-- Delete dialog -->
    <AlertDialog v-model:open="deleteDialogOpen">
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Slette avvik?</AlertDialogTitle>
          <AlertDialogDescription>Avviket blir permanent slettet og kan ikke gjenopprettes.</AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>Avbryt</AlertDialogCancel>
          <AlertDialogAction variant="destructive" @click="handleDelete">Slett</AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>

    <!-- Edit dialogs -->
    <FoodDeviationFormDialog
      v-model:open="editFoodOpen"
      mode="edit"
      :initial="foodDeviation"
      :submitting="updateFood.isPending.value"
      :members="memberOptions"
      @update="handleUpdateFood"
    />
    <AlcoholDeviationFormDialog
      v-model:open="editAlcoholOpen"
      mode="edit"
      :initial="alcoholDeviation"
      :submitting="updateAlcohol.isPending.value"
      :members="memberOptions"
      :penalty-summary="penaltyQuery.data.value ?? null"
      @update="handleUpdateAlcohol"
    />
  </AppLayout>
</template>

<style scoped>
.page-header { display: flex; height: 4rem; flex-shrink: 0; align-items: center; }
.page-header-inner { display: flex; align-items: center; gap: 0.5rem; padding: 0 1rem; }
.header-separator { height: 1rem !important; width: 1px !important; margin-right: 0.5rem; }
.page-title { font-weight: 500; color: hsl(var(--sidebar-primary, 245 43% 52%)); }
.page-content { display: flex; flex: 1; flex-direction: column; gap: 1.25rem; padding: 0 1rem 2rem; }

.back-button {
  display: inline-flex; align-items: center; gap: 6px;
  width: fit-content;
  background: none !important; border: none; cursor: pointer;
  color: hsl(var(--muted-foreground)); font-size: 0.85rem; padding: 4px 0;
  transition: color 150ms ease;
}
.back-button:hover {
  background: none !important;
  color: hsl(var(--foreground));
}

.detail-layout { max-width: 720px; }
.detail-main { display: flex; flex-direction: column; gap: 1.75rem; }

.tag-row { display: flex; flex-wrap: wrap; gap: 6px; }

.content-section h2 { margin: 0 0 0.75rem; font-size: 1.1rem; font-weight: 600; }
.detail-body { margin: 0; font-size: 1rem; line-height: 1.6; white-space: pre-wrap; }
.meta-inline { font-size: 0.85rem; color: hsl(var(--muted-foreground)); margin-top: 6px; }

.details-card {
  border: 1px solid hsl(var(--border)); border-radius: var(--radius-lg);
  background: hsl(var(--card)); padding: 1.25rem; display: flex; flex-direction: column; gap: 1rem;
}
.details-people { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 10px; }
.person-row { display: flex; align-items: center; gap: 10px; }
.avatar {
  display: flex; align-items: center; justify-content: center;
  width: 2rem; height: 2rem; border-radius: 0.5rem;
  background-color: hsl(var(--primary)); color: hsl(var(--primary-foreground));
  font-weight: 600; font-size: 0.75rem; flex-shrink: 0;
}
.person-info { display: flex; flex-direction: column; gap: 1px; }
.person-label { font-size: 0.75rem; color: hsl(var(--muted-foreground)); }
.person-name { font-size: 0.9rem; font-weight: 500; }
.details-dates { display: flex; flex-direction: column; gap: 6px; }
.date-row { display: flex; align-items: center; gap: 8px; font-size: 0.85rem; }
.date-icon { color: hsl(var(--muted-foreground)); flex-shrink: 0; }
.date-label { color: hsl(var(--muted-foreground)); min-width: 5.5rem; }
.date-value { color: hsl(var(--foreground)); font-weight: 500; }

.detail-actions { display: flex; gap: 10px; }
.delete-btn { background-color: #fde8e8; color: #c62828; border: none; box-shadow: none; }
.delete-btn:hover { background-color: #fad4d4; }

.state-line {
  border-radius: var(--radius-md); border: 1px solid hsl(var(--border));
  background: hsl(var(--card)); padding: 12px; color: var(--text-secondary);
}

.empty-state {
  position: relative; display: flex; min-height: 260px;
  flex-direction: column; align-items: center; justify-content: center;
  overflow: hidden; border-radius: 1rem;
  border: 2px dashed hsl(var(--muted-foreground) / 0.2);
  background: linear-gradient(to bottom right, hsl(var(--muted) / 0.4), hsl(var(--muted) / 0.2), hsl(var(--background)));
  padding: 2rem;
}
.empty-state-bg { position: absolute; inset: 0; background: radial-gradient(ellipse at center, hsl(var(--muted)) 0%, transparent 70%); opacity: 0.5; }
.empty-state-inner { position: relative; display: flex; flex-direction: column; align-items: center; gap: 1rem; text-align: center; }
.empty-state-icon {
  display: flex; height: 5rem; width: 5rem; align-items: center; justify-content: center;
  border-radius: 1rem; background-color: hsl(var(--primary) / 0.1); box-shadow: 0 0 0 4px hsl(var(--primary) / 0.05);
}
.empty-state-icon :deep(svg) { width: 2.5rem; height: 2.5rem; color: hsl(var(--primary) / 0.7); }
.empty-state-text h3 { font-size: 1.125rem; font-weight: 600; }
.empty-state-text p { max-width: 24rem; font-size: 0.875rem; color: hsl(var(--muted-foreground)); margin-top: 0.25rem; }
</style>
