<script setup lang="ts">
import LoginForm from '@/components/LoginForm.vue'
import { ref } from 'vue'
import veraLogo from '@/assets/vera.png'

const loginFormRef = ref<InstanceType<typeof LoginForm> | null>(null)
</script>

<template>
  <div class="container">
    <p class="step">STEG 1: INNLOGGING</p>

    <div class="card">
      <div>
        <img :src="veraLogo" alt="Vera" class="brand-logo" />
        <p class="subtitle">Logg inn for å fortsette</p>
      </div>

      <LoginForm ref="loginFormRef" />
    </div>

    <div v-if="loginFormRef?.loginError" class="error-card">
      Feil e-post eller passord
    </div>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 40px;
  justify-content: center;
  height: 90vh;
  margin-bottom: 20px;
  font-family: Arial, sans-serif;
}

.step {
  color: hsl(var(--muted-foreground));
  margin-bottom: 16px;
  font-weight: 500;
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
}

.brand-logo {
  display: block;
  width: 180px;
  max-width: 100%;
  margin: 0 auto 12px;
}

.subtitle {
  text-align: center;
  color: hsl(var(--muted-foreground));
  margin-bottom: 20px;
}

.error-card {
  width: 350px;
  margin-top: 12px;
  padding: 12px 16px;
  background: hsl(var(--destructive) / 0.1);
  border: 1px solid hsl(var(--destructive) / 0.3);
  border-radius: 10px;
  color: hsl(var(--destructive));
  font-size: 0.875rem;
  text-align: center;
}
</style>
