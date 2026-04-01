<script setup lang="ts">
import { X } from 'lucide-vue-next'

const props = defineProps<{
  types: string[]
  modelType: string
  modelStatus: string
}>()

const emit = defineEmits<{
  'update:modelType':   [value: string]
  'update:modelStatus': [value: string]
}>()

const statuses = ['Gyldig', 'Utløper snart', 'Mangler'] as const

function toggleType(val: string): void {
  emit('update:modelType', props.modelType === val ? '' : val)
}

function clearAll(): void {
  emit('update:modelType', '')
  emit('update:modelStatus', '')
}
</script>

<template>
  <div class="filter-bar">
    <div class="filter-group">
      <span class="filter-label">Type</span>
      <button
        v-for="t in types"
        :key="t"
        class="pill"
        :class="{ 'pill-active': modelType === t }"
        @click="toggleType(t)"
      >
        {{ t }}
      </button>
    </div>

    <div class="divider" />
    <div class="filter-group">
      <span class="filter-label">Status</span>
      <button
        v-for="s in statuses"
        :key="s"
        class="pill"
        :class="{
          'pill-active':         modelStatus === s,
          'pill-green':          s === 'Gyldig'        && modelStatus === s,
          'pill-amber':          s === 'Utløper snart' && modelStatus === s,
          'pill-red':            s === 'Mangler'       && modelStatus === s,
          'pill-outline-green':  s === 'Gyldig'        && modelStatus !== s,
          'pill-outline-amber':  s === 'Utløper snart' && modelStatus !== s,
          'pill-outline-red':    s === 'Mangler'       && modelStatus !== s,
        }"
      >
        <span class="pill-dot"
              :class="{
            'dot-green': s === 'Gyldig',
            'dot-amber': s === 'Utløper snart',
            'dot-red':   s === 'Mangler',
          }"
        />
        {{ s }}
      </button>
    </div>

    <button
      v-if="modelType || modelStatus"
      class="clear-btn"
      @click="clearAll"
    >
      <X :size="13" /> Fjern filter
    </button>

  </div>
</template>

<style scoped>
.filter-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
  padding: 10px 14px;
  background: #fff;
  border: 1px solid #e7e5e4;
  border-radius: 14px;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.filter-label {
  font-size: 0.72rem;
  font-weight: 600;
  color: #9ca3af;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  margin-right: 2px;
  white-space: nowrap;
}

.divider {
  width: 1px;
  height: 20px;
  background: #e7e5e4;
  margin: 0 4px;
}

.pill {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 4px 12px;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 500;
  cursor: pointer;
  border: 1.5px solid #e7e5e4;
  background: #fafaf9;
  color: #6b7280;
  transition: all 0.15s ease;
  white-space: nowrap;
}
.pill:hover {
  background: #f0fdf4;
  border-color: #a7f3d0;
  color: #065f46;
}

.pill-active {
  background: #ecfdf5;
  border-color: #6ee7b7;
  color: #065f46;
  font-weight: 600;
}

.pill-outline-green { color: #059669; border-color: #d1fae5; background: #f0fdf4; }
.pill-outline-amber { color: #d97706; border-color: #fde68a; background: #fffbeb; }
.pill-outline-red   { color: #dc2626; border-color: #fecdd3; background: #fff1f2; }

.pill-outline-green:hover { background: #d1fae5; border-color: #6ee7b7; }
.pill-outline-amber:hover { background: #fde68a; border-color: #fcd34d; }
.pill-outline-red:hover   { background: #fecdd3; border-color: #fca5a5; }

.pill-green { background: #059669; border-color: #059669; color: #fff; font-weight: 600; }
.pill-amber { background: #d97706; border-color: #d97706; color: #fff; font-weight: 600; }
.pill-red   { background: #dc2626; border-color: #dc2626; color: #fff; font-weight: 600; }

.pill-green:hover { background: #047857; }
.pill-amber:hover { background: #b45309; }
.pill-red:hover   { background: #b91c1c; }

.pill-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  flex-shrink: 0;
}
.dot-green { background: #059669; }
.dot-amber { background: #d97706; }
.dot-red   { background: #dc2626; }

.pill-green .pill-dot,
.pill-amber .pill-dot,
.pill-red   .pill-dot { background: rgba(255,255,255,0.7); }

.clear-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin-left: auto;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 0.78rem;
  font-weight: 500;
  color: #9ca3af;
  background: transparent;
  border: 1.5px solid #e7e5e4;
  cursor: pointer;
  transition: all 0.15s;
}
.clear-btn:hover {
  color: #dc2626;
  border-color: #fca5a5;
  background: #fff1f2;
}
</style>
