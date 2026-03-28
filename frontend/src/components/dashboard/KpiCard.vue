<script setup lang="ts">
const props = withDefaults(
  defineProps<{
    title: string
    value: string
    subtitle?: string
    highlight?: 'default' | 'danger' | 'success'
    progress?: {
      current: number
      total: number
    }
  }>(),
  {
    subtitle: '',
    highlight: 'default',
    progress: undefined,
  },
)

const progressPercent =
  props.progress && props.progress.total > 0
    ? Math.round((props.progress.current / props.progress.total) * 100)
    : 0
</script>

<template>
  <article class="kpi">
    <p class="kpi-title">{{ title }}</p>
    <p class="kpi-value" :class="`kpi-value--${highlight}`">{{ value }}</p>
    <p v-if="subtitle" class="kpi-subtitle" :class="`kpi-subtitle--${highlight}`">{{ subtitle }}</p>
    <div v-if="progress" class="progress-track" role="progressbar" :aria-valuenow="progress.current" :aria-valuemax="progress.total">
      <div class="progress-fill" :style="{ width: `${progressPercent}%` }" />
    </div>
  </article>
</template>

<style scoped>
.kpi {
  min-width: 0;
}

.kpi-title {
  margin: 0;
  color: #40454b;
  font-size: 1.04rem;
  line-height: 1.25;
}

.kpi-value {
  margin: 4px 0 0;
  font-size: 2.2rem;
  line-height: 1;
  font-weight: 800;
  color: #14161a;
}

.kpi-value--danger {
  color: var(--red);
}

.kpi-value--success {
  color: var(--green);
}

.kpi-subtitle {
  margin: 8px 0 0;
  color: #4f555c;
  font-size: 1.02rem;
}

.kpi-subtitle--danger {
  color: var(--red);
}

.kpi-subtitle--success {
  color: #50705f;
}

.progress-track {
  margin-top: 10px;
  height: 6px;
  border-radius: var(--radius-pill);
  background: #d1d1d0;
  overflow: hidden;
  max-width: 84px;
}

.progress-fill {
  height: 100%;
  background: var(--brand);
  border-radius: var(--radius-pill);
}
</style>
