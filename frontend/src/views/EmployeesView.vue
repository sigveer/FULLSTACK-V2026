<script setup lang="ts">
import { computed, ref } from 'vue'
import { ChevronDown, ChevronUp } from 'lucide-vue-next'
import AppLayout from '@/components/layout/AppLayout.vue'
import { Separator } from '@/components/ui/separator'
import { SidebarTrigger } from '@/components/ui/sidebar'
import { useAuthStore } from '@/stores/auth'

type Role = 'Admin' | 'Leder' | 'Ansatt'

type Employee = {
  id: number
  name: string
  email: string
  phone: string
  role: Role
  joinedAt: string
}

type OrganizationEmployees = {
  orgId: string
  orgName: string
  orgNumber: string
  category: string
  phone: string
  employees: Employee[]
}

const auth = useAuthStore()
const search = ref('')
const editingId = ref<number | null>(null)

const organizations: OrganizationEmployees[] = [
  {
    orgId: 'demo-1',
    orgName: 'Demo Organization',
    orgNumber: '2',
    category: 'Restaurant',
    phone: '+47 000 00 000',
    employees: [
      { id: 1, name: 'Admin User', email: 'admin@iksystem.local', phone: '+47 000 00 000', role: 'Admin', joinedAt: '01. jan 2024' },
      { id: 2, name: 'Anna Solberg', email: 'anna@demo.no', phone: '+47 111 11 111', role: 'Leder', joinedAt: '14. feb 2024' },
      { id: 3, name: 'Per Hansen', email: 'per@demo.no', phone: '+47 222 22 222', role: 'Leder', joinedAt: '08. mar 2024' },
      { id: 4, name: 'Kari Nordmann', email: 'kari@demo.no', phone: '+47 333 33 333', role: 'Ansatt', joinedAt: '17. apr 2024' },
      { id: 5, name: 'Ola Larsen', email: 'ola@demo.no', phone: '+47 444 44 444', role: 'Ansatt', joinedAt: '29. mai 2024' },
    ],
  },
  {
    orgId: 'demo-empty',
    orgName: 'Tom Virksomhet',
    orgNumber: '3',
    category: 'Kafé',
    phone: '+47 999 99 999',
    employees: [],
  },
]

const currentOrgId = computed(() => auth.organizationId || '')

const currentOrganization = computed(() => {
  return organizations.find((org) => org.orgId === currentOrgId.value) || null
})

const form = ref<Employee>({
  id: 0,
  name: '',
  email: '',
  phone: '',
  role: 'Ansatt',
  joinedAt: '',
})

const expandedIds = ref<number[]>([])

const filteredEmployees = computed(() => {
  const employees = currentOrganization.value?.employees || []
  const q = search.value.toLowerCase().trim()
  return q
    ? employees.filter((e) => [e.name, e.email, e.role].some((v) => v.toLowerCase().includes(q)))
    : employees
})

const stats = computed(() => {
  const employees = currentOrganization.value?.employees || []
  return {
    total: employees.length,
    leaders: employees.filter((e) => e.role === 'Leder').length,
    admins: employees.filter((e) => e.role === 'Admin').length,
  }
})

const hasOverview = computed(() => !!currentOrganization.value && currentOrganization.value.employees.length > 0)

const badgeClass = (role: Role) =>
  role === 'Admin' ? 'badge-admin' : role === 'Leder' ? 'badge-leader' : 'badge-employee'

const isExpanded = (id: number) => expandedIds.value.includes(id)

function toggleEmployee(id: number) {
  expandedIds.value = isExpanded(id) ? [] : [id]
}

function openEdit(employee: Employee) {
  editingId.value = employee.id
  form.value = { ...employee }
}

