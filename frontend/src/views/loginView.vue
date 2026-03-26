<script setup lang="ts">

</script>

<template>
  <div class="container">
    <p class="step">STEG 1: INNLOGGING</p>

    <div class="card">
      <h2>IK-Komplett</h2>
      <p class="subtitle">Logg inn for å fortsette</p>

      <label>E-post</label>
      <input
        type="email"
        placeholder="mail@mail.com"
      />

      <label>Passord</label>
      <input
        type="password"
        placeholder="••••••••"
      />

      <button>Logg inn</button>

      <p class="forgot">Glemt passord?</p>
    </div>
  </div>
lklkl
</template>

<style scoped>

.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 40px;
  font-family: Arial, sans-serif;
}

.step {
  color: #666;
  margin-bottom: 16px;
  font-weight: 500;
}

.card {
  width: 350px;
  background: #f7f7f7;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

h2 {
  color: #5b50c7;
  text-align: center;
  margin-bottom: 8px;
}

.subtitle {
  text-align: center;
  color: #666;
  margin-bottom: 20px;
}

label {
  display: block;
  margin-top: 12px;
  margin-bottom: 4px;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #ccc;
}

button {
  width: 100%;
  margin-top: 20px;
  padding: 12px;
  border: none;
  border-radius: 10px;
  background-color: #5b50c7;
  color: white;
  font-weight: bold;
  cursor: pointer;
}

button:hover {
  background-color: #4a42a8;
}

.forgot {
  text-align: center;
  margin-top: 12px;
  color: #777;
  font-size: 14px;
}
</style>
