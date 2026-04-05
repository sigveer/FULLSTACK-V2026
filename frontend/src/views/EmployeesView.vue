<script setup lang="ts">
import { computed, ref, toRef } from 'vue'
import { MoreVertical, Shield, ShieldCheck, User, UserMinus, ArrowUpDown, Search, Plus, Mail } from 'lucide-vue-next'
import { z } from 'zod/v4'
import { toast } from 'vue-sonner'
import AppLayout from '@/components/layout/AppLayout.vue'
import Button from '@/components/ui/button/Button.vue'
import { Separator } from '@/components/ui/separator'
import { SidebarTrigger } from '@/components/ui/sidebar'
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
import Dialog from '@/components/ui/dialog/Dialog.vue'
import DialogContent from '@/components/ui/dialog/DialogContent.vue'
import DialogDescription from '@/components/ui/dialog/DialogDescription.vue'
import DialogFooter from '@/components/ui/dialog/DialogFooter.vue'
import DialogHeader from '@/components/ui/dialog/DialogHeader.vue'
import DialogTitle from '@/components/ui/dialog/DialogTitle.vue'
import AlertDialog from '@/components/ui/alert-dialog/AlertDialog.vue'
import AlertDialogAction from '@/components/ui/alert-dialog/AlertDialogAction.vue'
import AlertDialogCancel from '@/components/ui/alert-dialog/AlertDialogCancel.vue'
import AlertDialogContent from '@/components/ui/alert-dialog/AlertDialogContent.vue'
import AlertDialogDescription from '@/components/ui/alert-dialog/AlertDialogDescription.vue'
import AlertDialogFooter from '@/components/ui/alert-dialog/AlertDialogFooter.vue'
import AlertDialogHeader from '@/components/ui/alert-dialog/AlertDialogHeader.vue'
import AlertDialogTitle from '@/components/ui/alert-dialog/AlertDialogTitle.vue'
import Badge from '@/components/ui/badge/Badge.vue'
import Select from '@/components/ui/select/Select.vue'
import SelectContent from '@/components/ui/select/SelectContent.vue'
import SelectItem from '@/components/ui/select/SelectItem.vue'
import SelectTrigger from '@/components/ui/select/SelectTrigger.vue'
import SelectValue from '@/components/ui/select/SelectValue.vue'
import { useAuthStore } from '@/stores/auth'
import {
  useMembersQuery,
  useOrganizationQuery,
  useUpdateMemberRoleMutation,
  useRemoveMemberMutation,
  useInviteMutation,
} from '@/composables/useMembers'
import type { OrganizationMember } from '@/types/member'
import type { AxiosError } from 'axios'

const auth = useAuthStore()
const search = ref('')

const orgId = toRef(() => auth.organizationId)
const { data: members, isLoading: membersLoading } = useMembersQuery()
const { data: org } = useOrganizationQuery(orgId)

const updateRoleMutation = useUpdateMemberRoleMutation()
const removeMemberMutation = useRemoveMemberMutation()
const inviteMutation = useInviteMutation()

const roleLabel: Record<string, string> = {
  ADMIN: 'Admin',
  MANAGER: 'Leder',
  EMPLOYEE: 'Ansatt',
}

const roleOptions = ['ADMIN', 'MANAGER', 'EMPLOYEE'] as const

type SortField = 'name' | 'email' | 'role'
type SortDir = 'asc' | 'desc'

const sortField = ref<SortField>('name')
const sortDir = ref<SortDir>('asc')

function toggleSort(field: SortField) {
  if (sortField.value === field) {
    sortDir.value = sortDir.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortField.value = field
    sortDir.value = 'asc'
  }
}

const isSelf = (member: OrganizationMember) => member.userId === auth.user?.id
const isAdmin = computed(() => auth.role === 'ADMIN')

const sortedAndFilteredMembers = computed(() => {
  if (!members.value) return []

  const q = search.value.toLowerCase().trim()
  let list = members.value.slice()

  if (q) {
    list = list.filter((m) =>
      [m.userFullName, m.userEmail, roleLabel[m.role] ?? m.role].some((v) =>
        v.toLowerCase().includes(q),
      ),
    )
  }

  const self = list.filter((m) => isSelf(m))
  const others = list.filter((m) => !isSelf(m))

  others.sort((a, b) => {
    let cmp = 0
    if (sortField.value === 'name') {
      cmp = a.userFullName.localeCompare(b.userFullName, 'nb')
    } else if (sortField.value === 'email') {
      cmp = a.userEmail.localeCompare(b.userEmail, 'nb')
    } else if (sortField.value === 'role') {
      const order = { ADMIN: 0, MANAGER: 1, EMPLOYEE: 2 }
      cmp = (order[a.role as keyof typeof order] ?? 3) - (order[b.role as keyof typeof order] ?? 3)
    }
    return sortDir.value === 'desc' ? -cmp : cmp
  })

  return [...self, ...others]
})

