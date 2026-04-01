<script setup lang="ts">
import axios from 'axios'
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowLeft,
  Calendar,
  Clock,
  AlertTriangle,
  CircleDot,
  CheckCircle2,
  Pencil,
  Trash2,
} from 'lucide-vue-next'
import { toast } from 'vue-sonner'
import AppLayout from '@/components/layout/AppLayout.vue'
import Button from '@/components/ui/button/Button.vue'
import { Separator } from '@/components/ui/separator'
import { SidebarTrigger } from '@/components/ui/sidebar'
import AlertDialog from '@/components/ui/alert-dialog/AlertDialog.vue'
import AlertDialogAction from '@/components/ui/alert-dialog/AlertDialogAction.vue'
import AlertDialogCancel from '@/components/ui/alert-dialog/AlertDialogCancel.vue'
import AlertDialogContent from '@/components/ui/alert-dialog/AlertDialogContent.vue'
import AlertDialogDescription from '@/components/ui/alert-dialog/AlertDialogDescription.vue'
import AlertDialogFooter from '@/components/ui/alert-dialog/AlertDialogFooter.vue'
import AlertDialogHeader from '@/components/ui/alert-dialog/AlertDialogHeader.vue'
import AlertDialogTitle from '@/components/ui/alert-dialog/AlertDialogTitle.vue'
import DeviationFormDialog from '@/components/deviations/DeviationFormDialog.vue'
import { useAuthStore } from '@/stores/auth'
import {
  useDeviationsQuery,
  useDeleteDeviationMutation,
  useOrganizationMembersQuery,
  useUpdateDeviationMutation,
  useUpdateDeviationStatusMutation,
} from '@/composables/useDeviations'
import type {
  DeviationModule,
  DeviationSeverity,
  DeviationStatus,
  UpdateDeviationRequest,
} from '@/types/deviation'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const deviationsQuery = useDeviationsQuery()
const updateDeviation = useUpdateDeviationMutation()
const updateDeviationStatus = useUpdateDeviationStatusMutation()
const deleteDeviation = useDeleteDeviationMutation()

const editDialogOpen = ref(false)
const deleteDialogOpen = ref(false)
const canManage = computed(() => auth.role === 'ADMIN' || auth.role === 'MANAGER')
const membersQuery = useOrganizationMembersQuery(canManage)

const deviationId = computed(() => Number(route.params.id))
const deviation = computed(() =>
  (deviationsQuery.data.value ?? []).find((d) => d.id === deviationId.value) ?? null,
)

const assigneeOptions = computed(() => {
  if (canManage.value) {
    return (membersQuery.data.value ?? []).map((member) => ({
      userId: member.userId,
      label: `${member.userFullName} (${member.role})`,
    }))
  }
  if (!auth.user) return []
  return [{ userId: auth.user.id, label: `${auth.user.fullName} (Deg)` }]
})

const moduleLabel: Record<DeviationModule, string> = {
  IK_MAT: 'IK-Mat',
  IK_ALKOHOL: 'IK-Alkohol',
}

const statusLabel: Record<DeviationStatus, string> = {
  OPEN: 'Åpen',
  IN_PROGRESS: 'Under behandling',
  RESOLVED: 'Løst',
  CLOSED: 'Løst',
}

const severityLabel: Record<DeviationSeverity, string> = {
  LOW: 'Lav',
  MEDIUM: 'Middels',
  HIGH: 'Høy',
  CRITICAL: 'Kritisk',
}

function normalizedStatus(status: DeviationStatus): DeviationStatus {
  return status === 'RESOLVED' ? 'CLOSED' : status
}

