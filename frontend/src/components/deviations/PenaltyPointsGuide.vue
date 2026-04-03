<script setup lang="ts">
</script>

<template>
  <div class="guide-card">
    <h3 class="guide-title">Prikkguide — kjenn risikoen</h3>
    <p class="guide-subtitle">Alkoholforskriften §10-3. Vis dette til ansatte slik at alle forstår konsekvensene.</p>

    <div class="tier tier--8p">
      <span class="tier-badge">8p</span>
      <span class="tier-text">Skjenking til mindreåring · Brudd på bistandsplikt · Uforsvarlig drift · Hindring av kontroll</span>
    </div>

    <div class="tier tier--4p">
      <span class="tier-badge">4p</span>
      <span class="tier-text">Skjenking til åpenbart beruset · Brudd på skjenketider · Brennevin til 18-19-åringer</span>
    </div>

    <div class="tier tier--2p">
      <span class="tier-badge">2p</span>
      <span class="tier-text">Beruset person i lokalet · Mangler IK-system · Mangler styrer/stedfortreder · Narkotika</span>
    </div>

    <div class="tier tier--1p">
      <span class="tier-badge">1p</span>
      <span class="tier-text">Alkoholfrie alternativer mangler · Medbrakt alkohol · Reklamebrudd · Vilkårsbrudd</span>
    </div>
  </div>
</template>

<style scoped>
.guide-card {
  border: 1px solid hsl(var(--border));
  border-radius: var(--radius-lg);
  background: hsl(var(--card));
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.guide-title {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 700;
  letter-spacing: -0.01em;
}

.guide-subtitle {
  margin: 0;
  font-size: 0.84rem;
  color: hsl(var(--muted-foreground));
  line-height: 1.4;
}

.tier {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 10px 12px;
  border-radius: var(--radius-md);
}

.tier-badge {
  font-weight: 800;
  font-size: 1rem;
  flex-shrink: 0;
  min-width: 2rem;
}

.tier-text {
  font-size: 0.88rem;
  line-height: 1.4;
}

.tier--8p { background: #f4e0e0; }
.tier--8p .tier-badge { color: #7f1d1d; }
.tier--8p .tier-text { color: #7f1d1d; }

.tier--4p { background: #f6dfc8; }
.tier--4p .tier-badge { color: #7c2d12; }
.tier--4p .tier-text { color: #7c2d12; }

.tier--2p { background: #f4e6d1; }
.tier--2p .tier-badge { color: #78350f; }
.tier--2p .tier-text { color: #78350f; }

.tier--1p { background: #f5f0e6; }
.tier--1p .tier-badge { color: #78350f; }
.tier--1p .tier-text { color: #78350f; }
</style>
