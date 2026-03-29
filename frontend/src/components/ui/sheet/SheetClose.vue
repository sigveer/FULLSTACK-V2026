<script setup lang="ts">
import { inject } from "vue"

const { close } = inject("sheet") as any
</script>

<template>
  <button type="button" @click="close">
    <slot />
  </button>
</template>
