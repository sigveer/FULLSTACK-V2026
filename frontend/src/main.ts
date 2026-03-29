import './assets/main.css'
import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { VueQueryPlugin, queryPluginOptions } from './lib/query'
import { useAuthStore } from './stores/auth'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(VueQueryPlugin, queryPluginOptions)

app.directive('role', {
  mounted(el, binding) {
    const auth = useAuthStore()
    const requiredRole = binding.value

    if (auth.role !== requiredRole) {
      el.remove()
    }
  },
})

app.mount('#app')
