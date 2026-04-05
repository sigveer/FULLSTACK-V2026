<script setup lang="ts">
import { computed, ref } from 'vue'
import axios from 'axios'
import { MoreVertical, ArrowUpDown, Search, Plus, Pencil, Trash2, AlertTriangle } from 'lucide-vue-next'
import { toast } from 'vue-sonner'
import { Separator } from '@/components/ui/separator'
import { SidebarTrigger } from '@/components/ui/sidebar'
import Button from '@/components/ui/button/Button.vue'
import Checkbox from '@/components/ui/checkbox/Checkbox.vue'
import {
  Table,
  TableBody,
  TableCell,
  TableEmpty,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
} from '@/components/ui/dropdown-menu'
import AlertDialog from '@/components/ui/alert-dialog/AlertDialog.vue'
import AlertDialogAction from '@/components/ui/alert-dialog/AlertDialogAction.vue'
import AlertDialogCancel from '@/components/ui/alert-dialog/AlertDialogCancel.vue'
import AlertDialogContent from '@/components/ui/alert-dialog/AlertDialogContent.vue'
import AlertDialogDescription from '@/components/ui/alert-dialog/AlertDialogDescription.vue'
import AlertDialogFooter from '@/components/ui/alert-dialog/AlertDialogFooter.vue'
import AlertDialogHeader from '@/components/ui/alert-dialog/AlertDialogHeader.vue'
import AlertDialogTitle from '@/components/ui/alert-dialog/AlertDialogTitle.vue'
import StatusBadge from '@/components/training/StatusBadge.vue'
import OverviewCard from '@/components/common/OverviewCard.vue'
import RegisterTrainingModal from '@/components/training/RegisterTrainingModal.vue'
import EditTrainingModal from '@/components/training/EditTrainingModal.vue'
import {
  useTrainingLogsQuery,
  useDeleteTrainingLogMutation,
} from '@/composables/useTrainingLogs'
import type { TrainingLog, TrainingStatus } from '@/types/training'

const trainingLogsQuery = useTrainingLogsQuery()
const deleteTrainingLog = useDeleteTrainingLogMutation()

const trainingLogs = computed(() => trainingLogsQuery.data.value ?? [])

// ── Search ──
const search = ref('')

// ── Sorting ──
type SortField = 'employee' | 'title' | 'completed' | 'expires' | 'status'
type SortDir = 'asc' | 'desc'
const sortField = ref<SortField>('employee')
const sortDir = ref<SortDir>('asc')

function toggleSort(field: SortField) {
  if (sortField.value === field) {
    sortDir.value = sortDir.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortField.value = field
    sortDir.value = 'asc'
  }
}

const statusLabel: Record<TrainingStatus, string> = {
  COMPLETED: 'Gyldig',
  EXPIRES_SOON: 'Utløper snart',
  EXPIRED: 'Utgått',
  NOT_COMPLETED: 'Mangler',
}

const statusOrder: Record<TrainingStatus, number> = {
  NOT_COMPLETED: 0,
  EXPIRED: 1,
  EXPIRES_SOON: 2,
  COMPLETED: 3,
}

