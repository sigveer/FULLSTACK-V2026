<script setup lang="ts">
import { useForm } from '@tanstack/vue-form'
import { z } from 'zod'
import { useRegister } from '@/composables/useAuth'
import { useRouter } from 'vue-router'
import { ref } from 'vue'
import { Eye, EyeOff, Mail, Lock, User, Phone } from 'lucide-vue-next'
import Button from '@/components/ui/button/Button.vue'
import InputGroup from '@/components/ui/input-group/InputGroup.vue'
import InputGroupAddon from '@/components/ui/input-group/InputGroupAddon.vue'
import InputGroupInput from '@/components/ui/input-group/InputGroupInput.vue'
import InputGroupButton from '@/components/ui/input-group/InputGroupButton.vue'

const router = useRouter()
const register = useRegister()
const showPassword = ref(false)
const termsAccepted = ref(false)

const nameSchema = z.string().min(1, 'Påkrevd')
const emailSchema = z.string().min(1, 'E-post er påkrevd').email('Ugyldig e-postadresse')
const phoneSchema = z.string().min(1, 'Telefonnummer er påkrevd').min(8, 'Ugyldig telefonnummer')
const passwordSchema = z.string().min(1, 'Passord er påkrevd').min(8, 'Passord må være minst 8 tegn')