const stats = computed(() => {
  const list = members.value ?? []
  return {
    total: list.length,
    managers: list.filter((m) => m.role === 'MANAGER').length,
    admins: list.filter((m) => m.role === 'ADMIN').length,
  }
})

function initials(name: string) {
  return name
    .split(' ')
    .slice(0, 2)
    .map((w) => w[0])
    .join('')
    .toUpperCase()
}

function badgeTone(role: string) {
  if (role === 'ADMIN') return 'brand'
  if (role === 'MANAGER') return 'warning'
  return 'ok'
}

// ── Role editing ──
const roleDialogOpen = ref(false)
const editingMember = ref<OrganizationMember | null>(null)
const selectedRole = ref('')

function openRoleEdit(member: OrganizationMember) {
  editingMember.value = member
  selectedRole.value = member.role
  roleDialogOpen.value = true
}

function saveRole() {
  const member = editingMember.value
  const role = selectedRole.value
  roleDialogOpen.value = false
  editingMember.value = null
  if (!member || role === member.role) return
  updateRoleMutation.mutate({ userId: member.userId, payload: { role } })
}

function cancelRoleEdit() {
  roleDialogOpen.value = false
  editingMember.value = null
}

// ── Remove member ──
const removeDialogOpen = ref(false)
const removingMember = ref<OrganizationMember | null>(null)

function openRemoveDialog(member: OrganizationMember) {
  removingMember.value = member
  removeDialogOpen.value = true
}

function confirmRemove() {
  const member = removingMember.value
  removeDialogOpen.value = false
  removingMember.value = null
  if (!member) return
  removeMemberMutation.mutate(member.userId)
}

function cancelRemove() {
  removeDialogOpen.value = false
  removingMember.value = null
}

// ── Add employee dialog ──
const addDialogOpen = ref(false)
const inviteEmail = ref('')
const inviteRole = ref('EMPLOYEE')
const inviteError = ref('')

const emailSchema = z.email({ message: 'Ugyldig e-postadresse' })

function openAddDialog() {
  inviteEmail.value = ''
  inviteRole.value = 'EMPLOYEE'
  inviteError.value = ''
  addDialogOpen.value = true
}

function handleInvite() {
  const result = emailSchema.safeParse(inviteEmail.value.trim())
  if (!result.success) {
    inviteError.value = result.error?.issues?.[0]?.message ?? 'Ugyldig e-postadresse'
    return
  }
  inviteError.value = ''
  inviteMutation.mutate(
    { email: inviteEmail.value.trim(), role: inviteRole.value },
    {
      onSuccess: () => {
        addDialogOpen.value = false
        toast.success('Invitasjon sendt til ' + inviteEmail.value.trim())
      },
      onError: (error: AxiosError) => {
        const msg = (error?.response?.data as Record<string, unknown>)?.error?.message ?? 'Kunne ikke sende invitasjon'
        inviteError.value = msg as string
        toast.error(msg as string)
      },
    },
  )
}
</script>

