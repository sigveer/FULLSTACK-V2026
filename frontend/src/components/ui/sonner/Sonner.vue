<script lang="ts" setup>
import type { ToasterProps } from "vue-sonner"
import { reactiveOmit } from "@vueuse/core"
import { CircleCheckIcon, InfoIcon, Loader2Icon, OctagonXIcon, TriangleAlertIcon, XIcon } from "lucide-vue-next"
import { Toaster as Sonner } from "vue-sonner"

const props = defineProps<ToasterProps>()
const delegatedProps = reactiveOmit(props, "toastOptions")
</script>

<template>
  <Sonner
    class="toaster"
    :toast-options="{
      classes: {
        toast: 'sonner---------------------toast',
        description: 'sonner---------------------description',
        actionButton: 'sonner---------------------action',
        cancelButton: 'sonner---------------------cancel',
      },
    }"
    v-bind="delegatedProps"
  >
    <template #success-icon>
      <CircleCheckIcon class="sonner-icon" />
    </template>
    <template #info-icon>
      <InfoIcon class="sonner-icon" />
    </template>
    <template #warning-icon>
      <TriangleAlertIcon class="sonner-icon" />
    </template>
    <template #error-icon>
      <OctagonXIcon class="sonner-icon" />
    </template>
    <template #loading-icon>
      <div>
        <Loader2Icon class="sonner-icon sonner-icon--spin" />
      </div>
    </template>
    <template #close-icon>
      <XIcon class="sonner-icon" />
    </template>
  </Sonner>
</template>

<style>
/* These styles must be unscoped since vue-sonner-------------------- injects toast elements outside this component's scope */

.sonner-toast {
  background-color: hsl(var(--background, 43 30% 96%)) !important;
  color: hsl(var(--foreground, 24 10% 10%)) !important;
  border-color: hsl(var(--border, 35 15% 90%)) !important;
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1) !important;
}

.sonner-description {
  color: hsl(var(--muted-foreground, 24 5% 46%)) !important;
}

.sonner-action {
  background-color: hsl(var(--primary, 245 43% 52%)) !important;
  color: hsl(var(--primary-foreground, 0 0% 100%)) !important;
}

.sonner-cancel {
  background-color: hsl(var(--muted, 40 15% 92%)) !important;
  color: hsl(var(--muted-foreground, 24 5% 46%)) !important;
}

.sonner-icon {
  width: 1rem;
  height: 1rem;
}

.sonner-icon--spin {
  animation: sonner-spin 1s linear infinite;
}

@keyframes sonner-spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
