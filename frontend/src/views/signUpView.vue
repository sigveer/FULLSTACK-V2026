<script setup lang="ts">

</script>

<template>
  <div class="page">

    <div class="layout">

      <div class="steps-col">
        <div class="step-entry">
          <div class="step-label active">Steg 1</div>
          <div class="step-dot active"></div>
        </div>
        <div class="step-line"></div>
        <div class="step-entry">
          <div class="step-dot"></div>
          <div class="step-label">Steg 2</div>
        </div>
      </div>

      <div class="cards-col">

        <div class="card card--active">
          <span class="card-tag card-tag--active">Steg 1: Registrering</span>
          <h1 class="card-title">IK-Komplett</h1>
          <p class="card-sub">Opprett en konto for å komme i gang</p>

          <div class="two-col">
            <div class="field">
              <label for="fornavn">Fornavn</label>
              <input id="fornavn" type="text" placeholder="Ola" />
            </div>
            <div class="field">
              <label for="etternavn">Etternavn</label>
              <input id="etternavn" type="text" placeholder="Nordmann" />
            </div>
          </div>

          <div class="field">
            <label for="epost">E-post</label>
            <input id="epost" type="email" placeholder="ola@eksempel.no" />
          </div>

          <div class="field">
            <label for="tlf">Telefonnummer</label>
            <input id="tlf" type="tel" placeholder="+47 000 00 000" />
          </div>

          <div class="field">
            <label for="passord">Passord</label>
            <div class="pw-wrap">
              <input id="passord" type="password" placeholder="Minst 8 tegn" />
              <button class="pw-toggle" type="button" aria-label="Vis/skjul passord">
                <svg width="17" height="17" viewBox="0 0 24 24" fill="none"
                     stroke="currentColor" stroke-width="1.8"
                     stroke-linecap="round" stroke-linejoin="round">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                  <circle cx="12" cy="12" r="3"/>
                </svg>
              </button>
            </div>
          </div>

          <label class="terms-row">
            <input type="checkbox" />
            <span class="terms-text">
              Jeg godtar <a href="#">vilkårene</a> og
              <a href="#">personvernreglene</a> for IK-Komplett
            </span>
          </label>

          <button class="btn-primary" type="button">Opprett konto</button>

          <p class="login-link">
            Har du allerede konto? <a href="#">Logg inn</a>
          </p>
        </div>

        <div class="card card--dim">
          <span class="card-tag">Steg 2: Bekreftelse</span>
          <h2 class="card-title">Bekreft e-post</h2>
          <p class="card-sub">Du mottar en bekreftelseslenke etter registrering.</p>
        </div>

      </div>
    </div>

  </div>
</template>

<style scoped>
.page {
  --accent:         #5046e4;
  --accent-hover:   #3d34c8;
  --accent-focus:   rgba(80, 70, 228, 0.12);
  --bg-page:        #e4e4ee;
  --bg-card:        #ffffff;
  --bg-card-dim:    #ededf4;
  --text-primary:   #1a1a2e;
  --text-secondary: #666;
  --text-muted:     #aaa;
  --border:         #e0e0ea;
  --radius-card:    20px;
  --radius-input:   10px;
  --radius-btn:     12px;
  --shadow-card:    0 6px 32px rgba(80, 70, 228, 0.10);
  --transition:     0.15s ease;
}

.page {
  font-family: 'Inter', sans-serif;
  background: var(--bg-page);
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2.5rem 1rem;
}

.layout {
  display: flex;
  align-items: center;
}

.steps-col {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 1.5rem;
}

.step-entry {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.step-label {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.09em;
  color: var(--text-muted);
  text-transform: uppercase;
  writing-mode: vertical-rl;
  transform: rotate(180deg);
  padding: 8px 0;
}

.step-label.active {
  color: var(--accent);
}

.step-dot {
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: var(--border);
  flex-shrink: 0;
}

.step-dot.active {
  background: var(--accent);
}

.step-line {
  width: 2px;
  height: 50px;
  background: var(--border);
}

.cards-col {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  width: 400px;
}

.card {
  background: var(--bg-card-dim);
  border-radius: var(--radius-card);
  padding: 2rem 1.8rem;
}

.card--active {
  background: var(--bg-card);
  box-shadow: var(--shadow-card);
}

.card--dim .card-title {
  color: #c0c0ce;
  font-size: 22px;
}

.card--dim .card-sub {
  color: #c0c0ce;
  margin-bottom: 0;
}

.card-tag {
  display: block;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: var(--text-muted);
  margin-bottom: 0.75rem;
}

.card-tag--active {
  color: var(--accent);
}

.card-title {
  font-size: 26px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.2;
  margin-bottom: 0.25rem;
}

.card-sub {
  font-size: 13.5px;
  color: var(--text-muted);
  margin-bottom: 1.6rem;
}

.two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.8rem;
}

.field {
  margin-bottom: 0.8rem;
}

.field label {
  display: block;
  font-size: 12px;
  font-weight: 600;
  color: var(--text-secondary);
  letter-spacing: 0.02em;
  margin-bottom: 5px;
}

.field input {
  width: 100%;
  height: 44px;
  border: 1.5px solid var(--border);
  border-radius: var(--radius-input);
  background: #ffffff;
  font-family: 'Inter', sans-serif;
  font-size: 14px;
  color: var(--text-primary);
  padding: 0 14px;
  outline: none;
  cursor: text;
  transition: border-color var(--transition), box-shadow var(--transition);
}

.field input::placeholder {
  color: var(--text-muted);
}

.field input:hover {
  border-color: #c0c0d8;
}

.field input:focus {
  border-color: var(--accent);
  box-shadow: 0 0 0 3px var(--accent-focus);
}

.pw-wrap {
  position: relative;
}

.pw-wrap input {
  padding-right: 44px;
}

.pw-toggle {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  padding: 0;
  color: var(--text-muted);
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: color var(--transition);
}

.pw-toggle:hover {
  color: var(--accent);
}

/* ── Terms ── */
.terms-row {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-top: 0.9rem;
  cursor: pointer;
}

.terms-row input[type="checkbox"] {
  margin-top: 2px;
  accent-color: var(--accent);
  width: 14px;
  height: 14px;
  flex-shrink: 0;
  cursor: pointer;
}

.terms-text {
  font-size: 12px;
  color: var(--text-muted);
  line-height: 1.55;
}

.terms-text a {
  color: var(--accent);
  text-decoration: none;
}

.terms-text a:hover {
  text-decoration: underline;
  cursor: pointer;
}

.btn-primary {
  display: block;
  width: 100%;
  height: 48px;
  background: var(--accent);
  color: #fff;
  border: 2px solid transparent;
  border-radius: var(--radius-btn);
  font-family: 'Inter', sans-serif;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 1.15rem;
  letter-spacing: 0.01em;
  transition: background var(--transition), border-color var(--transition), transform var(--transition);
}

.btn-primary:hover {
  background: var(--accent-hover);
  border-color: #2a22a8;
}

.btn-primary:active {
  transform: scale(0.985);
}

/* ── Login link ── */
.login-link {
  text-align: center;
  margin-top: 1rem;
  font-size: 13px;
  color: var(--text-muted);
}

.login-link a {
  color: var(--accent);
  text-decoration: none;
  font-weight: 600;
}

.login-link a:hover {
  text-decoration: underline;
  cursor: pointer;
}
</style>
