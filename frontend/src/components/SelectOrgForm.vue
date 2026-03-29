<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useOrg } from '@/composables/useOrg'
import OrgCard from '@/components/OrgCard.vue'
import Button from '@/components/ui/button/Button.vue'
import { Building2, Plus } from 'lucide-vue-next'

const router = useRouter()
const { memberships, hasMemberships, selectOrg } = useOrg()

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
  <div v-if="!hasMemberships" class="empty">
    <div class="empty-icon">
      <Building2 :size="32" />
    </div>
    <h3 class="empty-title">Ingen virksomheter</h3>
    <p class="empty-description">
      Du er ikke medlem av noen virksomheter ennå. Opprett en ny virksomhet for å komme i gang.
    </p>
    <Button type="button">
      <Plus :size="16" />
      Opprett virksomhet
    </Button>
  </div>

  <form v-else @submit.prevent="handleSubmit" novalidate>
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
  gap: 10px;
  max-height: 340px;
  overflow-y: auto;
}

form > :deep(.btn) {
  width: 100%;
}

/* ── Empty state ── */
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 1.5rem 0;
  gap: 12px;
}

.empty-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: hsl(var(--muted));
  color: hsl(var(--muted-foreground));
}

.empty-title {
  font-size: 1rem;
  font-weight: 600;
  color: hsl(var(--foreground));
  margin: 0;
}

.empty-description {
  font-size: 0.85rem;
  color: hsl(var(--muted-foreground));
  line-height: 1.5;
  margin: 0;
  max-width: 260px;
}
</style>
