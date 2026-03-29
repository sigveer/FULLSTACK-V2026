<script setup lang="ts">

</script>

<template>
  <div class="container">
    <div class="card">
      <h1 class="card-title">Opprett virksomhet</h1>
      <p class="card-subtitle">Fyll inn informasjon om din virksomhet</p>

      <div class="field">
        <label for="businessName">Virksomhetsnavn</label>
      </div>

      <div class="field">
        <label for="businessEmail">Virksomhetsmail</label>
      </div>

      <div class="field">
        <label for="businessNmbr">Telefonnummer</label>
      </div>

      <div class="field">
        <label for="OrgNr">Org nmr/id</label>
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

      <button class="btn-primary" type="button">Opprett</button>
    </div>
  </div>
</template>

<style scoped>

</style>
