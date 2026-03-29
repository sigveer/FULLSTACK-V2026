<script setup lang="ts">

</script>

<template>
  <div class="container">
    <div class="card">
      <h1 class="card-title">Opprett virksomhet</h1>
      <p class="card-subtitle">Fyll inn informasjon om din virksomhet</p>

      <div class="field">
        <label for="businessName">Virksomhetsnavn</label>
        <input id="businessName" type="text" placeholder="Eksempel AS" />
      </div>

      <div class="field">
        <label for="businessEmail">Virksomhetsmail</label>
        <input id="businessEmail" type="email" placeholder="kontakt@eksempel.no" />
      </div>

      <div class="field">
        <label for="businessNmbr">Telefonnummer</label>
        <input id="businessNmbr" type="tel" placeholder="+47 000 00 000" />
      </div>

      <div class="field">
        <label for="OrgNr">Org nmr/id</label>
        <input id="OrgNr" type="text" placeholder="000 000 000" />
      </div>

      <div class="field">
        <label for="businessType">Type virksomhet</label>
        <div class="select-wrap">
          <select id="businsType">
            <option>Restaurant</option>
            <option>Kafé</option>
            <option>Annet</option>
          </select>
          <svg class="select-arrow" width="14" height="14" viewBox="0 0 24 24" fill="none"
               stroke="currentColor" stroke-width="2.2"
               stroke-linecap="round" stroke-linejoin="round">
            <polyline points="6 9 12 15 18 9"/>
          </svg>
        </div>
      </div>

      <button class="finishBtn" type="button">Opprett</button>
    </div>
  </div>
</template>

<style scoped>

.container {
  --accent:         #5046e4;
  --accent-hover:   #3d34c8;
  --accent-focus:   rgba(80, 70, 228, 0.12);
  --bg-page:        #e4e4ee;
  --bg-card:        #ffffff;
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

.container {
  font-family: 'Inter', sans-serif;
  background: var(--bg-page);
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2.5rem 1rem;
}

.card {
  background: var(--bg-card);
  border-radius: var(--radius-card);
  padding: 2rem 1.8rem;
  width: 400px;
  box-shadow: var(--shadow-card);
}

.card-title {
  font-size: 26px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.2;
  margin-bottom: 0.25rem;
}

.card-subtitle {
  font-size: 13.5px;
  color: var(--text-muted);
  margin-bottom: 1.6rem;
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

.select-wrap {
  position: relative;
}

.select-wrap select {
  width: 100%;
  height: 44px;
  border: 1.5px solid var(--border);
  border-radius: var(--radius-input);
  background: #ffffff;
  font-family: 'Inter', sans-serif;
  font-size: 14px;
  color: var(--text-primary);
  padding: 0 38px 0 14px;
  outline: none;
  cursor: pointer;
  appearance: none;
  -webkit-appearance: none;
  transition: border-color var(--transition), box-shadow var(--transition);
}

.select-wrap select:hover {
  border-color: #c0c0d8;
}

.select-wrap select:focus {
  border-color: var(--accent);
  box-shadow: 0 0 0 3px var(--accent-focus);
}

.select-arrow {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-muted);
  pointer-events: none;
}

.finishBtn {
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

.finishBtn:hover {
  background: var(--accent-hover);
  border-color: #2a22a8;
}

.finishBtn:active {
  transform: scale(0.985);
}

</style>
