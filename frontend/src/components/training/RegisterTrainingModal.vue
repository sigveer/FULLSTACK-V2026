<script setup lang="ts">
import { ref } from 'vue'
import { X, Check } from 'lucide-vue-next'
import type { Training } from '@/stores/training'
import { useTrainingStore } from '@/stores/training'

defineProps<{ modelValue: boolean }>()
const emit = defineEmits<{ 'update:modelValue': [value: boolean] }>()

const store = useTrainingStore()

interface RegisterForm {
  employeeId: number | ''
  type: string
  completed: string
  expires: string
  status: Training['status']
}

const form = ref<RegisterForm>({ employeeId: '', type: '', completed: '', expires: '', status: 'Gyldig' })

function close(): void { emit('update:modelValue', false) }

function save(): void {
  if (!form.value.employeeId || !form.value.type) return
  store.addTraining(Number(form.value.employeeId), {
    type: form.value.type,
    completed: form.value.completed || null,
    expires: form.value.expires || null,
    status: form.value.status,
  })
  form.value = { employeeId: '', type: '', completed: '', expires: '', status: 'Gyldig' }
  close()
}
</script>

<template>
  <Teleport to="body">
    <div v-if="modelValue" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4" @click.self="close">
      <div class="bg-white rounded-2xl w-full max-w-md shadow-2xl overflow-hidden">

        <div class="flex items-center justify-between px-6 py-5 border-b border-stone-100">
          <h2 class="text-lg font-bold text-gray-900">Registrer opplæring</h2>
          <button class="p-1.5 rounded-lg hover:bg-stone-100 text-gray-400 hover:text-gray-700 transition-colors" @click="close">
            <X :size="17" />
          </button>
        </div>

        <div class="px-6 py-5 flex flex-col gap-4">
          <label class="field-label">
            Ansatt
            <select v-model="form.employeeId" class="form-input">
              <option value="" disabled>Velg ansatt</option>
              <option v-for="e in store.employees" :key="e.id" :value="e.id">{{ e.name }}</option>
            </select>
          </label>
          <label class="field-label">
            Opplæringstype
            <input v-model="form.type" class="form-input" placeholder="F.eks. Kunnskapsprøve alkohol" />
          </label>
          <label class="field-label">
            Fullført dato
            <input v-model="form.completed" class="form-input" placeholder="dd.mm.åååå" />
          </label>
          <label class="field-label">
            Utløpsdato
            <input v-model="form.expires" class="form-input" placeholder="dd.mm.åååå" />
          </label>
          <label class="field-label">
            Status
            <select v-model="form.status" class="form-input">
              <option>Gyldig</option>
              <option>Utløper snart</option>
              <option>Mangler</option>
            </select>
          </label>
        </div>

        <div class="flex justify-end gap-2 px-6 py-4 border-t border-stone-100">
          <button class="border border-stone-200 rounded-lg px-4 py-2 text-sm text-gray-500 hover:bg-stone-50 transition-colors" @click="close">Avbryt</button>
          <button class="flex items-center gap-1.5 bg-emerald-700 text-white rounded-lg px-4 py-2 text-sm font-semibold hover:bg-emerald-800 transition-colors" @click="save">
            <Check :size="14" /> Lagre
          </button>
        </div>

      </div>
    </div>
  </Teleport>
</template>

<style scoped>
.field-label { @apply flex flex-col gap-1.5 text-xs font-semibold text-gray-500 uppercase tracking-wide; }
.form-input  { @apply border border-stone-200 rounded-lg px-3 py-2 text-sm text-gray-800 bg-stone-50 outline-none focus:border-emerald-600 focus:bg-white transition-colors w-full font-normal normal-case tracking-normal; }
</style>
