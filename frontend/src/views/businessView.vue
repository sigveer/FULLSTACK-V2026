<script setup lang="ts">
import SelectOrgForm from '@/components/SelectOrgForm.vue'
import { ref } from 'vue'

const selectOrgFormRef = ref<InstanceType<typeof SelectOrgForm> | null>(null)
</script>

<template>
  <div class="container">
    <p class="step">STEG 2: VELG ORGANISASJON</p>

    <div class="card">
      <div>
        <h2>Velg virksomhet</h2>
        <p class="subtitle">Du har tilgang til flere virksomheter</p>
      </div>

      <SelectOrgForm ref="selectOrgFormRef" />
    </div>

    <div v-if="selectOrgFormRef?.selectOrgError" class="error-card">
      Kunne ikke velge organisasjon
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
