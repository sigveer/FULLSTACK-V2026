<script setup lang="ts">
withDefaults(defineProps<{
  initials: string
  color: string
  size?: 'sm' | 'lg'
}>(), { size: 'sm' })
</script>

<template>
  <div
    class="avatar"
    :class="size === 'lg' ? 'avatar-lg' : 'avatar-sm'"
    :style="{ background: color }"
  >
    {{ initials }}
  </div>
</template>

<style scoped>
.avatar {
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 600;
  flex-shrink: 0;
}

.avatar-sm {
  width: 36px;
  height: 36px;
  font-size: 0.75rem;
}

.avatar-lg {
  width: 52px;
  height: 52px;
  font-size: 1.1rem;
}
</style>
