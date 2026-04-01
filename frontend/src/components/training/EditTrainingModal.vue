<script setup lang="ts">
import { ref, watch } from 'vue'
import { X, Check, Trash2 } from 'lucide-vue-next'
import type { Training } from '@/stores/training'
import { useTrainingStore } from '@/stores/training'
import Button from '@/components/ui/button/Button.vue'

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
    <Transition name="modal">
      <div
        v-if="modelValue"
        class="overlay"
        @click.self="close"
      >
        <div class="modal">
          <div class="modal-header">
            <div class="modal-header-left">
              <div class="modal-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                  <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                </svg>
              </div>
              <h2 class="modal-title">Rediger opplæring</h2>
            </div>
            <Button variant="ghost" size="icon-sm" class="close-btn" @click="close" aria-label="Lukk">
              <X :size="16" />
            </Button>
          </div>

          <div class="modal-body">
            <div class="field-group">
              <label class="field-label">Opplæringstype</label>
              <input v-model="form.type" class="form-input" placeholder="Skriv inn type..." />
            </div>
            <div class="field-row">
              <div class="field-group">
                <label class="field-label">Fullført dato</label>
                <input v-model="form.completed" placeholder="dd.mm.åååå" class="form-input" />
              </div>
              <div class="field-group">
                <label class="field-label">Utløpsdato</label>
                <input v-model="form.expires" placeholder="dd.mm.åååå" class="form-input" />
              </div>
            </div>
            <div class="field-group">
              <label class="field-label">Status</label>
              <select v-model="form.status" class="form-input form-select">
                <option>Gyldig</option>
                <option>Utløper snart</option>
                <option>Mangler</option>
              </select>
            </div>
          </div>

          <div class="modal-footer">
            <div class="footer-left">
              <button
                v-if="!deleteConfirm"
                class="delete-btn"
                @click="deleteConfirm = true"
              >
                <Trash2 :size="13" /> Slett
              </button>
              <div v-else class="delete-confirm">
                <span class="delete-confirm-text">Er du sikker?</span>
                <button class="confirm-yes" @click="remove">Ja, slett</button>
                <button class="confirm-no" @click="deleteConfirm = false">Nei</button>
              </div>
            </div>
            <div class="footer-right">
              <Button variant="destructive" size="sm" @click="close">Avbryt</Button>
              <Button variant="default" size="sm" @click="save">
                <Check :size="14" /> Registrer
              </Button>
            </div>
          </div>

        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  backdrop-filter: blur(2px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 50;
  padding: 16px;
}

.modal {
  background: #ffffff;
  border-radius: 20px;
  width: 100%;
  max-width: 440px;
  box-shadow:
    0 0 0 1px rgba(0,0,0,0.06),
    0 8px 24px rgba(0,0,0,0.10),
    0 24px 64px rgba(0,0,0,0.12);
  overflow: hidden;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px 18px;
  border-bottom: 1px solid #f0eeee;
}

.modal-header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.modal-icon {
  width: 32px;
  height: 32px;
  border-radius: 9px;
  background: #f5f3ff;
  color: #7c3aed;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.modal-title {
  font-size: 0.95rem;
  font-weight: 700;
  color: #111827;
  margin: 0;
  letter-spacing: -0.01em;
}

.modal-body {
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.field-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: 0.72rem;
  font-weight: 600;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.form-input {
  border: 1.5px solid #e5e7eb;
  border-radius: 10px;
  padding: 9px 12px;
  font-size: 0.875rem;
  color: #1f2937;
  background: #fafafa;
  outline: none;
  transition: border-color 0.15s, background 0.15s, box-shadow 0.15s;
  width: 100%;
  box-sizing: border-box;
  font-family: inherit;
}

.form-input:focus {
  border-color: #7c3aed;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.08);
}

.form-input::placeholder {
  color: #d1d5db;
}

.form-select {
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%239ca3af' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  padding-right: 32px;
  cursor: pointer;
}

.modal-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px 20px;
  border-top: 1px solid #f0eeee;
  gap: 8px;
}

.footer-left { display: flex; align-items: center; }
.footer-right { display: flex; align-items: center; gap: 8px; }

.delete-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 0.8rem;
  font-weight: 500;
  color: #ef4444;
  background: transparent;
  border: 1.5px solid #fecaca;
  border-radius: 9px;
  padding: 7px 12px;
  cursor: pointer;
  transition: background 0.12s, border-color 0.12s;
}
.delete-btn:hover {
  background: #fff5f5;
  border-color: #fca5a5;
}

.delete-confirm {
  display: flex;
  align-items: center;
  gap: 8px;
}
.delete-confirm-text {
  font-size: 0.8rem;
  color: #ef4444;
  font-weight: 500;
}
.confirm-yes {
  font-size: 0.78rem;
  font-weight: 600;
  background: #ef4444;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 6px 10px;
  cursor: pointer;
  transition: background 0.12s;
}
.confirm-yes:hover { background: #dc2626; }

.confirm-no {
  font-size: 0.78rem;
  font-weight: 500;
  background: transparent;
  color: #6b7280;
  border: 1.5px solid #e5e7eb;
  border-radius: 8px;
  padding: 5px 10px;
  cursor: pointer;
  transition: background 0.12s;
}
.confirm-no:hover { background: #f9fafb; }

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.2s ease;
}
.modal-enter-active .modal,
.modal-leave-active .modal {
  transition: transform 0.22s cubic-bezier(0.34, 1.3, 0.64, 1), opacity 0.2s ease;
}
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
.modal-enter-from .modal {
  transform: scale(0.96) translateY(8px);
  opacity: 0;
}
.modal-leave-to .modal {
  transform: scale(0.96) translateY(4px);
  opacity: 0;
}
</style>
