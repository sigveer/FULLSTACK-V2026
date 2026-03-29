<script setup lang="ts">
import { useForm } from '@tanstack/vue-form'
import { z } from 'zod'
import { Building2, Mail, Phone, Hash, ChevronDown } from 'lucide-vue-next'
import { useRouter } from 'vue-router'
import { toast } from 'vue-sonner'
import { useCreateOrg } from '@/composables/useCreateOrg'
import { useSelectOrg } from '@/composables/useAuth'
import Button from '@/components/ui/button/Button.vue'
import InputGroup from '@/components/ui/input-group/InputGroup.vue'
import InputGroupAddon from '@/components/ui/input-group/InputGroupAddon.vue'
import InputGroupInput from '@/components/ui/input-group/InputGroupInput.vue'

const router = useRouter()
const createOrg = useCreateOrg()
const selectOrg = useSelectOrg()

const nameSchema = z.string().min(1, 'Virksomhetsnavn er påkrevd').max(200, 'Maks 200 tegn')
const emailSchema = z.string().min(1, 'E-post er påkrevd').email('Ugyldig e-postadresse')
const phoneSchema = z.string().min(1, 'Telefonnummer er påkrevd').min(8, 'Ugyldig telefonnummer')
const orgNrSchema = z.string()
  .min(1, 'Organisasjonsnummer er påkrevd')
  .regex(/^\d{9}$/, 'Organisasjonsnummer må være nøyaktig 9 siffer')

const form = useForm({
  defaultValues: {
    businessName: '',
    businessEmail: '',
    phoneNumber: '',
    orgNumber: '',
    businessType: '',
  },
  onSubmit: async ({ value }) => {
    try {
      const org = await createOrg.mutateAsync({
        name: value.businessName,
        orgNumber: value.orgNumber || undefined,
      })
      await selectOrg.mutateAsync({ organizationId: org.id })
      router.push('/')
    } catch (e: any) {
      if (e.response?.status === 409) {
        toast.error('En virksomhet med dette navnet finnes allerede')
      } else {
        toast.error('Kunne ikke opprette virksomhet')
      }
    }
  },
})

defineExpose({ createOrgError: createOrg.error })
</script>

<template>
  <form @submit.prevent.stop="form.handleSubmit()" novalidate>
    <div class="fields">
      <div>
        <form.Field name="businessName" :validators="{
          onBlur: ({ value }: { value: string }) => {
            const result = nameSchema.safeParse(value)
            if (result.success) return undefined
            return result.error.issues[0]?.message
          },
        }">
          <template v-slot="{ field, state }">
            <label>Virksomhetsnavn</label>
            <InputGroup :class="state.meta.errors.length ? 'input-group--error' : ''">
              <InputGroupAddon>
                <Building2 />
              </InputGroupAddon>
              <InputGroupInput
                type="text"
                placeholder="Eksempel AS"
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
        <form.Field name="businessEmail" :validators="{
          onBlur: ({ value }: { value: string }) => {
            const result = emailSchema.safeParse(value)
            if (result.success) return undefined
            return result.error.issues[0]?.message
          },
        }">
          <template v-slot="{ field, state }">
            <label>Virksomhetsmail</label>
            <InputGroup :class="state.meta.errors.length ? 'input-group--error' : ''">
              <InputGroupAddon>
                <Mail />
              </InputGroupAddon>
              <InputGroupInput
                type="email"
                placeholder="kontakt@eksempel.no"
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
        <form.Field name="orgNumber" :validators="{
          onBlur: ({ value }: { value: string }) => {
            const result = orgNrSchema.safeParse(value)
            if (result.success) return undefined
            return result.error.issues[0]?.message
          },
        }">
          <template v-slot="{ field, state }">
            <label>Org nr / id</label>
            <InputGroup :class="state.meta.errors.length ? 'input-group--error' : ''">
              <InputGroupAddon>
                <Hash />
              </InputGroupAddon>
              <InputGroupInput
                type="text"
                placeholder="123456789"
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
        <form.Field name="businessType" :validators="{
          onBlur: ({ value }: { value: string }) => !value ? 'Velg type virksomhet' : undefined,
        }">
          <template v-slot="{ field, state }">
            <label>Type virksomhet</label>
            <div class="select-wrap">
              <select
                :value="field.state.value"
                @change="field.handleChange(($event.target as HTMLSelectElement).value)"
                @blur="field.handleBlur"
                :class="['select-field', state.meta.errors.length ? 'select-field--error' : '']"
              >
                <option value="" disabled>Velg type</option>
                <option value="restaurant">Restaurant</option>
                <option value="kafe">Kafé</option>
                <option value="annet">Annet</option>
              </select>
              <ChevronDown class="select-arrow" />
            </div>
            <p v-if="state.meta.errors.length" class="error">{{ state.meta.errors[0] }}</p>
          </template>
        </form.Field>
      </div>
    </div>

    <div class="buttons">
      <Button type="submit" :disabled="createOrg.isPending.value">
        {{ createOrg.isPending.value ? 'Oppretter...' : 'Opprett' }}
      </Button>
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

.error {
  color: hsl(var(--destructive));
  font-size: 0.8rem;
  margin-top: 5px;
}

:deep(.input-group--error) {
  border-color: hsl(var(--destructive));
}

.select-wrap {
  position: relative;
}

.select-field {
  width: 100%;
  height: 2.25rem;
  border: 1px solid hsl(var(--input, 35 15% 85%));
  border-radius: 0.5rem;
  background-color: hsl(var(--card, 40 25% 98%));
  font-size: 0.875rem;
  color: hsl(var(--foreground));
  padding: 0 36px 0 12px;
  outline: none;
  cursor: pointer;
  appearance: none;
  -webkit-appearance: none;
  box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05);
  transition: border-color 150ms ease, box-shadow 150ms ease;
}

.select-field:focus {
  border-color: hsl(var(--primary, 245 43% 52%) / 0.5);
  box-shadow: 0 0 0 2px hsl(var(--ring, 245 43% 52%) / 0.2);
}

.select-field--error {
  border-color: hsl(var(--destructive));
}

.select-arrow {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  width: 16px;
  height: 16px;
  color: hsl(var(--muted-foreground));
  pointer-events: none;
}

.buttons {
  gap: 4px;
  display: flex;
  flex-direction: column;
  width: 100%;
}
</style>
