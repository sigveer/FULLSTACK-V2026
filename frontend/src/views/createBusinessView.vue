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
        <label for="OrgNr">Org nmr/id</label>
      </div>

      <div class="field">
        <label for="businessType">Type virksomhet</label>
        <div class="select-wrap">
          <select id="businsType">
            <option>Aksjeselskap (AS)</option>
            <option>Enkeltpersonforetak (ENK)</option>
            <option>Ansvarlig selskap (ANS)</option>
            <option>Samvirkeforetak (SA)</option>
            <option>Stiftelse</option>
            <option>Annet</option>
          </select>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