<template>
  <AppLayout>
    <header class="page-header">
      <div class="page-header-inner">
        <SidebarTrigger />
        <Separator orientation="vertical" class="header-separator" />
        <span class="page-title">Ansatte</span>
      </div>
    </header>

    <div class="page-content">
      <section class="header-row">
        <div>
          <h1>Ansatte</h1>
          <p>Administrer og se oversikt over alle ansatte.</p>
        </div>
        <Button @click="openAddDialog">
          <Plus :size="16" />
          Legg til ansatt
        </Button>
      </section>

      <div v-if="membersLoading" class="loading-state">
        Laster ansatte...
      </div>

      <template v-else-if="members && members.length > 0">
        <section class="org-card">
          <div class="org-left">
            <div class="org-avatar">{{ (org?.name ?? '??').slice(0, 2).toUpperCase() }}</div>
            <div class="org-details">
              <h2>{{ org?.name ?? 'Laster...' }}</h2>
              <span v-if="org?.orgNumber" class="org-meta">Org. nr: {{ org.orgNumber }}</span>
            </div>
          </div>
          <div class="org-stats">
            <div class="stat-item">
              <span class="stat-label">ANSATTE</span>
              <strong class="stat-value">{{ stats.total }}</strong>
            </div>
            <div class="stat-item">
              <span class="stat-label">LEDERE</span>
              <strong class="stat-value">{{ stats.managers }}</strong>
            </div>
            <div class="stat-item">
              <span class="stat-label">ADMIN</span>
              <strong class="stat-value">{{ stats.admins }}</strong>
            </div>
          </div>
        </section>

        <section class="table-section">
          <div class="search-wrapper">
            <Search :size="16" class="search-icon" />
            <input v-model="search" class="search-input" placeholder="Søk etter ansatt..." />
          </div>

          <div class="table-card">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead class="th-name">
                    <Button variant="ghost" size="sm" class="sort-btn" @click="toggleSort('name')">
                      Navn
                      <ArrowUpDown :size="14" class="sort-icon" :class="{ 'sort-icon--active': sortField === 'name' }" />
                    </Button>
                  </TableHead>
                  <TableHead class="th-email">
                    <Button variant="ghost" size="sm" class="sort-btn" @click="toggleSort('email')">
                      E-post
                      <ArrowUpDown :size="14" class="sort-icon" :class="{ 'sort-icon--active': sortField === 'email' }" />
                    </Button>
                  </TableHead>
                  <TableHead class="th-role">
                    <Button variant="ghost" size="sm" class="sort-btn" @click="toggleSort('role')">
                      Rolle
                      <ArrowUpDown :size="14" class="sort-icon" :class="{ 'sort-icon--active': sortField === 'role' }" />
                    </Button>
                  </TableHead>
                  <TableHead class="th-actions" />
                </TableRow>
              </TableHeader>

              <TableBody>
                <TableRow
                  v-for="member in sortedAndFilteredMembers"
                  :key="member.id"
                  :class="isSelf(member) ? 'row-self' : ''"
                >
                  <TableCell>
                    <div class="cell-user">
                      <div class="user-avatar" :class="{ 'user-avatar--self': isSelf(member) }">
                        {{ initials(member.userFullName) }}
                      </div>
                      <div>
                        <span class="user-name">{{ member.userFullName }}</span>
                        <span v-if="isSelf(member)" class="you-label">(deg)</span>
                      </div>
                    </div>
                  </TableCell>
                  <TableCell class="cell-email">{{ member.userEmail }}</TableCell>
                  <TableCell>
                    <Badge :tone="badgeTone(member.role)">
                      {{ roleLabel[member.role] ?? member.role }}
                    </Badge>
                  </TableCell>
                  <TableCell class="cell-actions">
                    <div v-if="!isSelf(member)" class="actions-wrapper">
                      <DropdownMenu>
                        <DropdownMenuTrigger as-child>
                          <Button type="button" variant="ghost" size="icon-sm" class="actions-trigger">
                            <MoreVertical :size="18" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end" :side-offset="4">
                          <DropdownMenuLabel>Handlinger</DropdownMenuLabel>
                          <DropdownMenuSeparator />
                          <DropdownMenuItem @click="openRoleEdit(member)">
                            <ShieldCheck :size="16" />
                            Endre rolle
                          </DropdownMenuItem>
                          <template v-if="isAdmin">
                            <DropdownMenuSeparator />
                            <DropdownMenuItem class="menu-item--danger" @click="openRemoveDialog(member)">
                              <UserMinus :size="16" />
                              Fjern fra organisasjon
                            </DropdownMenuItem>
                          </template>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </div>
                  </TableCell>
                </TableRow>

                <TableEmpty v-if="sortedAndFilteredMembers.length === 0" :colspan="4">
                  Ingen ansatte matcher søket.
                </TableEmpty>
              </TableBody>
            </Table>
          </div>
        </section>
      </template>

      <section v-else-if="!membersLoading" class="landing-card">
        <h1>Ansatte</h1>
        <p>Denne virksomheten har ingen ansatte registrert ennå.</p>
        <div class="landing-box">
          <h3>Tom oversikt</h3>
          <span>Når ansatte legges til for denne virksomheten, vil oversikten vises her.</span>
        </div>
      </section>
    </div>

    <!-- Edit role dialog -->
    <AlertDialog :open="roleDialogOpen" @update:open="(v: boolean) => { roleDialogOpen = v }">
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Endre rolle</AlertDialogTitle>
          <AlertDialogDescription>
            Velg ny rolle for {{ editingMember?.userFullName }}.
          </AlertDialogDescription>
        </AlertDialogHeader>

        <div class="role-options">
          <label
            v-for="role in roleOptions"
            :key="role"
            class="role-option"
            :class="{ 'role-option--selected': selectedRole === role }"
          >
            <input v-model="selectedRole" type="radio" :value="role" class="sr-only" />
            <Shield v-if="role === 'ADMIN'" :size="18" />
            <ShieldCheck v-else-if="role === 'MANAGER'" :size="18" />
            <User v-else :size="18" />
            <span>{{ roleLabel[role] }}</span>
          </label>
        </div>

        <AlertDialogFooter>
          <AlertDialogCancel @click="cancelRoleEdit">Avbryt</AlertDialogCancel>
          <AlertDialogAction @click="saveRole">Lagre</AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>

    <!-- Remove member dialog -->
    <AlertDialog :open="removeDialogOpen" @update:open="(v: boolean) => { removeDialogOpen = v }">
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Fjern fra organisasjon?</AlertDialogTitle>
          <AlertDialogDescription>
            {{ removingMember?.userFullName }} vil miste tilgangen til denne organisasjonen. Brukerkontoen blir ikke slettet.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel @click="cancelRemove">Avbryt</AlertDialogCancel>
          <AlertDialogAction variant="destructive" @click="confirmRemove">Fjern</AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>

    <!-- Add employee dialog -->
    <Dialog :open="addDialogOpen" @update:open="(v: boolean) => { addDialogOpen = v }">
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Legg til ansatt</DialogTitle>
          <DialogDescription>
            Inviter en ny ansatt til organisasjonen via e-post.
          </DialogDescription>
        </DialogHeader>

        <form class="invite-form" @submit.prevent="handleInvite">
          <div class="form-field">
            <label class="form-label" for="invite-email">E-post</label>
            <div class="email-input-wrapper">
              <Mail :size="16" class="email-input-icon" />
              <input
                id="invite-email"
                v-model="inviteEmail"
                type="email"
                class="email-input"
                placeholder="navn@eksempel.no"
                autocomplete="off"
              />
            </div>
            <span v-if="inviteError" class="form-error">{{ inviteError }}</span>
          </div>

          <div class="form-field">
            <label class="form-label">Rolle</label>
            <Select v-model="inviteRole">
              <SelectTrigger>
                <SelectValue placeholder="Velg rolle" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="ADMIN">Admin</SelectItem>
                <SelectItem value="MANAGER">Leder</SelectItem>
                <SelectItem value="EMPLOYEE">Ansatt</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <DialogFooter>
            <Button variant="outline" type="button" @click="addDialogOpen = false">Avbryt</Button>
            <Button type="submit" :disabled="inviteMutation.isPending.value">
              {{ inviteMutation.isPending.value ? 'Sender...' : 'Send invitasjon' }}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  </AppLayout>
