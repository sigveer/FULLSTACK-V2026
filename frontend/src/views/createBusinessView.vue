<script setup lang="ts">
import CreateOrgForm from '@/components/CreateOrgForm.vue'
import { ref } from 'vue'

const createOrgFormRef = ref<InstanceType<typeof CreateOrgForm> | null>(null)
</script>

<template>
  <div class="container">
    <p class="step">OPPRETT VIRKSOMHET</p>

    <div class="card">
      <div>
        <h2>IK-Komplett</h2>
        <p class="subtitle">Fyll inn informasjon om din virksomhet</p>
      </div>

      <CreateOrgForm ref="createOrgFormRef" />
    </div>

    <div v-if="createOrgFormRef?.createOrgError" class="error-card">
      Kunne ikke opprette virksomhet. Prøv igjen.
    </div>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 90vh;
  padding: 40px 1rem;
  font-family: Arial, sans-serif;
}

.step {
  color: hsl(var(--muted-foreground));
  margin-bottom: 16px;
  font-weight: 500;
  font-size: 0.75rem;
  letter-spacing: 0.05em;
}

.card {
  width: 420px;
  background: hsl(var(--card));
  border-radius: 10px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

h2 {
  color: hsl(var(--primary));
  text-align: center;
  margin-bottom: 8px;
}

.subtitle {
  text-align: center;
  color: hsl(var(--muted-foreground));
  margin-bottom: 20px;
}

.error-card {
  width: 420px;
  margin-top: 12px;
  padding: 12px 16px;
  background: hsl(var(--destructive) / 0.1);
  border: 1px solid hsl(var(--destructive) / 0.3);
  border-radius: 10px;
  color: hsl(var(--destructive));
  font-size: 0.875rem;
  text-align: center;
}

@media (max-width: 520px) {
  .card,
  .error-card {
    width: 100%;
  }
}
</style>
