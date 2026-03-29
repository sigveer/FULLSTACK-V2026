<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const { activeItem = 'Dashboard' } = defineProps<{
  activeItem?: string
}>()

const router = useRouter()

const itemToRoute: Record<string, string | null> = {
  Dashboard: '/dashboard',
  Sjekklister: '/sjekklister',
  Temperatur: '/temperatur',
  Opplæring: '/opplæring',
  Bevilling: '/bevilling',
  Avvik: '/avvik',
}

function navigateTo(item: string): void {
  const target = itemToRoute[item]
  if (!target) {
    return
  }
  router.push(target)
}

const sections = computed(() => [
  {
    title: 'OVERSIKT',
    items: ['Dashboard'],
  },
  {
    title: 'IK-MAT',
    items: ['Sjekklister', 'Temperatur'],
  },
  {
    title: 'IK-ALKOHOL',
    items: ['Opplæring', 'Bevilling'],
  },
  {
    title: 'FELLES',
    items: ['Avvik'],
  },
])
</script>

<template>
  <aside class="sidebar">
    <div>
      <h1 class="brand">IK-Komplett</h1>

      <nav>
        <section v-for="section in sections" :key="section.title" class="section">
          <h2 class="section-title">{{ section.title }}</h2>
          <ul class="item-list">
            <li v-for="item in section.items" :key="item">
              <button type="button" class="item" :class="{ 'item--active': activeItem === item }" @click="navigateTo(item)">
                {{ item }}
              </button>
            </li>
          </ul>
        </section>
      </nav>
    </div>

    <footer class="tenant">
      <p>Everest Sushi &amp; Fusion</p>
      <p>Leder Ledersen</p>
    </footer>
  </aside>
</template>

<style scoped>
.sidebar {
  width: 240px;
  min-height: calc(100vh - 48px);
  border: 1px solid var(--border);
  border-radius: var(--radius-xl);
  background: var(--panel-bg);
  padding: 24px 18px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  box-shadow: var(--shadow-sm);
}

.brand {
  margin: 0;
  color: var(--brand);
  font-size: 1.92rem;
  font-weight: 800;
  letter-spacing: -0.02em;
}

.section {
  margin-top: 24px;
}

.section-title {
  margin: 0 0 10px;
  font-size: 1.06rem;
  color: #444952;
  letter-spacing: 0.05em;
}

.item-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.item-list li + li {
  margin-top: 2px;
}

.item {
  width: 100%;
  border: none;
  background: transparent;
  text-align: left;
  border-radius: var(--radius-md);
  padding: 10px 10px;
  color: #2f3339;
  font-size: 1.1rem;
  font-weight: 550;
  cursor: pointer;
}

.item--active {
  background: #e2e1f3;
  color: #3f3aa6;
}

.tenant {
  border-top: 1px solid var(--border);
  margin-top: 20px;
  padding-top: 16px;
  color: #4d5158;
  font-size: 1.03rem;
  line-height: 1.25;
}

.tenant p {
  margin: 0;
}

@media (max-width: 980px) {
  .sidebar {
    width: 100%;
    min-height: auto;
    padding: 20px;
  }

  .tenant {
    margin-top: 10px;
  }
}
</style>