function saveEdit() {
  if (!currentOrganization.value) return
  const employeeIndex = currentOrganization.value.employees.findIndex((e) => e.id === editingId.value)
  if (employeeIndex === -1) return
  currentOrganization.value.employees[employeeIndex] = { ...form.value }
  editingId.value = null
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
      <template v-if="currentOrganization && hasOverview">
        <section class="hero-card">
          <h1>Ansatte</h1>
          <p>Administrer og se oversikt over alle ansatte.</p>

          <div class="org-card">
            <div class="org-avatar">{{ currentOrganization.orgName.slice(0, 2).toUpperCase() }}</div>
            <div>
              <h2>{{ currentOrganization.orgName }}</h2>
              <span class="org-meta">Org. nr: {{ currentOrganization.orgNumber }} · {{ currentOrganization.category }} · {{ currentOrganization.phone }}</span>
              <div class="stats-row">
                <div><strong>{{ stats.total }}</strong><span>ANSATTE</span></div>
                <div><strong>{{ stats.leaders }}</strong><span>LEDERE</span></div>
                <div><strong>{{ stats.admins }}</strong><span>ADMIN</span></div>
              </div>
            </div>
          </div>

          <input v-model="search" class="search-input" placeholder="Søk etter ansatt..." />
        </section>

        <section class="list-card">
          <article v-for="employee in filteredEmployees" :key="employee.id" class="employee-row">
            <div class="row-top">
              <div>
                <h3>{{ employee.name }}</h3>
                <p>{{ employee.email }}</p>
              </div>

              <div class="row-actions">
                <span class="badge" :class="badgeClass(employee.role)">{{ employee.role }}</span>
                <button class="icon-btn" @click="toggleEmployee(employee.id)">
                  <ChevronUp v-if="isExpanded(employee.id)" :size="18" />
                  <ChevronDown v-else :size="18" />
                </button>
              </div>
            </div>

            <div v-if="isExpanded(employee.id)" class="details">
              <div><span>E-POST</span><strong>{{ employee.email }}</strong></div>
              <div><span>TELEFON</span><strong>{{ employee.phone }}</strong></div>
              <div><span>ROLLE</span><strong>{{ employee.role }}</strong></div>
              <div class="details-last">
                <div><span>ANSATT SIDEN</span><strong>{{ employee.joinedAt }}</strong></div>
                <button class="edit-btn" @click="openEdit(employee)">Rediger</button>
              </div>
            </div>
          </article>
        </section>
      </template>

      <section v-else class="landing-card">
        <h1>Ansatte</h1>
        <p>Denne virksomheten har ingen ansatte registrert ennå.</p>
        <div class="landing-box">
          <h3>Tom oversikt</h3>
          <span>Når ansatte legges til for denne virksomheten, vil oversikten vises her.</span>
        </div>
      </section>
    </div>

    <div v-if="editingId" class="modal-backdrop" @click.self="editingId = null">
      <div class="modal">
        <h3>Rediger bruker</h3>
        <input v-model="form.name" placeholder="Navn" />
        <input v-model="form.email" placeholder="E-post" />
        <input v-model="form.phone" placeholder="Telefon" />
        <input v-model="form.joinedAt" placeholder="Ansatt siden" />
        <select v-model="form.role">
          <option value="Admin">Admin</option>
          <option value="Leder">Leder</option>
          <option value="Ansatt">Ansatt</option>
        </select>
        <div class="modal-actions">
          <button class="cancel-btn" @click="editingId = null">Avbryt</button>
          <button class="save-btn" @click="saveEdit">Lagre</button>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<style scoped>
.page-header { display: flex; height: 4rem; align-items: center; }
.page-header-inner { display: flex; align-items: center; gap: 0.5rem; padding: 0 1rem; }
.header-separator { height: 1rem !important; width: 1px !important; margin-right: 0.5rem; }
.page-title { font-weight: 500; color: hsl(var(--sidebar-primary, 245 43% 52%)); }
.page-content { display: flex; flex-direction: column; gap: 1rem; padding: 0 1rem 1rem; background: #f6f4ef; min-height: calc(100vh - 4rem); }
.hero-card, .list-card, .landing-card {
  border: 1px solid #e4ddd3;
  border-radius: 1rem;
  background: #fff;
}
.hero-card, .list-card { border: 1px solid #e4ddd3; border-radius: 1rem; background: #fff; }
.hero-card { padding: 1.5rem; }
.hero-card h1 { margin: 0; font-size: 2.4rem; font-weight: 800; }
.hero-card p { margin: 0.35rem 0 1.25rem; color: #6f6a63; }
.org-card { display: flex; gap: 1rem; align-items: center; padding: 1.25rem; border: 1px solid #e4ddd3; border-radius: 1rem; margin-bottom: 1rem; }
.org-avatar { width: 4.5rem; height: 4.5rem; border-radius: 1rem; display: flex; align-items: center; justify-content: center; background: #d9d0ff; color: #5a4fd6; font-size: 1.7rem; font-weight: 700; }
.org-card h2 { margin: 0; font-size: 1.8rem; }
.org-meta { color: #6f6a63; }
.stats-row { display: flex; gap: 2rem; margin-top: 1rem; flex-wrap: wrap; }
.stats-row div { display: flex; flex-direction: column; }
.stats-row strong { font-size: 1.8rem; line-height: 1; }
.stats-row span { font-size: 0.8rem; color: #6f6a63; letter-spacing: 0.05em; }
.search-input, .modal input, .modal select { width: 100%; border: 1px solid #ddd5ca; border-radius: 0.85rem; padding: 0.85rem 1rem; font: inherit; background: #fff; }
.list-card { overflow: hidden; }
.employee-row + .employee-row { border-top: 1px solid #ece5db; }
.row-top { display: flex; justify-content: space-between; align-items: center; gap: 1rem; padding: 1rem 1.25rem; }
.row-top h3 { margin: 0; font-size: 1.1rem; }
.row-top p { margin: 0.15rem 0 0; color: #6f6a63; }
.row-actions { display: flex; align-items: center; gap: 0.75rem; }
.badge { padding: 0.45rem 0.9rem; border-radius: 999px; font-size: 0.9rem; font-weight: 600; }
.badge-admin { background: #eee9ff; color: #5a4fd6; }
.badge-leader { background: #f7ecd2; color: #9a7322; }
.badge-employee { background: #e5efd9; color: #5d7f31; }
.icon-btn { border: 0; background: transparent; color: #6f6a63; cursor: pointer; display: flex; }
.details { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 1rem; padding: 0 1.25rem 1.25rem; border-top: 1px solid #ece5db; }
.details div { display: flex; flex-direction: column; gap: 0.35rem; padding-top: 1rem; }
.details span { font-size: 0.8rem; color: #6f6a63; letter-spacing: 0.05em; }
.details strong { font-size: 1rem; }
.details-last { display: flex !important; flex-direction: row !important; justify-content: space-between; align-items: end; gap: 1rem; }
.edit-btn, .save-btn, .cancel-btn { border: 0; border-radius: 0.8rem; padding: 0.75rem 1rem; font-weight: 600; cursor: pointer; }
.edit-btn { background: #eee9ff; color: #5a4fd6; }
.landing-card { padding: 1.5rem; }
.landing-card h1 { margin: 0; font-size: 2.4rem; font-weight: 800; }
.landing-card p { margin: 0.35rem 0 1.25rem; color: #6f6a63; }
.landing-box { border: 1px dashed #d8cfff; background: #f6f1ff; color: #5a4fd6; border-radius: 1rem; padding: 1.25rem; display: flex; flex-direction: column; gap: 0.35rem; }
.landing-box h3 { margin: 0; font-size: 1.1rem; }
.landing-box span { color: #6f6a63; }
.modal-backdrop { position: fixed; inset: 0; background: rgba(0,0,0,0.2); display: flex; align-items: center; justify-content: center; padding: 1rem; }
.modal { width: 100%; max-width: 26rem; background: white; border-radius: 1rem; padding: 1rem; display: grid; gap: 0.75rem; }
.modal h3 { margin: 0 0 0.25rem; }
.modal-actions { display: flex; justify-content: end; gap: 0.75rem; }
.cancel-btn { background: #efebe4; color: #3b3732; }
.save-btn { background: #5a4fd6; color: white; }
@media (max-width: 700px) { .details { grid-template-columns: 1fr; } .details-last { flex-direction: column !important; align-items: flex-start; } .row-top { flex-direction: column; align-items: flex-start; } }
</style>
