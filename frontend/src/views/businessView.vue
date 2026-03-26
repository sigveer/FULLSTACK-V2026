<script setup lang="ts">
import { ref } from "vue";

const selectedCompany = ref(1);

const companies = [
  {
    id: 1,
    initials: "ES",
    name: "Everest Sushi & Fusion",
    org: "937 219 997",
    role: "Leder",
    logoClass: "purple",
    roleClass: "purple-badge",
  },
  {
    id: 2,
    initials: "CT",
    name: "Cafe Testesen",
    org: "000 000 000",
    role: "Admin",
    logoClass: "green",
    roleClass: "green-badge",
  },
];
</script>

<template>
  <div class="container">
    <p class="step">Steg 2: Velg organisasjon</p>

    <div class="card">
      <h2>Velg virksomhet</h2>
      <p class="subtitle">Du har tilgang til flere virksomheter</p>

      <div
        v-for="company in companies"
        :key="company.id"
        class="company-card"
        :class="{selected: selectedCompany === company.id}"
        @click="selectedCompany = company.id"
      >
        <div class="company-left">
          <div class="logo-box" :class="company.logoClass">
            {{ company.initials }}
          </div>

          <div class="company-info">
            <h2>{{ company.name }}</h2>
            <p>Org: {{ company.org }}</p>
          </div>
        </div>

        <span class="role-badge" :class="company.roleClass">
            {{ company.role }}
          </span>
      </div>

      <button class="continue-btn" :disabled="!selectedCompany">
        Fortsett
      </button>
    </div>
  </div>
</template>

<style scoped>
.page {
  min-height: 100vh;
  background: #e9e7e2;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 32px 16px;
  font-family: Arial, Helvetica, sans-serif;
}

.wrapper {
  width: 100%;
  max-width: 680px;
  text-align: center;
}

.step {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 2px;
  color: #2f2f2f;
  margin-bottom: 24px;
}

.card {
  background: #ffffff;
  border: 2px solid #d8d8d8;
  border-radius: 24px;
  padding: 48px 36px 36px;
  box-sizing: border-box;
}

.card h1 {
  font-size: 34px;
  margin: 0;
  font-weight: 700;
  color: #000;
}

.subtitle {
  margin-top: 12px;
  margin-bottom: 36px;
  font-size: 22px;
  color: #555;
}

.company-card {
  width: 100%;
  border: 2px solid #dddddd;
  border-radius: 22px;
  padding: 28px 24px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-sizing: border-box;
  background: #fff;
  cursor: pointer;
  transition: all 0.2s ease;
}

.company-card.selected {
  border: 4px solid #5a4ccf;
}

.company-left {
  display: flex;
  align-items: center;
  gap: 20px;
  text-align: left;
}

.logo-box {
  width: 84px;
  height: 84px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 700;
}

.logo-box.purple {
  background: #ece9fb;
  color: #5a4ccf;
}

.logo-box.green {
  background: #dff0ea;
  color: #0f5f58;
}

.company-info h2 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 700;
  color: #000;
  line-height: 1.1;
}

.company-info p {
  margin: 0;
  font-size: 20px;
  color: #555;
}

.role-badge {
  padding: 10px 18px;
  border-radius: 999px;
  font-size: 18px;
  font-weight: 600;
  white-space: nowrap;
}

.purple-badge {
  background: #f0edff;
  color: #5a4ccf;
}

.green-badge {
  background: #dff0ea;
  color: #0f5f58;
}

.continue-btn {
  width: 100%;
  margin-top: 16px;
  padding: 22px;
  border: none;
  border-radius: 20px;
  background: #5a4ccf;
  color: white;
  font-size: 28px;
  font-weight: 700;
  cursor: pointer;
}

.continue-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

@media (max-width: 640px) {
  .card {
    padding: 32px 20px 24px;
  }

  .card h1 {
    font-size: 28px;
  }

  .subtitle {
    font-size: 18px;
  }

  .company-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .company-info h2 {
    font-size: 22px;
  }

  .company-info p {
    font-size: 18px;
  }

  .continue-btn {
    font-size: 24px;
  }
}
</style>
