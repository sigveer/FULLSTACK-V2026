import { createRouter, createWebHistory } from 'vue-router'
import DashboardView from '@/views/DashboardView.vue'
import LoginView from '@/views/LoginView.vue'
import BusinessView from '@/views/BusinessView.vue'
import ChecklistView from '@/views/ChecklistView.vue'
import TemperatureView from '@/views/TemperatureView.vue'
import DeviationsView from '@/views/DeviationsView.vue'
import TrainingView from '@/views/TrainingView.vue'
import AllowanceView from '@/views/AllowanceView.vue'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/signup',
      name: 'signup',
      component: () => import('@/views/signUpView.vue'),
      meta: { public: true },
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { public: true },
    },
    {
      path: '/select-org',
      name: 'select-org',
      component: BusinessView,
      meta: { public: true },
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      redirect: '/',
    },
    {
      path: '/',
      name: 'home',
      component: DashboardView,
    },
    {
      path: '/create-org',
      name: 'create-org',
      component: () => import('@/views/createBusinessView.vue'),
      meta: { public: true },
    },
    {
      path: '/sjekklister',
      name: 'sjekklister',
      component: ChecklistView,
    },
    {
      path: '/temperatur',
      name: 'temperatur',
      component: TemperatureView,
    },
    {
      path: '/business',
      name: 'business',
      component: BusinessView,
      meta: { public: true },
    },
    {
      path: '/opplaering',
      name: 'opplaering',
      component: TrainingView,
    },
    {
      path: '/bevilling',
      name: 'bevilling',
      component: AllowanceView,
    },
    {
      path: '/avvik',
      name: 'avvik',
      component: DeviationsView,
    },
    {
      path: '/ansatte',
      name: 'ansatte',
      component: () => import('@/views/EmployeesView.vue'),
    },
    {
      path: '/innstillinger',
      name: 'innstillinger',
      component: () => import('@/views/SettingsView.vue'),
    },
  ],
})

router.beforeEach((to, _from, next) => {
  const auth = useAuthStore()

  // Allow public routes
  if (to.meta.public) {
    return next()
  }

  // Not authenticated → redirect to login
  if (!auth.isAuthenticated) {
    return next('/login')
  }

  // Role-based route guard
  if (to.meta.role && auth.role !== to.meta.role) {
    return next('/unauthorized')
  }

  next()
})

export default router
