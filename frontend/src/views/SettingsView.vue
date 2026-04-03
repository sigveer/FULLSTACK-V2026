<script setup lang="ts">
import { ref } from 'vue'
import AppLayout from '@/components/layout/AppLayout.vue'
import { Separator } from '@/components/ui/separator'
import { SidebarTrigger } from '@/components/ui/sidebar'
import Button from '@/components/ui/button/Button.vue'
import Switch from '@/components/ui/switch/Switch.vue'

const unreadBadge = ref(true)
const commEmails = ref(false)
const socialEmails = ref(false)
const announcementEmails = ref(false)
const tipsEmails = ref(false)
</script>

<template>
  <AppLayout>
    <header class="page-header">
      <div class="page-header-inner">
        <SidebarTrigger />
        <Separator orientation="vertical" class="header-separator" />
        <span class="page-title">Innstillinger</span>
      </div>
    </header>

    <div class="page-content">

      <section class="settings-section">
        <h2 class="section-title">Merker</h2>

        <div class="setting-row">
          <div class="setting-info">
            <span class="setting-label">Aktiver ulest melding-merke</span>
            <span class="setting-desc">Viser et rødt merke i appikonet når du har uleste meldinger.</span>
          </div>
          <Switch
            :checked="unreadBadge"
            @update:checked="unreadBadge = $event"
          />
        </div>
      </section>

      <Separator />

      <section class="settings-section">
        <h2 class="section-title">E-postadresse</h2>

        <div class="setting-row">
          <div class="setting-info">
            <span class="setting-label">Kommunikasjons-e-poster</span>
            <span class="setting-desc">Motta e-poster om ubesvarte anrop, meldinger og sammendrag av meldinger.</span>
          </div>
          <Switch
            :checked="commEmails"
            @update:checked="commEmails = $event"
          />
        </div>

        <div class="setting-row">
          <div class="setting-info">
            <span class="setting-label">Sosiale e-poster</span>
            <span class="setting-desc">Motta e-poster om venneforespørsler, forslag til venner og eventer på serveren din.</span>
          </div>
          <Switch
            :checked="socialEmails"
            @update:checked="socialEmails = $event"
          />
        </div>

        <div class="setting-row">
          <div class="setting-info">
            <span class="setting-label">E-poster med kunngjøringer og oppdateringer</span>
            <span class="setting-desc">Motta e-poster om produktoppdateringer, nytt innhold, forbedringer og feilretting.</span>
          </div>
          <Switch
            :checked="announcementEmails"
            @update:checked="announcementEmails = $event"
          />
        </div>

        <div class="setting-row">
          <div class="setting-info">
            <span class="setting-label">E-poster med tips</span>
            <span class="setting-desc">Motta e-poster med nyttige råd om hvordan du bruker Discord, og med informasjon om mindre kjente funksjoner.</span>
          </div>
          <Switch
            :checked="tipsEmails"
            @update:checked="tipsEmails = $event"
          />
        </div>
      </section>

      <Separator />

      <section class="settings-section">
        <h2 class="section-title">Autentiseringsapp</h2>
        <p class="section-desc">
          Det å konfigurere en autentiseringsapp er en fin måte å sikre Discord-kontoen på, slik at det bare er du som kan logge på.
        </p>
        <div class="button-row">
          <Button variant="default">Vis sikkerhetskoder</Button>
          <Button variant="outline" class="btn--danger-outline">Fjern autentiseringsapp</Button>
        </div>
      </section>

      <section class="settings-section">
        <h2 class="section-title">SMS som reserveløsning for autentisering</h2>
        <p class="section-desc">
          Bruk telefonen som backupløsning for flerfaktorautentisering i tilfelle du mister tilgang til autentiseringsappen eller sikkerhetskopikodene.
        </p>
        <p class="section-desc">
          Det nåværende telefonnummeret ditt er: *******5853.
          <Button variant="link" class="inline-link">Vis</Button>
        </p>
        <div class="button-row">
          <Button variant="outline" class="btn--danger-outline">Fjern SMS-autentisering</Button>
        </div>
      </section>

      <section class="settings-section">
        <h2 class="section-title">Sikkerhetsnøkler</h2>
        <p class="section-desc">Gi kontoen din ekstra beskyttelse med en sikkerhetsnøkkel.</p>
        <div class="button-row">
          <Button variant="default">Registrer en sikkerhetsnøkkel</Button>
        </div>
      </section>

      <Separator />

      <section class="settings-section">
        <h2 class="section-title">Fjerning av konto</h2>
        <p class="section-desc">Hvis du deaktiverer kontoen din, kan du når som helst hente den frem igjen.</p>
        <div class="button-row">
          <Button variant="destructive">Deaktiver kontoen</Button>
          <Button variant="outline" class="btn--danger-outline">Slett kontoen</Button>
        </div>
      </section>

    </div>
  </AppLayout>
</template>

<style scoped>

.page-header {
  display: flex;
  height: 4rem;
  flex-shrink: 0;
  align-items: center;
}
.page-header-inner {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0 1rem;
}
.header-separator {
  height: 1rem !important;
  width: 1px !important;
  margin-right: 0.5rem;
}
.page-title {
  font-weight: 500;
  color: hsl(var(--sidebar-primary, 245 43% 52%));
}
.page-content {
  display: flex;
  flex: 1;
  flex-direction: column;
  padding: 0 1.5rem 2rem;
  max-width: 760px;
}

.settings-section {
  padding: 1.75rem 0;
}
.section-title {
  font-size: 1.25rem;
  font-weight: 700;
  margin: 0 0 0.5rem;
  color: hsl(var(--foreground));
}
.section-desc {
  font-size: 0.9rem;
  color: hsl(var(--muted-foreground));
  margin: 0 0 0.25rem;
  line-height: 1.5;
}

.setting-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 2rem;
  padding: 1rem 0;
  border-bottom: 1px solid hsl(var(--border) / 0.5);
}
.setting-row:last-child {
  border-bottom: none;
}
.setting-info {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}
.setting-label {
  font-size: 0.9375rem;
  font-weight: 600;
  color: hsl(var(--foreground));
}

.button-row {
  display: flex;
  flex-wrap: wrap;
  gap: 0.625rem;
  margin-top: 0.75rem;
}

:deep(.btn--danger-outline) {
  color: hsl(var(--destructive, 0 55% 48%));
}
:deep(.btn--danger-outline:hover) {
  color: hsl(var(--destructive, 0 55% 42%));
  background-color: hsl(var(--destructive, 0 55% 48%) / 0.05);
}

:deep(.inline-link) {
  height: auto;
  padding: 0;
  font-size: inherit;
  vertical-align: baseline;
}
</style>
