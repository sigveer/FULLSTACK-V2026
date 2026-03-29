<script setup lang="ts">
import { useInitAuth } from '@/composables/useInitAuth'

const { init, isLoading } = useInitAuth()
init()
</script>

<template>
  <div v-if="isLoading">Loading...</div>
  <RouterView v-else />
</template>