</template>

<style scoped>
.page-header { display: flex; height: 4rem; flex-shrink: 0; align-items: center; }
.page-header-inner { display: flex; align-items: center; gap: 0.5rem; padding: 0 1rem; }
.header-separator { height: 1rem !important; width: 1px !important; margin-right: 0.5rem; }
.page-title { font-weight: 500; color: hsl(var(--sidebar-primary, 245 43% 52%)); }
.page-content { display: flex; flex: 1; flex-direction: column; gap: 1rem; padding: 0 1rem 1rem; }

/* Header */
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
}

h1 { margin: 0; font-size: 2.4rem; letter-spacing: -0.02em; }
.header-row p { margin-top: 6px; color: var(--text-secondary); font-size: 1.08rem; }

.loading-state { padding: 3rem; text-align: center; color: hsl(var(--muted-foreground, 24 5% 46%)); }

/* Org card */
.org-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  padding: 1rem 1.25rem;
  border: 1px solid hsl(var(--border, 35 18% 88%));
  border-radius: 0.75rem;
  background: hsl(var(--card, 40 25% 98%));
}

.org-left {
  display: flex;
  gap: 0.75rem;
  align-items: center;
  min-width: 0;
}

.org-avatar {
  width: 2.75rem;
  height: 2.75rem;
  border-radius: 0.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #d9d0ff;
  color: #5a4fd6;
  font-size: 1.1rem;
  font-weight: 700;
  flex-shrink: 0;
}

