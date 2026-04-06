<script setup lang="ts">
import { useForm } from '@tanstack/vue-form'
import { z } from 'zod'
import { useRegister } from '@/composables/useAuth'
import { useRouter } from 'vue-router'
import { ref } from 'vue'
import { Eye, EyeOff, Mail, Lock, User, Phone } from 'lucide-vue-next'
import Button from '@/components/ui/button/Button.vue'
import Checkbox from '@/components/ui/checkbox/Checkbox.vue'
import InputGroup from '@/components/ui/input-group/InputGroup.vue'
import InputGroupAddon from '@/components/ui/input-group/InputGroupAddon.vue'
import InputGroupInput from '@/components/ui/input-group/InputGroupInput.vue'
import InputGroupButton from '@/components/ui/input-group/InputGroupButton.vue'

const router = useRouter()
const register = useRegister()
const showPassword = ref(false)

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
    termsAccepted: false,
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

defineExpose({ registerError: register.error })
</script>

<template>
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

    <form.Field name="termsAccepted" :validators="{
      onChange: ({ value }: { value: boolean }) => !value ? 'Du må godta vilkårene' : undefined,
    }">
      <template v-slot="{ field, state }">
        <div class="terms-row" @click="field.handleChange(!field.state.value)">
          <Checkbox
            :checked="field.state.value"
            @update:checked="field.handleChange($event)"
            @click.stop
          />
          <span class="terms-text">
            Jeg godtar <a href="#" @click.stop>vilkårene</a> og
            <a href="#" @click.stop>personvernreglene</a> for Vera
          </span>
        </div>
        <p v-if="state.meta.errors.length" class="error">{{ state.meta.errors[0] }}</p>
      </template>
    </form.Field>

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
</template>

<style scoped>
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

@media (max-width: 520px) {
  .two-col {
    grid-template-columns: 1fr;
  }
}
</style>
