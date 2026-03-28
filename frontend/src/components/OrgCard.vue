<script setup lang="ts">
import type { MembershipSummary } from '@/types/auth'

const props = defineProps<{
  membership: MembershipSummary
  selected: boolean
}>()

defineEmits<{
  select: []
}>()

const roleLabels: Record<string, string> = {
  ADMIN: 'Admin',
  MANAGER: 'Leder',
  EMPLOYEE: 'Ansatt',
}

function getInitials(name: string): string {
  return name
    .split(' ')
    .map((w) => w[0])
    .join('')
    .toUpperCase()
    .slice(0, 2)
}

function getColorIndex(id: number): number {
  return id % 3
}
</script>

<template>
  <div
    class="org-card"
    :class="{ 'org-card--selected': props.selected }"
    @click="$emit('select')"
  >
    <div class="org-card__left">
      <div class="org-card__logo" :class="`org-card__logo--${getColorIndex(props.membership.organizationId)}`">
        {{ getInitials(props.membership.organizationName) }}
      </div>
      <div class="org-card__info">
        <h3>{{ props.membership.organizationName }}</h3>
      </div>
    </div>
    <span class="org-card__role" :class="`org-card__role--${getColorIndex(props.membership.organizationId)}`">
      {{ roleLabels[props.membership.role] ?? props.membership.role }}
    </span>
  </div>
</template>

<style scoped>
.org-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border: 2px solid hsl(var(--border));
  border-radius: 0.5rem;
  background: hsl(var(--popover));
  cursor: pointer;
  transition: border-color 150ms ease;
}

.org-card:hover {
  border-color: hsl(var(--primary) / 0.4);
}

.org-card--selected {
  border-color: hsl(var(--primary));
}

.org-card__left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.org-card__logo {
  width: 40px;
  height: 40px;
  border-radius: 0.375rem;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.875rem;
  font-weight: 700;
  flex-shrink: 0;
}

.org-card__logo--0 {
  background: hsl(var(--accent));
  color: hsl(var(--primary));
}

.org-card__logo--1 {
  background: hsl(142 50% 90%);
  color: hsl(142 50% 30%);
}

.org-card__logo--2 {
  background: hsl(35 80% 90%);
  color: hsl(35 80% 35%);
}

.org-card__info h3 {
  margin: 0;
  font-size: 0.875rem;
  font-weight: 600;
  color: hsl(var(--foreground));
  line-height: 1.2;
}

.org-card__role {
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 600;
  white-space: nowrap;
  flex-shrink: 0;
}

.org-card__role--0 {
  background: hsl(var(--accent));
  color: hsl(var(--primary));
}

.org-card__role--1 {
  background: hsl(142 50% 90%);
  color: hsl(142 50% 30%);
}

.org-card__role--2 {
  background: hsl(35 80% 90%);
  color: hsl(35 80% 35%);
}
</style>
