<template>
  <div class="booking-form p-4 border rounded">
    <h2>Broneeri laud</h2>
    <form @submit.prevent="submitBooking">
      <div class="mb-3">
        <label class="form-label">Nimi</label>
        <input type="text" v-model="name" class="form-control" required />
      </div>
      <div>
        <label class="form-label">Akna äärde</label>
        <input type="checkbox" v-model="window" class="form-control" required />
      </div>

      <div class="mb-3">
        <label class="form-label">Kuupäev</label>
        <VueDatePicker v-model="date" :format="yyyy-MM-dd"></VueDatePicker>
      </div>

      <button type="submit" class="btn btn-primary">Broneeri</button>
    </form>

    <p v-if="submitted" class="mt-3 text-success">
      Broneering edukalt salvestatud: {{ name }} – {{ date }}
    </p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { VueDatePicker } from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css'

const name = ref('')
const date = ref(null)
const submitted = ref(false)

function submitBooking() {
  submitted.value = true
  console.log('Broneering:', name.value, date.value)
}
</script>

<style scoped>
.booking-form {
  max-width: 500px;
  margin: auto;
}
</style>