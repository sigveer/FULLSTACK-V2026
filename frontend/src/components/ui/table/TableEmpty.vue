<script setup lang="ts">
import TableCell from "./TableCell.vue"
import TableRow from "./TableRow.vue"

defineProps<{
  class?: string
  colspan?: number
}>()
</script>

<template>
  <TableRow>
    <TableCell
      :class="`table-empty ${$props.class ?? ''}`"
      :colspan="colspan"
    >
      <div class="table-empty__content">
        <slot />
      </div>
    </TableCell>
  </TableRow>
</template>

<style scoped>
.table-empty {
  padding: 1rem;
  white-space: nowrap;
  vertical-align: middle;
  font-size: 0.875rem;
  line-height: 1.25rem;
  color: hsl(var(--foreground, 24 10% 15%));
}

.table-empty__content {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2.5rem 0;
}
</style>
