<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useOrg } from '@/composables/useOrg'
import OrgCard from '@/components/OrgCard.vue'
import Button from '@/components/ui/button/Button.vue'

const router = useRouter()
const { memberships, selectOrg } = useOrg()

const selectedOrgId = ref<number | null>(
  memberships.value.length === 1 ? memberships.value[0]?.organizationId ?? null : null,
)

async function handleSubmit() {
  if (!selectedOrgId.value) return
  await selectOrg.mutateAsync({ organizationId: selectedOrgId.value })
  router.push('/')
}

defineExpose({ selectOrgError: selectOrg.error })
</script>

<template>
  <form @submit.prevent="handleSubmit" novalidate>
    <div class="org-list">
      <OrgCard
        v-for="m in memberships"
        :key="m.membershipId"
        :membership="m"
        :selected="selectedOrgId === m.organizationId"
        @select="selectedOrgId = m.organizationId"
      />
    </div>

    <Button type="submit" :disabled="!selectedOrgId || selectOrg.isPending.value">
      {{ selectOrg.isPending.value ? 'Velger...' : 'Fortsett' }}
    </Button>
  </form>
</template>

<style scoped>
form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.org-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 340px;
  overflow-y: auto;
}

form > :deep(.btn) {
  width: 100%;
}
</style>
