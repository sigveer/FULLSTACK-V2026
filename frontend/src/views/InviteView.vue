<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useForm } from '@tanstack/vue-form'
import { z } from 'zod'
import { Eye, EyeOff, Lock, User, Phone } from 'lucide-vue-next'
import Button from '@/components/ui/button/Button.vue'
import InputGroup from '@/components/ui/input-group/InputGroup.vue'
import InputGroupAddon from '@/components/ui/input-group/InputGroupAddon.vue'
import InputGroupInput from '@/components/ui/input-group/InputGroupInput.vue'
import InputGroupButton from '@/components/ui/input-group/InputGroupButton.vue'
import { useAuthStore } from '@/stores/auth'
import api from '@/lib/api'
import type { AuthResponse } from '@/types/auth'
import type { AxiosError } from 'axios'

type ApiError = AxiosError<{ error: { message: string } }>

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const token = route.query.token as string | undefined

const loading = ref(true)
const error = ref('')
const submitError = ref('')
const submitting = ref(false)
const showPassword = ref(false)

const inviteEmail = ref('')
const orgName = ref('')
const existingUser = ref(false)

onMounted(async () => {
  if (!token) {
    error.value = 'Ingen invitasjonslenke funnet.'
    loading.value = false
    return
  }

  try {
    const { data } = await api.get(`/invitations/${token}`)
    inviteEmail.value = data.email
    orgName.value = data.organizationName
    existingUser.value = data.existingUser
  } catch (e) {
    error.value = (e as ApiError)?.response?.data?.error?.message ?? 'Invitasjonen er ugyldig eller utløpt.'
  } finally {
    loading.value = false
  }
})

const nameSchema = z.string().min(1, 'Påkrevd')
const phoneSchema = z.string().min(1, 'Telefonnummer er påkrevd').min(8, 'Ugyldig telefonnummer')
const passwordSchema = z.string().min(1, 'Passord er påkrevd').min(8, 'Passord må være minst 8 tegn')

const form = useForm({
  defaultValues: {
    fornavn: '',
    etternavn: '',
    phoneNumber: '',
    password: '',
  },
  onSubmit: async ({ value }) => {
    submitting.value = true
    submitError.value = ''
    try {
      const payload: Record<string, string> = {}

      if (!existingUser.value) {
        payload.fullName = `${value.fornavn} ${value.etternavn}`.trim()
        payload.phoneNumber = value.phoneNumber
        payload.password = value.password
      } else {
        payload.password = value.password
      }

      const { data } = await api.post<AuthResponse>(`/invitations/${token}/accept`, payload)
      auth.setAuth(data)
      router.push('/')
    } catch (e) {
      submitError.value = (e as ApiError)?.response?.data?.error?.message ?? 'Noe gikk galt. Prøv igjen.'
    } finally {
      submitting.value = false
    }
  },
})
</script>

<template>
  <div class="container">
    <div class="cards-col">
      <!-- Loading -->
      <div v-if="loading" class="card">
        <p class="loading-text">Laster invitasjon...</p>
      </div>

      <!-- Error (invalid/expired token) -->
      <div v-else-if="error" class="card">
        <h2>Ugyldig invitasjon</h2>
        <p class="subtitle">{{ error }}</p>
        <div class="buttons">
          <RouterLink to="/login" custom v-slot="{ navigate }">
            <Button @click="navigate" role="link" type="button">Gå til innlogging</Button>
          </RouterLink>
        </div>
      </div>

      <!-- Valid invite -->
      <template v-else>
        <div class="card">
          <div>
            <p class="step">INVITASJON</p>
            <h2>{{ orgName }}</h2>
            <p class="subtitle">
              Du er invitert til <strong>{{ orgName }}</strong> som <strong>{{ inviteEmail }}</strong>.
              <template v-if="existingUser">
                Bekreft passordet ditt for å godta invitasjonen.
              </template>
              <template v-else>
                Opprett en konto for å komme i gang.
              </template>
            </p>
          </div>

          <form @submit.prevent.stop="form.handleSubmit()" novalidate>
            <div class="fields">
              <!-- New user fields -->
              <template v-if="!existingUser">
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
              </template>

              <!-- Password (both new and existing users) -->
              <div>
                <form.Field name="password" :validators="{
                  onBlur: ({ value }: { value: string }) => {
                    const result = passwordSchema.safeParse(value)
                    if (result.success) return undefined
                    return result.error.issues[0]?.message
                  },
                }">
                  <template v-slot="{ field, state }">
                    <label>{{ existingUser ? 'Bekreft passord' : 'Passord' }}</label>
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

            <div class="buttons">
              <Button type="submit" :disabled="submitting">
                {{ submitting ? 'Godtar...' : 'Godta invitasjon' }}
              </Button>
            </div>
          </form>
        </div>

        <div v-if="submitError" class="error-card">
          {{ submitError }}
        </div>
      </template>
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

h2 {
  color: hsl(var(--primary));
  text-align: center;
  margin-bottom: 8px;
}

.subtitle {
  text-align: center;
  color: hsl(var(--muted-foreground));
  margin-bottom: 20px;
  font-size: 0.9rem;
  line-height: 1.5;
}

.loading-text {
  text-align: center;
  color: hsl(var(--muted-foreground));
  padding: 2rem 0;
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

.buttons {
  gap: 4px;
  display: flex;
  flex-direction: column;
  width: 100%;
}

.error-card {
  width: 100%;
  padding: 12px 16px;
  background: hsl(var(--destructive) / 0.1);
  border: 1px solid hsl(var(--destructive) / 0.3);
  border-radius: 10px;
  color: hsl(var(--destructive));
  font-size: 0.875rem;
  text-align: center;
}

@media (max-width: 520px) {
  .cards-col {
    width: 100%;
  }

  .two-col {
    grid-template-columns: 1fr;
  }
}
</style>
