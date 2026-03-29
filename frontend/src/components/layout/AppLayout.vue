<script setup lang="ts">
import AppSidebar from './AppSidebar.vue'

withDefaults(
  defineProps<{
    activeMenuItem?: string
  }>(),
  {
    activeMenuItem: 'Dashboard',
  },
)
</script>

<template>
  <div class="layout-shell">
    <AppSidebar :active-item="activeMenuItem" />
    <main class="content"><slot /></main>
  </div>
</template>

<style scoped>
.layout-shell {
  width: min(1280px, 100% - 48px);
  margin: 24px auto;
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 24px;
}

.content {
  min-width: 0;
}

@media (max-width: 980px) {
  .layout-shell {
    width: min(1280px, 100% - 24px);
    margin: 12px auto;
    grid-template-columns: 1fr;
    gap: 16px;
  }
}
</style>