.org-details { min-width: 0; }
.org-details h2 { margin: 0; font-size: 1.15rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.org-meta { color: hsl(var(--muted-foreground, 24 5% 46%)); font-size: 0.85rem; }

.org-stats {
  display: flex;
  gap: 1.5rem;
  flex-shrink: 0;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.1rem;
}

.stat-label {
  font-size: 0.6rem;
  font-weight: 500;
  letter-spacing: 0.06em;
  color: hsl(var(--muted-foreground, 24 5% 46%));
}

.stat-value {
  font-size: 1.4rem;
  font-weight: 700;
  line-height: 1;
  color: hsl(var(--foreground, 24 10% 15%));
}

/* Search */
.search-wrapper {
  position: relative;
  width: 16rem;
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

.table-card {
  border: 1px solid hsl(var(--border, 35 18% 88%));
  border-radius: 0.75rem;
  background: hsl(var(--card, 40 25% 98%));
}

/* Sort buttons — same hover as three-dot trigger */
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

/* Self row */
.row-self {
  background-color: hsl(var(--muted, 40 18% 93%) / 0.6) !important;
}

.row-self:hover {
  background-color: hsl(var(--muted, 40 18% 93%) / 0.75) !important;
}

/* User avatar in table */
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

.user-avatar--self {
  background: #d9d0ff;
  color: #5a4fd6;
}

.user-name { font-weight: 500; }
.you-label { color: hsl(var(--muted-foreground, 24 5% 46%)); font-size: 0.8rem; margin-left: 0.25rem; }

.cell-email { color: hsl(var(--muted-foreground, 24 5% 46%)); }

/* Actions cell */
.cell-actions {
  width: 3rem;
  text-align: right;
}

.actions-wrapper {
  position: relative;
  display: inline-flex;
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

/* Responsive columns */
.th-name { min-width: 10rem; }
.th-email { min-width: 10rem; }
.th-role { min-width: 6rem; }
.th-actions { width: 3rem; }

/* Landing / empty */
.landing-card {
  padding: 1.5rem;
  border: 1px solid hsl(var(--border, 35 18% 88%));
  border-radius: 1rem;
  background: hsl(var(--card, 40 25% 98%));
}
.landing-card h1 { margin: 0; font-size: 2.4rem; font-weight: 800; }
.landing-card p { margin: 0.35rem 0 1.25rem; color: hsl(var(--muted-foreground, 24 5% 46%)); }
.landing-box {
  border: 1px dashed #d8cfff;
  background: #f6f1ff;
  color: #5a4fd6;
  border-radius: 1rem;
  padding: 1.25rem;
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}
.landing-box h3 { margin: 0; font-size: 1.1rem; }
.landing-box span { color: hsl(var(--muted-foreground, 24 5% 46%)); }

/* Role edit dialog */
.role-options { display: flex; flex-direction: column; gap: 0.5rem; }
.role-option {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  border: 1px solid hsl(var(--border, 35 18% 88%));
  border-radius: 0.75rem;
  cursor: pointer;
  transition: border-color 0.15s, background 0.15s;
}
.role-option:hover { background: hsl(var(--muted, 40 18% 93%) / 0.5); }
.role-option--selected { border-color: #5a4fd6; background: #f3f0ff; }
.sr-only { position: absolute; width: 1px; height: 1px; padding: 0; margin: -1px; overflow: hidden; clip: rect(0,0,0,0); border: 0; }

/* Invite form */
.invite-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.form-label {
  font-size: 0.85rem;
  font-weight: 500;
  color: hsl(var(--foreground, 24 10% 15%));
}

.email-input-wrapper {
  position: relative;
}

.email-input-icon {
  position: absolute;
  left: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  color: hsl(var(--muted-foreground, 24 5% 46%));
  pointer-events: none;
}

.email-input {
  display: flex;
  height: 2.5rem;
  width: 100%;
  border-radius: 0.5rem;
  border: 1px solid hsl(var(--input, 35 15% 85%));
  background-color: hsl(var(--card, 40 25% 98%));
  padding: 0.5rem 0.75rem 0.5rem 2.25rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
  color: inherit;
  box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05);
  outline: none;
  transition: all 150ms ease;
  font-family: inherit;
}

.email-input::placeholder {
  color: hsl(var(--muted-foreground, 24 5% 46%));
}

.email-input:focus {
  box-shadow: 0 0 0 2px hsl(var(--ring, 245 43% 52%) / 0.2);
  border-color: hsl(var(--primary, 245 43% 52%) / 0.5);
}

.form-error {
  font-size: 0.8rem;
  color: #dc2626;
}

@media (max-width: 700px) {
  .th-email, .cell-email { display: none; }
  .org-card { flex-direction: column; align-items: flex-start; }
  .org-stats { align-self: flex-start; }
}
</style>