function formatDate(iso: string | null): string {
  if (!iso) return '—'
  const d = new Date(iso)
  return d.toLocaleDateString('nb-NO', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

const filteredAndSorted = computed(() => {
  const q = search.value.toLowerCase().trim()
  let list = trainingLogs.value.slice()

  if (q) {
    list = list.filter((t) =>
      [t.employeeUserName, t.title, statusLabel[t.status]].some((v) =>
        v.toLowerCase().includes(q),
      ),
    )
  }

  list.sort((a, b) => {
    let cmp = 0
    if (sortField.value === 'employee') {
      cmp = a.employeeUserName.localeCompare(b.employeeUserName, 'nb')
    } else if (sortField.value === 'title') {
      cmp = a.title.localeCompare(b.title, 'nb')
    } else if (sortField.value === 'completed') {
      cmp = (a.completedAt ?? '').localeCompare(b.completedAt ?? '')
    } else if (sortField.value === 'expires') {
      cmp = (a.expiresAt ?? '').localeCompare(b.expiresAt ?? '')
    } else if (sortField.value === 'status') {
      cmp = statusOrder[a.status] - statusOrder[b.status]
    }
    return sortDir.value === 'desc' ? -cmp : cmp
  })

  return list
})

// ── Checkbox selection ──
const selected = ref<Set<number>>(new Set())

function toggleSelect(id: number) {
  const s = new Set(selected.value)
  if (s.has(id)) s.delete(id)
  else s.add(id)
  selected.value = s
}

const allSelected = computed(() =>
  filteredAndSorted.value.length > 0 &&
  filteredAndSorted.value.every((r) => selected.value.has(r.id)),
)

function toggleSelectAll() {
  selected.value = allSelected.value
    ? new Set()
    : new Set(filteredAndSorted.value.map((r) => r.id))
}

// ── Stats ──
const stats = computed(() => {
  const list = trainingLogs.value
  return {
    total: list.length,
    completed: list.filter((t) => t.status === 'COMPLETED').length,
    expiringSoon: list.filter((t) => t.status === 'EXPIRES_SOON').length,
    expired: list.filter((t) => t.status === 'EXPIRED').length,
    notCompleted: list.filter((t) => t.status === 'NOT_COMPLETED').length,
  }
})

// ── Register modal ──
const showRegister = ref(false)

// ── Edit modal ──
const editModalOpen = ref(false)
const editingLog = ref<TrainingLog | null>(null)

function openEdit(log: TrainingLog) {
  editingLog.value = log
  editModalOpen.value = true
}

// ── Delete ──
const deleteDialogOpen = ref(false)
const deletingIds = ref<number[]>([])

function openDeleteSingle(log: TrainingLog) {
  deletingIds.value = [log.id]
  deleteDialogOpen.value = true
}

function openDeleteSelected() {
  deletingIds.value = [...selected.value]
  deleteDialogOpen.value = true
}

async function confirmDelete() {
  try {
    for (const id of deletingIds.value) {
      await deleteTrainingLog.mutateAsync(id)
    }
    toast.success(`Slettet ${deletingIds.value.length} opplæring${deletingIds.value.length > 1 ? 'er' : ''}`)
    selected.value = new Set()
  } catch (error) {
    handleMutationError(error, 'Kunne ikke slette opplæring')
  } finally {
    deleteDialogOpen.value = false
    deletingIds.value = []
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
  <header class="page-header">
    <div class="page-header-inner">
      <SidebarTrigger />
      <Separator orientation="vertical" class="header-separator" />
      <span class="page-title">Opplæring</span>
    </div>
  </header>

  <div class="page-content">
    <section class="header-row">
      <div>
        <h1>Opplæring og sertifiseringer</h1>
        <p>Oversikt over ansattes opplæringsstatus</p>
      </div>
      <div class="header-actions">
        <Button @click="showRegister = true">
          <Plus :size="16" />
          Registrer opplæring
        </Button>
      </div>
    </section>

    <!-- Stats cards -->
    <section class="cards-group">
      <OverviewCard label="Totalt opplærte" :value="stats.total" />
      <OverviewCard label="Fullført" :value="stats.completed" variant="resolved" />
      <OverviewCard label="Utløper snart" :value="stats.expiringSoon" variant="in-progress" />
      <OverviewCard label="Utgått" :value="stats.expired" variant="open" />
      <OverviewCard label="Mangler" :value="stats.notCompleted" variant="open" />
    </section>

    <!-- Table -->
    <section class="table-section">
      <div class="search-row">
        <div class="search-wrapper">
          <Search :size="16" class="search-icon" />
          <input v-model="search" class="search-input" placeholder="Søk etter ansatt, type eller status..." />
        </div>
        <Button
          v-if="selected.size > 0"
          variant="destructive-ghost"
          @click="openDeleteSelected"
        >
          <Trash2 :size="16" />
          Slett ({{ selected.size }})
        </Button>
      </div>

      <p v-if="trainingLogsQuery.isLoading.value" class="state-line">Laster opplæringer...</p>

      <div v-else-if="trainingLogsQuery.isError.value" class="empty-state">
        <div class="empty-state-bg" />
        <div class="empty-state-inner">
          <div class="empty-state-icon">
            <AlertTriangle :stroke-width="1.5" />
          </div>
          <div class="empty-state-text">
            <h3>Kunne ikke hente opplæringer</h3>
            <p>Noe gikk galt under lasting. Prøv igjen senere.</p>
          </div>
        </div>
      </div>

      <div v-else class="table-card">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead class="th-check">
                <Checkbox :checked="allSelected" @update:checked="toggleSelectAll" />
              </TableHead>
              <TableHead class="th-employee">
                <Button variant="ghost" size="sm" class="sort-btn" @click="toggleSort('employee')">
                  Ansatt
                  <ArrowUpDown :size="14" class="sort-icon" :class="{ 'sort-icon--active': sortField === 'employee' }" />
                </Button>
              </TableHead>
              <TableHead class="th-title">
                <Button variant="ghost" size="sm" class="sort-btn" @click="toggleSort('title')">
                  Opplæringstype
                  <ArrowUpDown :size="14" class="sort-icon" :class="{ 'sort-icon--active': sortField === 'title' }" />
                </Button>
              </TableHead>
              <TableHead class="th-date hide-mobile">
                <Button variant="ghost" size="sm" class="sort-btn" @click="toggleSort('completed')">
                  Fullført
                  <ArrowUpDown :size="14" class="sort-icon" :class="{ 'sort-icon--active': sortField === 'completed' }" />
                </Button>
              </TableHead>
              <TableHead class="th-date hide-mobile">
                <Button variant="ghost" size="sm" class="sort-btn" @click="toggleSort('expires')">
                  Utløper
                  <ArrowUpDown :size="14" class="sort-icon" :class="{ 'sort-icon--active': sortField === 'expires' }" />
                </Button>
              </TableHead>
              <TableHead class="th-status">
                <Button variant="ghost" size="sm" class="sort-btn" @click="toggleSort('status')">
                  Status
                  <ArrowUpDown :size="14" class="sort-icon" :class="{ 'sort-icon--active': sortField === 'status' }" />
                </Button>
              </TableHead>
              <TableHead class="th-actions" />
            </TableRow>
          </TableHeader>

          <TableBody>
            <TableRow
              v-for="log in filteredAndSorted"
              :key="log.id"
              :class="selected.has(log.id) ? 'row-selected' : ''"
            >
              <TableCell class="td-check">
                <Checkbox :checked="selected.has(log.id)" @update:checked="toggleSelect(log.id)" />
              </TableCell>
              <TableCell>
                <div class="cell-user">
                  <div class="user-avatar">
                    {{ log.employeeUserName.split(' ').slice(0, 2).map(w => w[0]).join('').toUpperCase() }}
                  </div>
                  <span class="user-name">{{ log.employeeUserName }}</span>
                </div>
              </TableCell>
              <TableCell class="cell-text">{{ log.title }}</TableCell>
              <TableCell class="cell-text hide-mobile">{{ formatDate(log.completedAt) }}</TableCell>
              <TableCell :class="`cell-text hide-mobile${log.status === 'EXPIRES_SOON' ? ' cell-expires-soon' : ''}`">
                {{ formatDate(log.expiresAt) }}
              </TableCell>
              <TableCell>
                <StatusBadge :status="statusLabel[log.status]" />
              </TableCell>
              <TableCell class="cell-actions">
                <DropdownMenu>
                  <DropdownMenuTrigger as-child>
                    <Button type="button" variant="ghost" size="icon-sm" class="actions-trigger">
                      <MoreVertical :size="18" />
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end" :side-offset="4">
                    <DropdownMenuLabel>Handlinger</DropdownMenuLabel>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem @click="openEdit(log)">
                      <Pencil :size="16" />
                      Rediger
                    </DropdownMenuItem>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem class="menu-item--danger" @click="openDeleteSingle(log)">
                      <Trash2 :size="16" />
                      Slett
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              </TableCell>
            </TableRow>

            <TableEmpty v-if="filteredAndSorted.length === 0" :colspan="7">
              Ingen opplæringer matcher søket.
            </TableEmpty>
          </TableBody>
        </Table>
      </div>
    </section>

    <!-- Modals -->
    <RegisterTrainingModal :open="showRegister" @update:open="(v) => { showRegister = v }" />
    <EditTrainingModal
      :open="editModalOpen"
      :training="editingLog"
      @update:open="(v) => { editModalOpen = v }"
    />

    <!-- Delete confirmation dialog -->
    <AlertDialog :open="deleteDialogOpen" @update:open="(v: boolean) => { deleteDialogOpen = v }">
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Slett opplæring?</AlertDialogTitle>
          <AlertDialogDescription>
            {{ deletingIds.length === 1
            ? 'Er du sikker på at du vil slette denne opplæringen? Dette kan ikke angres.'
            : `Er du sikker på at du vil slette ${deletingIds.length} opplæringer? Dette kan ikke angres.`
            }}
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>Avbryt</AlertDialogCancel>
          <AlertDialogAction variant="destructive" @click="confirmDelete">Slett</AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  </div>
</template>

<style scoped>
.page-header { display: flex; height: 4rem; flex-shrink: 0; align-items: center; }
.page-header-inner { display: flex; align-items: center; gap: 0.5rem; padding: 0 1rem; }
.header-separator { height: 1rem !important; width: 1px !important; margin-right: 0.5rem; }
.page-title { font-weight: 500; color: hsl(var(--sidebar-primary, 245 43% 52%)); }
.page-content { display: flex; flex: 1; flex-direction: column; gap: 1rem; padding: 0 1rem 1rem; }

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

h1 { margin: 0; font-size: 2.4rem; letter-spacing: -0.02em; }
.header-row p { margin-top: 6px; color: var(--text-secondary); font-size: 1.08rem; }

/* Stats cards */
.cards-group {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 10px;
}

/* Search */
.search-wrapper {
  position: relative;
  width: 20rem;
}

.search-icon {
  position: absolute;
  left: 0.65rem;
  top: 50%;
  transform: translateY(-50%);
  color: hsl(var(--muted-foreground, 24 5% 46%));
  pointer-events: none;
}

.search-input {
  width: 100%;
  border: 1px solid hsl(var(--border, 35 18% 88%));
  border-radius: 0.5rem;
  padding: 0.5rem 0.75rem 0.5rem 2.1rem;
  font: inherit;
  font-size: 0.85rem;
  background: hsl(var(--card, 40 25% 98%));
}

.search-input:focus {
  outline: none;
  border-color: hsl(var(--ring, 245 43% 52%));
  box-shadow: 0 0 0 2px hsl(var(--ring, 245 43% 52%) / 0.15);
}

/* Table */
.table-section { display: flex; flex-direction: column; gap: 0.75rem; }

.search-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
}

.table-card {
  border: 1px solid hsl(var(--border, 35 18% 88%));
  border-radius: 0.75rem;
  background: hsl(var(--card, 40 25% 98%));
}

/* Sort buttons */
.sort-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  background: none;
  border: none;
  font: inherit;
  font-weight: 500;
  color: hsl(var(--muted-foreground, 24 5% 46%));
  cursor: pointer;
  padding: 0.25rem 0.4rem;
  border-radius: var(--radius-md, 0.375rem);
  margin: -0.25rem -0.4rem;
  transition: background 150ms ease, color 150ms ease;
}

.sort-btn:hover {
  background: hsl(var(--accent, 250 40% 95%));
  color: hsl(var(--foreground, 24 10% 15%));
}

.sort-icon { opacity: 0.4; transition: opacity 150ms; }
.sort-icon--active { opacity: 1; }

/* Checkbox */
.th-check, .td-check { width: 3rem; padding-left: 1rem; padding-right: 0; }

/* Row selection */
.row-selected {
  background-color: hsl(var(--muted, 40 18% 93%) / 0.6) !important;
}

/* User cell */
.cell-user {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.user-avatar {
  width: 2.25rem;
  height: 2.25rem;
  border-radius: 9999px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: hsl(var(--muted, 40 18% 93%));
  color: hsl(var(--muted-foreground, 24 5% 46%));
  font-size: 0.75rem;
  font-weight: 600;
  flex-shrink: 0;
}

.user-name { font-weight: 500; }
.cell-text { color: hsl(var(--muted-foreground, 24 5% 46%)); }
.cell-expires-soon { color: #d97706; font-weight: 600; }

/* Actions cell */
.cell-actions {
  width: 3rem;
  text-align: right;
}

.actions-trigger {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  border-radius: var(--radius-md, 0.375rem);
  border: none;
  background: none;
  color: hsl(var(--muted-foreground, 24 5% 46%));
  cursor: pointer;
  transition: background 150ms ease, color 150ms ease;
}

.actions-trigger:hover {
  background: hsl(var(--accent, 250 40% 95%));
  color: hsl(var(--foreground, 24 10% 15%));
}

.menu-item--danger { color: #dc2626; }

/* Column widths */
.th-employee { min-width: 10rem; }
.th-title { min-width: 8rem; }
.th-date { min-width: 6rem; }
.th-status { min-width: 6rem; }
.th-actions { width: 3rem; }

/* Loading / Error states */
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
  letter-spacing: -0.01em;
}

.empty-state-text p {
  max-width: 24rem;
  font-size: 0.875rem;
  color: hsl(var(--muted-foreground));
  margin-top: 0.25rem;
}

@media (max-width: 1100px) {
  .cards-group { grid-template-columns: repeat(3, minmax(0, 1fr)); }
}

@media (max-width: 768px) {
  .hide-mobile { display: none; }
  .cards-group { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .header-row { flex-direction: column; }
  .search-wrapper { width: 100%; }
}
</style>
