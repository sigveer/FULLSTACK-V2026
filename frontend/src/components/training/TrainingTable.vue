<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ChevronDown, ChevronUp, MoreVertical, Pencil, Trash2 } from 'lucide-vue-next'
import type { TrainingRow } from '@/stores/training.ts'
import EmployeeAvatar from './EmployeeAvatar.vue'
import StatusBadge from './StatusBadge.vue'

defineProps<{
  groupedTrainings: Record<string, TrainingRow[]>
}>()

const emit = defineEmits<{
  edit:   [row: TrainingRow]
  delete: [row: TrainingRow]
}>()

const collapsed  = ref<Record<string, boolean>>({})
const openMenuId = ref<number | null>(null)

const toggle = (type: string): void => { collapsed.value[type] = !collapsed.value[type] }

function toggleMenu(id: number): void {
  openMenuId.value = openMenuId.value === id ? null : id
}

function handleEdit(row: TrainingRow): void {
  openMenuId.value = null
  emit('edit', row)
}

function handleDelete(row: TrainingRow): void {
  openMenuId.value = null
  emit('delete', row)
}

function onOutsideClick(e: MouseEvent): void {
  if (!(e.target as HTMLElement).closest('.ctx-wrap')) openMenuId.value = null
}

onMounted(()   => document.addEventListener('click', onOutsideClick))
onUnmounted(() => document.removeEventListener('click', onOutsideClick))
</script>

<template>
  <div class="bg-white border border-stone-200 rounded-2xl overflow-hidden">

    <div v-if="!Object.keys(groupedTrainings).length" class="py-16 text-center text-sm text-gray-400">
      Ingen resultater matcher filteret.
    </div>

    <div
      v-for="(rows, type) in groupedTrainings"
      :key="type"
      class="border-b border-stone-100 last:border-b-0"
    >
      <!-- Group header -->
      <button
        class="w-full flex items-center gap-3 px-5 py-3.5 bg-stone-50 hover:bg-stone-100 transition-colors text-left"
        @click="toggle(type)"
      >
        <span class="flex-1 text-sm font-semibold text-gray-700">{{ type }}</span>
        <span class="text-xs bg-stone-200 text-gray-500 rounded-full px-2.5 py-0.5">{{ rows.length }} ansatte</span>
        <component :is="collapsed[type] ? ChevronDown : ChevronUp" :size="15" class="text-gray-400" />
      </button>

      <!-- Table -->
      <div v-if="!collapsed[type]" class="overflow-x-auto">
        <table class="w-full border-collapse">
          <thead>
          <tr class="border-b border-stone-100">
            <th class="text-left text-xs font-semibold text-gray-400 px-5 py-2.5">Ansatt</th>
            <th class="text-left text-xs font-semibold text-gray-400 px-5 py-2.5 hidden md:table-cell">Opplæringstype</th>
            <th class="text-left text-xs font-semibold text-gray-400 px-5 py-2.5 hidden md:table-cell">Fullført</th>
            <th class="text-left text-xs font-semibold text-gray-400 px-5 py-2.5">Utløper</th>
            <th class="text-left text-xs font-semibold text-gray-400 px-5 py-2.5">Status</th>
            <th class="w-10 px-3 py-2.5"></th>
          </tr>
          </thead>
          <tbody>
          <tr
            v-for="row in rows"
            :key="row.id"
            class="border-b border-stone-50 hover:bg-stone-50/70 transition-colors last:border-b-0"
          >
            <td class="px-5 py-3.5">
              <div class="flex items-center gap-2.5">
                <EmployeeAvatar :initials="row.employee.initials" :color="row.employee.color" size="sm" />
                <div>
                  <p class="text-sm font-semibold text-gray-900">{{ row.employee.name }}</p>
                  <p class="text-xs text-gray-400">{{ row.employee.role }}</p>
                </div>
              </div>
            </td>
            <td class="px-5 py-3.5 text-sm text-gray-600 hidden md:table-cell">{{ row.type }}</td>
            <td class="px-5 py-3.5 text-sm text-gray-600 hidden md:table-cell">{{ row.completed ?? '—' }}</td>
            <td
              class="px-5 py-3.5 text-sm"
              :class="row.status === 'Utløper snart' ? 'text-amber-600 font-semibold' : 'text-gray-600'"
            >
              {{ row.expires ?? '—' }}
            </td>
            <td class="px-5 py-3.5"><StatusBadge :status="row.status" /></td>

            <!-- ⋮ context menu -->
            <td class="px-3 py-3.5">
              <div class="ctx-wrap relative flex justify-center">
                <button
                  :class="[
                      'w-7 h-7 rounded-lg flex items-center justify-center transition-colors',
                      openMenuId === row.id
                        ? 'bg-stone-100 text-gray-700'
                        : 'text-gray-400 hover:text-gray-700 hover:bg-stone-100'
                    ]"
                  @click.stop="toggleMenu(row.id)"
                  aria-label="Alternativer"
                >
                  <MoreVertical :size="16" />
                </button>

                <Transition
                  enter-active-class="transition ease-out duration-100"
                  enter-from-class="opacity-0 scale-95"
                  enter-to-class="opacity-100 scale-100"
                  leave-active-class="transition ease-in duration-75"
                  leave-from-class="opacity-100 scale-100"
                  leave-to-class="opacity-0 scale-95"
                >
                  <div
                    v-if="openMenuId === row.id"
                    class="absolute right-0 top-8 z-50 w-40 bg-white rounded-xl border border-stone-200 shadow-lg py-1 origin-top-right"
                  >
                    <button
                      class="w-full flex items-center gap-2.5 px-3.5 py-2 text-sm text-gray-700 hover:bg-stone-50 transition-colors"
                      @click="handleEdit(row)"
                    >
                      <Pencil :size="14" class="text-gray-400" />
                      Rediger
                    </button>
                    <div class="my-1 border-t border-stone-100" />
                    <button
                      class="w-full flex items-center gap-2.5 px-3.5 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors"
                      @click="handleDelete(row)"
                    >
                      <Trash2 :size="14" class="text-red-400" />
                      Slett
                    </button>
                  </div>
                </Transition>
              </div>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

  </div>
</template>
