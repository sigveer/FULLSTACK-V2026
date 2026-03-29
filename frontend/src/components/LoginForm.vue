<script setup lang="ts">
import { useForm } from '@tanstack/vue-form'
import { z } from 'zod'
import { useLogin, useSelectOrg } from '@/composables/useAuth'
import { useRouter } from 'vue-router'
import { ref } from 'vue'
import { Eye, EyeOff, Mail, Lock } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import Button from '@/components/ui/button/Button.vue'
import InputGroup from '@/components/ui/input-group/InputGroup.vue'
import InputGroupAddon from '@/components/ui/input-group/InputGroupAddon.vue'
import InputGroupInput from '@/components/ui/input-group/InputGroupInput.vue'
import InputGroupButton from '@/components/ui/input-group/InputGroupButton.vue'

const router = useRouter()
const auth = useAuthStore()
const login = useLogin()
const selectOrg = useSelectOrg()
const showPassword = ref(false)

const emailSchema = z.string().min(1, 'E-post er påkrevd').email('Ugyldig e-postadresse')

const form = useForm({
  defaultValues: {
    email: '',
    password: '',
  },
  onSubmit: async ({ value }) => {
    await login.mutateAsync(value)

    const firstMembership = auth.memberships[0]
    if (!firstMembership) {
      router.push('/create-org')
      return
    }

    await selectOrg.mutateAsync({ organizationId: firstMembership.organizationId })
    router.push('/')
  },
})

defineExpose({ loginError: login.error })
</script>

<template>
  <form
    @submit.prevent.stop="form.handleSubmit()"
    novalidate
  >
    <div class="fields">
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
                placeholder="leder@everest.no"
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
          onBlur: ({ value }: { value: string }) => !value ? 'Passord er påkrevd' : undefined,
        }">
          <template v-slot="{ field, state }">
            <label>Passord</label>
            <InputGroup :class="state.meta.errors.length ? 'input-group--error' : ''">
              <InputGroupAddon>
                <Lock />
              </InputGroupAddon>
              <InputGroupInput
                :type="showPassword ? 'text' : 'password'"
                placeholder="********"
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
      <Button type="submit" :disabled="login.isPending.value">
        {{ login.isPending.value ? 'Logger inn...' : 'Logg inn' }}
      </Button>

      <div class="signuplink">
        <p class="subtitle">
          Har du ikke en konto?
        </p>
        <RouterLink to="/signup" custom v-slot="{ navigate }">
          <Button @click="navigate" role="link" variant="link" type="button">Registrer deg</Button>
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

label {
  display: block;
  margin-bottom: 6px;
  font-size: 0.875rem;
  font-weight: 600;
  color: hsl(var(--foreground));
}

form > :deep(.btn) {
  width: 100%;
}

form > :deep(.btn--link) {
  margin-top: 0;
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

.signuplink {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
}

.subtitle {
  font-size: 14px;
}
</style>