const form = useForm({
  defaultValues: {
    fornavn: '',
    etternavn: '',
    email: '',
    phoneNumber: '',
    password: '',
  },
  onSubmit: async ({ value }) => {
    await register.mutateAsync({
      fullName: `${value.fornavn} ${value.etternavn}`.trim(),
      email: value.email,
      phoneNumber: value.phoneNumber,
      password: value.password,
    })
    router.push('/select-org')
  },
})
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

          <form @submit.prevent.stop="form.handleSubmit()" novalidate>
            <div class="fields">
              <div class="two-col">
                <div>
                  <form.Field name="fornavn" :validators="{
                    onBlur: ({ value }: { value: string }) => {
                      const result = nameSchema.safeParse(value)
                      if (result.success) return undefined
                      return result.error.issues[0]?.message
                    },
                  }">
                    <template v-slot="{ field, state }">
                      <label>Fornavn</label>
                      <InputGroup :class="state.meta.errors.length ? 'input-group--error' : ''">
                        <InputGroupAddon>
                          <User />
                        </InputGroupAddon>
                        <InputGroupInput
                          type="text"
                          placeholder="Ola"
                          :model-value="field.state.value"
                          @update:model-value="field.handleChange(String($event))"
                          @blur="field.handleBlur"
                        />
                      </InputGroup>
                      <p v-if="state.meta.errors.length" class="error">{{ state.meta.errors[0] }}</p>
                    </template>
                  </form.Field>
                </div>
                <div>
                  <form.Field name="etternavn" :validators="{
                    onBlur: ({ value }: { value: string }) => {
                      const result = nameSchema.safeParse(value)
                      if (result.success) return undefined
                      return result.error.issues[0]?.message
                    },
                  }">
                    <template v-slot="{ field, state }">
                      <label>Etternavn</label>
                      <InputGroup :class="state.meta.errors.length ? 'input-group--error' : ''">
                        <InputGroupAddon>
                          <User />
                        </InputGroupAddon>
                        <InputGroupInput
                          type="text"
                          placeholder="Nordmann"
                          :model-value="field.state.value"
                          @update:model-value="field.handleChange(String($event))"
                          @blur="field.handleBlur"
                        />
                      </InputGroup>
                      <p v-if="state.meta.errors.length" class="error">{{ state.meta.errors[0] }}</p>
                    </template>
                  </form.Field>
                </div>
              </div>

              <div>
                <form.Field name="email" :validators="{
                  onBlur: ({ value }: { value: string }) => {
                    const result = emailSchema.safeParse(value)
                    if (result.success) return undefined
                    return result.error.issues[0]?.message
                  },
                }">
                  <template v-slot="{ field, state }">
                    <label>E-post</label>
                    <InputGroup :class="state.meta.errors.length ? 'input-group--error' : ''">
                      <InputGroupAddon>
                        <Mail />
                      </InputGroupAddon>
                      <InputGroupInput
                        type="email"
                        placeholder="ola@eksempel.no"
                        :model-value="field.state.value"
                        @update:model-value="field.handleChange(String($event))"
                        @blur="field.handleBlur"
                      />
                    </InputGroup>
                    <p v-if="state.meta.errors.length" class="error">{{ state.meta.errors[0] }}</p>
                  </template>
                </form.Field>
              </div>

              <div>
                <form.Field name="phoneNumber" :validators="{
                  onBlur: ({ value }: { value: string }) => {
                    const result = phoneSchema.safeParse(value)
                    if (result.success) return undefined
                    return result.error.issues[0]?.message
                  },
                }">
                  <template v-slot="{ field, state }">
                    <label>Telefonnummer</label>
                    <InputGroup :class="state.meta.errors.length ? 'input-group--error' : ''">
                      <InputGroupAddon>
                        <Phone />
                      </InputGroupAddon>
                      <InputGroupInput
                        type="tel"
                        placeholder="+47 000 00 000"
                        :model-value="field.state.value"
                        @update:model-value="field.handleChange(String($event))"
                        @blur="field.handleBlur"
                      />
                    </InputGroup>
                    <p v-if="state.meta.errors.length" class="error">{{ state.meta.errors[0] }}</p>
                  </template>
                </form.Field>
              </div>

              <div>
                <form.Field name="password" :validators="{
                  onBlur: ({ value }: { value: string }) => {
                    const result = passwordSchema.safeParse(value)
                    if (result.success) return undefined
                    return result.error.issues[0]?.message
                  },
                }">
                  <template v-slot="{ field, state }">
                    <label>Passord</label>
                    <InputGroup :class="state.meta.errors.length ? 'input-group--error' : ''">
                      <InputGroupAddon>
                        <Lock />
                      </InputGroupAddon>
                      <InputGroupInput
                        :type="showPassword ? 'text' : 'password'"
                        placeholder="Minst 8 tegn"
                        :model-value="field.state.value"
                        @update:model-value="field.handleChange(String($event))"
                        @blur="field.handleBlur"
                      />
                      <InputGroupAddon align="inline-end">
                        <InputGroupButton @click="showPassword = !showPassword">
                          <Eye v-if="!showPassword" />
                          <EyeOff v-else />
                        </InputGroupButton>
                      </InputGroupAddon>
                    </InputGroup>
                    <p v-if="state.meta.errors.length" class="error">{{ state.meta.errors[0] }}</p>
                  </template>
                </form.Field>
              </div>
            </div>

            <label class="terms-row">
              <input type="checkbox" v-model="termsAccepted" />
              <span class="terms-text">
                Jeg godtar <a href="#">vilkårene</a> og
                <a href="#">personvernreglene</a> for IK-Komplett
              </span>
            </label>

            <div class="buttons">
              <Button type="submit" :disabled="register.isPending.value">
                {{ register.isPending.value ? 'Oppretter konto...' : 'Opprett konto' }}
              </Button>

              <div class="loginlink">
                <p class="link-text">
                  Har du allerede konto?
                </p>
                <RouterLink to="/login" custom v-slot="{ navigate }">
                  <Button @click="navigate" role="link" variant="link" type="button">Logg inn</Button>
                </RouterLink>
              </div>
            </div>
          </form>
        </div>

        <div class="card card--dim">
          <p class="step">STEG 2: BEKREFTELSE</p>
          <h3 class="dim-title">Bekreft e-post</h3>
          <p class="dim-sub">Du mottar en bekreftelseslenke etter registrering.</p>
        </div>

        <div v-if="register.error.value" class="error-card">
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

form {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.fields {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.two-col > * {
  min-width: 0;
}

label {
  display: block;
  margin-bottom: 6px;
  font-size: 0.875rem;
  font-weight: 600;
  color: hsl(var(--foreground));
}

.error {
  color: hsl(var(--destructive));
  font-size: 0.8rem;
  margin-top: 5px;
}

:deep(.input-group--error) {
  border-color: hsl(var(--destructive));
}

.terms-row {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  cursor: pointer;
}

.terms-row input[type="checkbox"] {
  margin-top: 2px;
  accent-color: hsl(var(--primary));
  width: 14px;
  height: 14px;
  flex-shrink: 0;
  cursor: pointer;
}

.terms-text {
  font-size: 0.8rem;
  color: hsl(var(--muted-foreground));
  line-height: 1.55;
}

.terms-text a {
  color: hsl(var(--primary));
  text-decoration: none;
}

.terms-text a:hover {
  text-decoration: underline;
}

.buttons {
  gap: 4px;
  display: flex;
  flex-direction: column;
  width: 100%;
}

.loginlink {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 0;
}

.link-text {
  font-size: 14px;
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

  .two-col {
    grid-template-columns: 1fr;
  }
}
</style>
