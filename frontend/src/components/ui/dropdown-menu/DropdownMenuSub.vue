<script setup lang="ts">
import { provide, ref } from "vue"

const isSubOpen = ref(false)

function openSub() {
  isSubOpen.value = true
}

function closeSub() {
  isSubOpen.value = false
}

provide("dropdown-sub", { isSubOpen, openSub, closeSub })
</script>

<template>
  <div class="dropdown-sub">
    <slot />
  </div>
</template>

<style scoped>
.dropdown-sub {
  position: relative;
}
</style>
