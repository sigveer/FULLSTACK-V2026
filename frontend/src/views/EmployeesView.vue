<script setup lang="ts">
import { Users } from 'lucide-vue-next'
import AppLayout from '@/components/layout/AppLayout.vue'
import { Separator } from '@/components/ui/separator'
import { SidebarTrigger } from '@/components/ui/sidebar'

type Role = 'Admin' | 'Leder' | 'Ansatt'

type Employee = {
  id: number
  name: string
  email: string
  phone: string
  role: Role
  joinedAt: string
  expanded: boolean
}

const search = ref('')
const editingId = ref<number | null>(null)

const employees = ref<Employee[]>([
  { id: 1, name: 'Admin User', email: 'admin@iksystem.local', phone: '+47 000 00 000', role: 'Admin', joinedAt: '01. jan 2024', expanded: true },
  { id: 2, name: 'Anna Solberg', email: 'anna@demo.no', phone: '+47 111 11 111', role: 'Leder', joinedAt: '14. feb 2024', expanded: false },
  { id: 3, name: 'Per Hansen', email: 'per@demo.no', phone: '+47 222 22 222', role: 'Leder', joinedAt: '08. mar 2024', expanded: false },
  { id: 4, name: 'Kari Nordmann', email: 'kari@demo.no', phone: '+47 333 33 333', role: 'Ansatt', joinedAt: '17. apr 2024', expanded: false },
  { id: 5, name: 'Ola Larsen', email: 'ola@demo.no', phone: '+47 444 44 444', role: 'Ansatt', joinedAt: '29. mai 2024', expanded: false },
])

const form = ref<Employee>({
  id: 0,
  name: '',
  email: '',
  phone: '',
  role: 'Ansatt',
  joinedAt: '',
  expanded: false,
})

const filteredEmployees = computed(() => {
  const q = search.value.toLowerCase().trim()
  return q
    ? employees.value.filter((e) => [e.name, e.email, e.role].some((v) => v.toLowerCase().includes(q)))
    : employees.value
})

const stats = computed(() => ({
  total: employees.value.length,
  leaders: employees.value.filter((e) => e.role === 'Leder').length,
  admins: employees.value.filter((e) => e.role === 'Admin').length,
}))

const badgeClass = (role: Role) =>
  role === 'Admin' ? 'badge-admin' : role === 'Leder' ? 'badge-leader' : 'badge-employee'

function toggleEmployee(id: number) {
  employees.value = employees.value.map((e) => ({ ...e, expanded: e.id === id ? !e.expanded : false }))
}

function openEdit(employee: Employee) {
  editingId.value = employee.id
  form.value = { ...employee }
}

function saveEdit() {
  employees.value = employees.value.map((e) => (e.id === editingId.value ? { ...form.value } : e))
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
      <section class="hero-card">
        <h1>Ansatte</h1>
        <p>Administrer og se oversikt over alle ansatte.</p>

        <div class="org-card">
          <div class="org-avatar">DE</div>
          <div>
            <h2>Demo Organization</h2>
            <span class="org-meta">Org. nr: 2 · Restaurant · +47 000 00 000</span>
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
                <ChevronUp v-if="employee.expanded" :size="18" />
                <ChevronDown v-else :size="18" />
              </button>
            </div>
          </div>

          <div v-if="employee.expanded" class="details">
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
    </div>
  </AppLayout>
</template>


<style scoped>
.page-header { display: flex; height: 4rem; flex-shrink: 0; align-items: center; }
.page-header-inner { display: flex; align-items: center; gap: 0.5rem; padding: 0 1rem; }
.header-separator { height: 1rem !important; width: 1px !important; margin-right: 0.5rem; }
.page-title { font-weight: 500; color: hsl(var(--sidebar-primary, 245 43% 52%)); }
.page-content { display: flex; flex: 1; flex-direction: column; gap: 1rem; padding: 0 1rem 1rem; }
.empty-state { position: relative; display: flex; min-height: 320px; flex-direction: column; align-items: center; justify-content: center; overflow: hidden; border-radius: 1rem; border: 2px dashed hsl(var(--muted-foreground) / 0.2); background: linear-gradient(to bottom right, hsl(var(--muted) / 0.4), hsl(var(--muted) / 0.2), hsl(var(--background))); padding: 2rem; }
.empty-state-bg { position: absolute; inset: 0; background: radial-gradient(ellipse at center, hsl(var(--muted)) 0%, transparent 70%); opacity: 0.5; }
.empty-state-inner { position: relative; display: flex; flex-direction: column; align-items: center; gap: 1rem; text-align: center; }
.empty-state-icon { display: flex; height: 5rem; width: 5rem; align-items: center; justify-content: center; border-radius: 1rem; background-color: hsl(var(--primary) / 0.1); box-shadow: 0 0 0 4px hsl(var(--primary) / 0.05); }
.empty-state-icon :deep(svg) { width: 2.5rem; height: 2.5rem; color: hsl(var(--primary) / 0.7); }
.empty-state-text h3 { font-size: 1.125rem; font-weight: 600; letter-spacing: -0.01em; }
.empty-state-text p { max-width: 24rem; font-size: 0.875rem; color: hsl(var(--muted-foreground)); margin-top: 0.25rem; }
</style>
