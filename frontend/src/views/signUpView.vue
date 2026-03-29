<script setup lang="ts">
import SignUpForm from '@/components/SignUpForm.vue'
import { ref } from 'vue'

const signUpFormRef = ref<InstanceType<typeof SignUpForm> | null>(null)
</script>

<template>
  <div class="container">
    <div class="layout">
      <div class="steps-col">
        <div class="step-entry">
          <div class="step-label active">Steg 1</div>
          <div class="step-dot active"></div>
        </div>
        <div class="step-line"></div>
        <div class="step-entry">
          <div class="step-dot"></div>
          <div class="step-label">Steg 2</div>
        </div>
      </div>

      <div class="cards-col">
        <div class="card">
          <div>
            <p class="step">STEG 1: REGISTRERING</p>
            <h2>IK-Komplett</h2>
            <p class="subtitle">Opprett en konto for å komme i gang</p>
          </div>

          <SignUpForm ref="signUpFormRef" />
        </div>

        <div class="card card--dim">
          <p class="step">STEG 2: BEKREFTELSE</p>
          <h3 class="dim-title">Bekreft e-post</h3>
          <p class="dim-sub">Du mottar en bekreftelseslenke etter registrering.</p>
        </div>

        <div v-if="signUpFormRef?.registerError" class="error-card">
          Noe gikk galt under registrering. Prøv igjen.
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 90vh;
  padding: 40px 1rem;
  font-family: Arial, sans-serif;
}

.layout {
  display: flex;
  align-items: center;
}

.steps-col {
  flex-shrink: 0;
}

/* ── Step sidebar ── */
.steps-col {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 1.25rem;
}

.step-entry {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.step-label {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.09em;
  color: hsl(var(--muted-foreground));
  text-transform: uppercase;
  writing-mode: vertical-rl;
  transform: rotate(180deg);
  padding: 8px 0;
}

.step-label.active {
  color: hsl(var(--primary));
}

.step-dot {
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: hsl(var(--border));
  flex-shrink: 0;
}

.step-dot.active {
  background: hsl(var(--primary));
}

.step-line {
  width: 2px;
  height: 50px;
  background: hsl(var(--border));
}

/* ── Cards column ── */
.cards-col {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 420px;
}

.step {
  color: hsl(var(--muted-foreground));
  margin-bottom: 8px;
  font-weight: 500;
  font-size: 0.75rem;
  letter-spacing: 0.05em;
}

.card {
  background: hsl(var(--card));
  border-radius: 10px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.card--dim {
  background: hsl(var(--muted));
  box-shadow: none;
}

.dim-title {
  color: hsl(var(--muted-foreground));
  font-size: 1.1rem;
}

.dim-sub {
  color: hsl(var(--muted-foreground));
  font-size: 0.85rem;
  margin: 0;
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
  width: 100%;
  margin-top: 0;
  padding: 12px 16px;
  background: hsl(var(--destructive) / 0.1);
  border: 1px solid hsl(var(--destructive) / 0.3);
  border-radius: 10px;
  color: hsl(var(--destructive));
  font-size: 0.875rem;
  text-align: center;
}

@media (max-width: 520px) {
  .steps-col {
    display: none;
  }

  .cards-col {
    width: 100%;
  }
}
</style>