function formatDate(value: string | null): string {
  if (!value) return '-'
  return new Date(value).toLocaleString('nb-NO', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function getInitials(name: string | null): string {
  if (!name) return '?'
  const parts = name.split(' ').filter(Boolean)
  if (parts.length >= 2) {
    return ((parts[0]?.[0] ?? '') + (parts[parts.length - 1]?.[0] ?? '')).toUpperCase()
  }
  return parts[0]?.[0]?.toUpperCase() ?? '?'
}

async function onStatusClick(status: DeviationStatus) {
  if (!deviation.value) return
  try {
    await updateDeviationStatus.mutateAsync({
      id: deviation.value.id,
      payload: { status },
    })
    toast.success('Status oppdatert')
  } catch (error) {
    handleMutationError(error, 'Kunne ikke oppdatere status')
  }
}

async function handleUpdate(payload: { id: number; data: UpdateDeviationRequest }) {
  try {
    await updateDeviation.mutateAsync({ id: payload.id, payload: payload.data })
    editDialogOpen.value = false
    toast.success('Avvik oppdatert')
  } catch (error) {
    handleMutationError(error, 'Kunne ikke oppdatere avvik')
  }
}

async function handleDelete() {
  if (!deviation.value) return
  try {
    await deleteDeviation.mutateAsync(deviation.value.id)
    toast.success('Avvik slettet')
    router.push('/avvik')
  } catch (error) {
    handleMutationError(error, 'Kunne ikke slette avvik')
  }
}

function handleMutationError(error: unknown, fallbackMessage: string) {
  if (axios.isAxiosError(error)) {
    const message = error.response?.data?.error?.message
    if (typeof message === 'string' && message.trim().length > 0) {
      toast.error(message)
      return
    }
  }
  toast.error(fallbackMessage)
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
      <button class="back-button" @click="router.push('/avvik')">
        <ArrowLeft :size="18" />
        <span>Tilbake til oversikt</span>
      </button>

      <div v-if="deviationsQuery.isLoading.value" class="state-line">Laster...</div>

      <div v-else-if="!deviation" class="empty-state">
        <div class="empty-state-bg" />
        <div class="empty-state-inner">
          <div class="empty-state-icon">
            <AlertTriangle :stroke-width="1.5" />
          </div>
          <div class="empty-state-text">
            <h3>Avvik ikke funnet</h3>
            <p>Avviket du leter etter finnes ikke eller har blitt slettet.</p>
          </div>
          <Button @click="router.push('/avvik')">Tilbake til oversikt</Button>
        </div>
      </div>

      <template v-else>
        <div class="detail-layout">
          <div class="detail-main">
            <!-- Title -->
            <h1>{{ deviation.title }}</h1>

            <!-- Details card (moves under title on mobile) -->
            <div class="details-card">
              <div class="details-meta">
                <span class="meta-item">{{ severityLabel[deviation.severity] }}</span>
                <span class="meta-dot" />
                <span class="meta-item">{{ moduleLabel[deviation.module] }}</span>
                <span class="meta-dot" />
                <span class="meta-item">{{ statusLabel[deviation.status] }}</span>
              </div>

              <div class="details-people">
                <div class="person-row">
                  <div class="avatar">{{ getInitials(deviation.reportedByUserName) }}</div>
                  <div class="person-info">
                    <span class="person-label">Rapportert av</span>
                    <span class="person-name">{{ deviation.reportedByUserName }}</span>
                  </div>
                </div>
                <div class="person-row">
                  <div class="avatar" :class="{ 'avatar--muted': !deviation.assignedToUserName }">
                    {{ getInitials(deviation.assignedToUserName) }}
                  </div>
                  <div class="person-info">
                    <span class="person-label">Tildelt ansvar</span>
                    <span class="person-name">{{ deviation.assignedToUserName ?? 'Ikke tildelt' }}</span>
                  </div>
                </div>
              </div>

              <div class="details-dates">
                <div class="date-row">
                  <Calendar :size="15" class="date-icon" />
                  <span class="date-label">Rapportert</span>
                  <span class="date-value">{{ formatDate(deviation.reportedAt) }}</span>
                </div>
                <div class="date-row">
                  <Clock :size="15" class="date-icon" />
                  <span class="date-label">Oppdatert</span>
                  <span class="date-value">{{ formatDate(deviation.updatedAt) }}</span>
                </div>
                <div v-if="deviation.resolvedAt" class="date-row">
                  <CheckCircle2 :size="15" class="date-icon date-icon--green" />
                  <span class="date-label">Løst</span>
                  <span class="date-value">{{ formatDate(deviation.resolvedAt) }}</span>
                </div>
                <div v-if="deviation.closedAt" class="date-row">
                  <CheckCircle2 :size="15" class="date-icon date-icon--green" />
                  <span class="date-label">Lukket</span>
                  <span class="date-value">{{ formatDate(deviation.closedAt) }}</span>
                </div>
              </div>
            </div>

            <!-- Status buttons -->
            <section v-if="canManage" class="status-section">
              <h2>Status</h2>
              <div class="status-buttons">
                <button
                  type="button"
                  class="status-btn status-btn--open"
                  :class="{ 'status-btn--active': normalizedStatus(deviation.status) === 'OPEN' }"
                  @click="onStatusClick('OPEN')"
                >
                  <CircleDot :size="16" />
                  Åpen
                </button>
                <button
                  type="button"
                  class="status-btn status-btn--in-progress"
                  :class="{ 'status-btn--active': normalizedStatus(deviation.status) === 'IN_PROGRESS' }"
                  @click="onStatusClick('IN_PROGRESS')"
                >
                  <Clock :size="16" />
                  Under behandling
                </button>
                <button
                  type="button"
                  class="status-btn status-btn--resolved"
                  :class="{ 'status-btn--active': normalizedStatus(deviation.status) === 'CLOSED' }"
                  @click="onStatusClick('CLOSED')"
                >
                  <CheckCircle2 :size="16" />
                  Løst
                </button>
              </div>
            </section>

            <!-- Description -->
            <section class="content-section">
              <h2>Beskrivelse</h2>
              <p class="detail-body">{{ deviation.description }}</p>
            </section>

            <!-- Immediate action -->
            <section v-if="deviation.immediateAction" class="content-section">
              <h2>Umiddelbar korrigerende handling</h2>
              <p class="detail-body">{{ deviation.immediateAction }}</p>
            </section>

            <!-- Actions -->
            <div v-if="canManage" class="detail-actions">
              <Button variant="secondary" @click="editDialogOpen = true">
                <Pencil />
                Rediger avvik
              </Button>

              <Button variant="destructive" class="delete-btn" @click="deleteDialogOpen = true">
                <Trash2 />
                Slett avvik
              </Button>

              <AlertDialog v-model:open="deleteDialogOpen">
                <AlertDialogContent>
                  <AlertDialogHeader>
                    <AlertDialogTitle>Slette avvik?</AlertDialogTitle>
                    <AlertDialogDescription>
                      Avviket blir permanent slettet og kan ikke gjenopprettes.
                    </AlertDialogDescription>
                  </AlertDialogHeader>
                  <AlertDialogFooter>
                    <AlertDialogCancel>Avbryt</AlertDialogCancel>
                    <AlertDialogAction variant="destructive" @click="handleDelete">
                      Slett
                    </AlertDialogAction>
                  </AlertDialogFooter>
                </AlertDialogContent>
              </AlertDialog>
            </div>
          </div>
        </div>
      </template>
    </div>

    <DeviationFormDialog
      v-model:open="editDialogOpen"
      mode="edit"
      :initial-deviation="deviation"
      :submitting="updateDeviation.isPending.value"
      :assignees="assigneeOptions"
      @update="handleUpdate"
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
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: none;
  border: none;
  cursor: pointer;
  color: hsl(var(--muted-foreground));
  font-size: 0.9rem;
  padding: 4px 0;
  transition: color 150ms ease;
}
.back-button:hover { color: hsl(var(--foreground)); }

.detail-layout {
  max-width: 720px;
}

.detail-main {
  display: flex;
  flex-direction: column;
  gap: 1.75rem;
}

h1 {
  margin: 0;
  font-size: 2rem;
  letter-spacing: -0.02em;
  line-height: 1.2;
}

/* Details card */
.details-card {
  border: 1px solid hsl(var(--border));
  border-radius: var(--radius-lg);
  background: hsl(var(--card));
  padding: 1.25rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.details-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9rem;
  color: hsl(var(--muted-foreground));
}

.meta-dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: hsl(var(--muted-foreground) / 0.4);
  flex-shrink: 0;
}

.meta-item {
  font-weight: 500;
}

.details-people {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.person-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  border-radius: 0.5rem;
  background-color: hsl(var(--primary, 245 43% 52%));
  color: hsl(var(--primary-foreground, 0 0% 100%));
  font-weight: 600;
  font-size: 0.75rem;
  flex-shrink: 0;
}

.avatar--muted {
  background-color: hsl(var(--muted-foreground) / 0.3);
  color: hsl(var(--muted-foreground));
}

.person-info {
  display: flex;
  flex-direction: column;
  gap: 1px;
  min-width: 0;
}

.person-label {
  font-size: 0.75rem;
  color: hsl(var(--muted-foreground));
}

.person-name {
  font-size: 0.9rem;
  font-weight: 500;
  color: hsl(var(--foreground));
}

.details-dates {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.date-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.85rem;
}

.date-icon {
  color: hsl(var(--muted-foreground));
  flex-shrink: 0;
}

.date-icon--green { color: #3c8f2c; }

.date-label {
  color: hsl(var(--muted-foreground));
  min-width: 5.5rem;
}

.date-value {
  color: hsl(var(--foreground));
  font-weight: 500;
}

/* Status section */
.status-section h2,
.content-section h2 {
  margin: 0 0 0.75rem;
  font-size: 1.1rem;
  font-weight: 600;
  color: hsl(var(--foreground));
}

.status-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.status-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: 1.5px solid transparent;
  border-radius: var(--radius-pill);
  padding: 8px 16px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 150ms ease;
}

.status-btn--open {
  background: #fdf5f5;
  border-color: #e7bdbd;
  color: #a62929;
}
.status-btn--open.status-btn--active {
  background: #f5d5d5;
  border-color: #a62929;
  font-weight: 700;
}

.status-btn--in-progress {
  background: #fdf9f0;
  border-color: #eccf9f;
  color: #946013;
}
.status-btn--in-progress.status-btn--active {
  background: #f5e8c8;
  border-color: #946013;
  font-weight: 700;
}

.status-btn--resolved {
  background: #f3faf2;
  border-color: #b8d8b1;
  color: #3c8f2c;
}
.status-btn--resolved.status-btn--active {
  background: #d8f0d4;
  border-color: #3c8f2c;
  font-weight: 700;
}

/* Content */
.detail-body {
  margin: 0;
  font-size: 1rem;
  line-height: 1.6;
  color: hsl(var(--foreground));
  white-space: pre-wrap;
}

/* Actions */
.detail-actions {
  display: flex;
  gap: 10px;
}

.delete-btn {
  background-color: #fde8e8;
  color: #c62828;
  border: none;
  box-shadow: none;
}
.delete-btn:hover { background-color: #fad4d4; }
.delete-btn:active { background-color: #f5c2c2; }

/* States */
.state-line {
  border-radius: var(--radius-md);
  border: 1px solid hsl(var(--border));
  background: hsl(var(--card));
  padding: 12px;
  color: var(--text-secondary);
}

.empty-state {
  position: relative;
  display: flex;
  min-height: 260px;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border-radius: 1rem;
  border: 2px dashed hsl(var(--muted-foreground) / 0.2);
  background: linear-gradient(to bottom right, hsl(var(--muted) / 0.4), hsl(var(--muted) / 0.2), hsl(var(--background)));
  padding: 2rem;
}

.empty-state-bg {
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse at center, hsl(var(--muted)) 0%, transparent 70%);
  opacity: 0.5;
}

.empty-state-inner {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  text-align: center;
}

.empty-state-icon {
  display: flex;
  height: 5rem;
  width: 5rem;
  align-items: center;
  justify-content: center;
  border-radius: 1rem;
  background-color: hsl(var(--primary) / 0.1);
  box-shadow: 0 0 0 4px hsl(var(--primary) / 0.05);
}

.empty-state-icon :deep(svg) {
  width: 2.5rem;
  height: 2.5rem;
  color: hsl(var(--primary) / 0.7);
}

.empty-state-text h3 {
  font-size: 1.125rem;
  font-weight: 600;
}

.empty-state-text p {
  max-width: 24rem;
  font-size: 0.875rem;
  color: hsl(var(--muted-foreground));
  margin-top: 0.25rem;
}

@media (max-width: 600px) {
  .details-people {
    grid-template-columns: 1fr;
  }
}
</style>
