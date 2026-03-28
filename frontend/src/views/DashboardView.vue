<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { useLogout } from '@/composables/useAuth'
import Button from '@/components/ui/button/Button.vue'

const auth = useAuthStore()
const logout = useLogout()
</script>

<template>
  <div class="container">
    <div class="card">
      <h2>Velkommen, {{ auth.user?.fullName }}</h2>
      <p class="subtitle">{{ auth.role }} — Org #{{ auth.organizationId }}</p>

      <Button variant="destructive" @click="logout.mutate()">
        Logg ut
      </Button>
    </div>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 90vh;
  font-family: Arial, sans-serif;
}

.card {
  width: 350px;
  background: hsl(var(--card));
  border-radius: 10px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  gap: 10px;
  text-align: center;
}

h2 {
  color: hsl(var(--primary));
  margin-bottom: 4px;
}

.subtitle {
  color: hsl(var(--muted-foreground));
  margin-bottom: 12px;
}

.card > :deep(.btn) {
  width: 100%;
}
</style>
