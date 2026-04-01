<script setup lang="ts">
import { ref, computed } from 'vue'
import { X, Check, ChevronDown } from 'lucide-vue-next'
import { useTrainingStore } from '@/stores/training'
import Button from '@/components/ui/button/Button.vue'

defineProps<{ modelValue: boolean }>()
const emit = defineEmits<{ 'update:modelValue': [value: boolean] }>()
const store = useTrainingStore()

const form = ref({
  employeeId: null as number | null,
  type: '',
  completed: '',
  expires: '',
  status: 'Fullført' as 'Fullført' | 'Ikke-fullført',
})

const errors = ref<Record<string, string>>({})

const employees = computed(() =>
  store.employees ?? []
)

function validate(): boolean {
  const e: Record<string, string> = {}
  if (!form.value.employeeId) e.employeeId = 'Velg en ansatt'
  if (!form.value.type.trim()) e.type = 'Opplæringstype er påkrevd'
  errors.value = e
  return Object.keys(e).length === 0
}

function close(): void {
  emit('update:modelValue', false)
  setTimeout(reset, 300)
}

function reset(): void {
  form.value = { employeeId: null, type: '', completed: '', expires: '', status: 'Fullført' }
  errors.value = {}
}

function save(): void {
  if (!validate()) return
  store.addTraining(form.value.employeeId!, {
    type: form.value.type.trim(),
    completed: form.value.completed || null,
    expires: form.value.expires || null,
    status: form.value.status === 'Ikke-fullført' ? 'Mangler' : 'Gyldig',
  })
  close()
}
</script>

<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="modelValue" class="overlay" @click.self="close">
        <div class="modal">
          <div class="modal-header">
            <div class="modal-header-left">
              <div class="modal-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M12 5v14M5 12h14"/>
                </svg>
              </div>
              <div>
                <h2 class="modal-title">Registrer opplæring</h2>
                <p class="modal-sub">Legg til ny opplæring for en ansatt</p>
              </div>
            </div>
            <Button variant="ghost" size="icon-sm" class="close-btn" @click="close" aria-label="Lukk">
              <X :size="16" />
            </Button>
          </div>

          <div class="modal-body">
            <div class="field-group" :class="{ 'has-error': errors.employeeId }">
              <label class="field-label">Ansatt <span class="required">*</span></label>
              <div class="select-wrapper">
                <select v-model="form.employeeId" class="form-input form-select">
                  <option :value="null" disabled>Velg ansatt…</option>
                  <option v-for="emp in employees" :key="emp.id" :value="emp.id">
                    {{ emp.name }}
                  </option>
                </select>
                <ChevronDown :size="14" class="select-icon" />
              </div>
              <span v-if="errors.employeeId" class="error-msg">{{ errors.employeeId }}</span>
            </div>
            <div class="field-group" :class="{ 'has-error': errors.type }">
              <label class="field-label">Opplæringstype <span class="required">*</span></label>
              <input
                v-model="form.type"
                class="form-input"
                placeholder="F.eks. Brannvern, HMS, Førstehjelp…"
                @input="delete errors.type"
              />
              <span v-if="errors.type" class="error-msg">{{ errors.type }}</span>
            </div>
            <div class="field-row">
              <div class="field-group">
                <label class="field-label">Fullført dato</label>
                <input v-model="form.completed" class="form-input" placeholder="dd.mm.åååå" />
              </div>
              <div class="field-group">
                <label class="field-label">Utløpsdato</label>
                <input v-model="form.expires" class="form-input" placeholder="dd.mm.åååå" />
              </div>
            </div>
            <div class="field-group">
              <label class="field-label">Status</label>
              <div class="status-pills">
                <button
                  v-for="s in ['Fullført', 'Ikke-fullført']"
                  :key="s"
                  type="button"
                  :class="['status-pill', `pill-${s === 'Ikke-fullført' ? 'ikke-fullfort' : 'fullfort'}`, { active: form.status === s }]"
                  @click="form.status = s as typeof form.status"
                >
                  <span class="pill-dot" />
                  {{ s }}
                </button>
              </div>
            </div>

          </div>

          <div class="modal-footer">
            <p class="required-note"><span class="required">*</span> Påkrevde felt</p>
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
  max-width: 460px;
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
  gap: 12px;
}

.modal-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
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

.modal-sub {
  font-size: 0.75rem;
  color: #9ca3af;
  margin: 1px 0 0;
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

.required { color: #7c3aed; }

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
.form-input::placeholder { color: #d1d5db; }

.has-error .form-input {
  border-color: #fca5a5;
  background: #fff5f5;
}
.has-error .form-input:focus {
  border-color: #ef4444;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.08);
}

.error-msg {
  font-size: 0.72rem;
  color: #ef4444;
  font-weight: 500;
}

.select-wrapper {
  position: relative;
}
.form-select {
  appearance: none;
  padding-right: 32px;
  cursor: pointer;
}
.select-icon {
  position: absolute;
  right: 11px;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
  pointer-events: none;
}

.status-pills {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 500;
  cursor: pointer;
  border: 1.5px solid transparent;
  background: #f3f4f6;
  color: #6b7280;
  transition: all 0.14s;
  font-family: inherit;
}

.pill-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: currentColor;
  opacity: 0.5;
  transition: opacity 0.14s;
}

.pill-fullfort { color: #059669; }
.pill-fullfort:hover { background: #ecfdf5; border-color: #a7f3d0; }
.pill-fullfort.active { background: #ecfdf5; border-color: #6ee7b7; color: #059669; }
.pill-fullfort.active .pill-dot { opacity: 1; }

.pill-ikke-fullfort { color: #dc2626; }
.pill-ikke-fullfort:hover { background: #fff5f5; border-color: #fca5a5; }
.pill-ikke-fullfort.active { background: #fff5f5; border-color: #fca5a5; color: #dc2626; }
.pill-ikke-fullfort.active .pill-dot { opacity: 1; }

.modal-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px 20px;
  border-top: 1px solid #f0eeee;
}

.required-note {
  font-size: 0.75rem;
  color: #9ca3af;
  margin: 0;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.modal-enter-active,
.modal-leave-active { transition: opacity 0.2s ease; }
.modal-enter-active .modal,
.modal-leave-active .modal { transition: transform 0.22s cubic-bezier(0.34, 1.3, 0.64, 1), opacity 0.2s ease; }
.modal-enter-from,
.modal-leave-to { opacity: 0; }
.modal-enter-from .modal { transform: scale(0.96) translateY(8px); opacity: 0; }
.modal-leave-to .modal { transform: scale(0.96) translateY(4px); opacity: 0; }

</style>
