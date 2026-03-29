import { createRouter, createWebHistory } from 'vue-router'
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
      component: () => import('@/views/loginView.vue'),
      meta: { public: true },
    },
    {
      path: '/select-org',
      name: 'select-org',
      component: () => import('@/views/businessView.vue'),
      meta: { public: true },
    },
    {
      path: '/create-org',
      name: 'create-org',
      component: () => import('@/views/createBusinessView.vue'),
      meta: { public: true },
    },
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/DashboardView.vue'),
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
