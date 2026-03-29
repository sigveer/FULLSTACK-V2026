<script setup lang="ts">
import { inject } from "vue"

const { close } = inject("dialog") as { close: () => void }
</script>

<template>
  <button type="button" @click="close">
    <slot />
  </button>
</template>
