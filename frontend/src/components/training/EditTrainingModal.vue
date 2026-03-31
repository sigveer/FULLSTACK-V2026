<script setup lang="ts">
import { ref, watch } from 'vue'
import { X, Check, Trash2 } from 'lucide-vue-next'
import type { Training } from '@/stores/training'
import { useTrainingStore } from '@/stores/training'

const props = defineProps<{
  modelValue: boolean
  training: Training | null
  employeeId: number | undefined
}>()
const emit = defineEmits<{ 'update:modelValue': [value: boolean] }>()

const store = useTrainingStore()
const form  = ref<Omit<Training, 'id'>>({ type: '', completed: null, expires: null, status: 'Gyldig' })
const deleteConfirm = ref(false)

watch(() => props.training, (t) => {
  if (t) {
    form.value = { type: t.type, completed: t.completed, expires: t.expires, status: t.status }
    deleteConfirm.value = false
  }
}, { immediate: true })

function close(): void { emit('update:modelValue', false) }

function save(): void {
  if (!props.training || !props.employeeId) return
  store.updateTraining(props.employeeId, props.training.id, form.value)
  close()
}

function remove(): void {
  if (!props.training || !props.employeeId) return
  store.deleteTraining(props.employeeId, props.training.id)
  close()
}
</script>

<template>
  <Teleport to="body">
    <div v-if="modelValue" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4" @click.self="close">
      <div class="bg-white rounded-2xl w-full max-w-md shadow-2xl overflow-hidden">

        <div class="flex items-center justify-between px-6 py-5 border-b border-stone-100">
          <h2 class="text-lg font-bold text-gray-900">Rediger opplæring</h2>
          <button class="p-1.5 rounded-lg hover:bg-stone-100 text-gray-400 hover:text-gray-700 transition-colors" @click="close">
            <X :size="17" />
          </button>
        </div>

        <div class="px-6 py-5 flex flex-col gap-4">
          <label class="field-label">
            Opplæringstype
            <input v-model="form.type" class="form-input" />
          </label>
          <label class="field-label">
            Fullført dato
            <input v-model="form.completed" placeholder="dd.mm.åååå" class="form-input" />
          </label>
          <label class="field-label">
            Utløpsdato
            <input v-model="form.expires" placeholder="dd.mm.åååå" class="form-input" />
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

        <div class="flex items-center justify-between px-6 py-4 border-t border-stone-100">
          <div>
            <button
              v-if="!deleteConfirm"
              class="flex items-center gap-1.5 text-sm text-red-600 border border-red-200 rounded-lg px-3 py-2 hover:bg-red-50 transition-colors"
              @click="deleteConfirm = true"
            >
              <Trash2 :size="14" /> Slett
            </button>
            <div v-else class="flex items-center gap-2 text-sm text-red-600">
              <span>Er du sikker?</span>
              <button class="bg-red-600 text-white rounded-lg px-3 py-1.5 text-xs hover:bg-red-700 transition-colors" @click="remove">Ja</button>
              <button class="border border-stone-200 rounded-lg px-3 py-1.5 text-xs text-gray-500 hover:bg-stone-50 transition-colors" @click="deleteConfirm = false">Nei</button>
            </div>
          </div>
          <div class="flex gap-2">
            <button class="border border-stone-200 rounded-lg px-4 py-2 text-sm text-gray-500 hover:bg-stone-50 transition-colors" @click="close">Avbryt</button>
            <button class="flex items-center gap-1.5 bg-emerald-700 text-white rounded-lg px-4 py-2 text-sm font-semibold hover:bg-emerald-800 transition-colors" @click="save">
              <Check :size="14" /> Lagre
            </button>
          </div>
        </div>

      </div>
    </div>
  </Teleport>
</template>

<style scoped>
.field-label { @apply flex flex-col gap-1.5 text-xs font-semibold text-gray-500 uppercase tracking-wide; }
.form-input  { @apply border border-stone-200 rounded-lg px-3 py-2 text-sm text-gray-800 bg-stone-50 outline-none focus:border-emerald-600 focus:bg-white transition-colors w-full font-normal normal-case tracking-normal; }
</style>
