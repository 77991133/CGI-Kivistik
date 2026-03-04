<template>
  <div class="container">
    <div class="booking-form p-4 border rounded">
      <h2>Broneeri laud</h2>
      <form @submit.prevent="submitBooking">
        <div class="mb-3">
          <label class="form-label">Nimi</label>
          <input type="text" v-model="name" class="form-control" required />
        </div>
        <div class="form-check form-switch mb-2">
          <input class="form-check-input" type="checkbox" v-model="window" id="windowSwitch">
          <label class="form-check-label" for="windowSwitch">Akna äärde</label>
        </div>

        <div class="form-check form-switch mb-2">
          <input class="form-check-input" type="checkbox" v-model="kidsCorner" id="kidsSwitch">
          <label class="form-check-label" for="kidsSwitch">Lastenurga lähedale</label>
        </div>

        <div class="form-check form-switch mb-2">
          <input class="form-check-input" type="checkbox" v-model="accessible" id="accessibleSwitch">
          <label class="form-check-label" for="accessibleSwitch">Ratastooli juurdepääs</label>
        </div>
        <div class="mb-3">
          <label class="form-label">Kuupäev</label>
          <Datepicker v-model="date" format="dd-MM-yyyy" />
        </div>
        <div class="mb-3">
          <label class="form-label">Kellaaeg</label>
          <select v-model="time" class="form-select" required>
            <option value="" disabled>Vali kellaaeg</option>
            <option v-for="t in times" :key="t" :value="t">{{ t }}</option>
          </select>
        </div>
        <router-link to="/tables">
          <button type="submit" class="btn btn-primary">Broneeri</button>
        </router-link>
      </form>

      <p v-if="submitted" class="mt-3 text-success">
        Broneering edukalt salvestatud: {{ name }} – {{ date }} {{ time }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Datepicker from 'vue3-datepicker'


const time = ref('')
const times = []

for (let h = 10; h <= 22; h++) {
  times.push(`${String(h).padStart(2, '0')}:00`)
  if (h < 22) times.push(`${String(h).padStart(2, '0')}:30`)
}

const name = ref('')
const date = ref(null)
const submitted = ref(false)
const window = ref(false)
const kidsCorner = ref(false)
const accessible = ref(false)

function submitBooking() {
  submitted.value = true
  console.log({
    name: name.value,
    date: date.value,
    time: time.value,
    window: window.value,
    kidsCorner: kidsCorner.value,
    accessible: accessible.value
  })
}
</script>

<style scoped>
.booking-form {
  max-width: 500px;
  margin: auto;
}

.container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center
}
</style>