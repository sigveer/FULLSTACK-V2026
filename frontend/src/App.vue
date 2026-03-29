<script setup lang="ts">
import 'vue-sonner/style.css'
import { useInitAuth } from '@/composables/useInitAuth'
import Sonner from '@/components/ui/sonner/Sonner.vue'

const { init, isLoading } = useInitAuth()
init()
</script>

<template>
  <div v-if="isLoading">Loading...</div>
  <RouterView v-else />
  <Sonner position="bottom-right" />
</template>

<style>
@media (max-width: 600px) {
  [data-sonner-toaster] {
    left: 0 !important;
    right: 0 !important;
    width: 100% !important;
    transform: none !important;
  }

  [data-sonner-toaster] [data-sonner-toast] {
    width: 100% !important;
    border-radius: 0 !important;
  }
}
</style>
