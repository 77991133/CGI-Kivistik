<template>
  <div class="container">
    <div class="booking-form p-4 border rounded">
      <h2>Broneeri laud</h2>
      <form @submit.prevent="submitBooking">
        <div class="mb-3">
          <label class="form-label">Nimi</label>
          <input type="text" v-model="name" class="form-control" required />
        </div>
        <div class="mb-3">
          <label class="form-label">Inimesi</label>
          <input type="number" v-model="seat" class="form-control" required />
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
        <button type="submit" class="btn btn-primary">Otsi vabu laudu</button>
      </form>
    </div>
    <TableSelector v-if="submitted" :recommended-ids="recommendedTables" :booked-ids="bookedTables" @table-selected="handleTableSelection"></TableSelector>
    <button v-if="submitted" class="btn btn-primary mt-3" @click="addReservation" :disabled="!selectedTableId">
      Broneeri
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Datepicker from 'vue3-datepicker'
import TableSelector from './TableSelector.vue';
import axios from 'axios';
import { format } from 'date-fns';


const time = ref('')
const times = []

for (let h = 10; h <= 22; h++) {
  times.push(`${String(h).padStart(2, '0')}:00`)
  if (h < 22) times.push(`${String(h).padStart(2, '0')}:30`)
}

const name = ref('')
const date = ref(null)
const submitted = ref(false)
const recommendedTables = ref([])
const bookedTables = ref([])
const window = ref(false)
const kidsCorner = ref(false)
const accessible = ref(false)
const seat = ref(null)
const selectedTableId = ref(null)
const router = useRouter()

function handleTableSelection(tableId) {
  selectedTableId.value = tableId;
}

async function addReservation() {
  if (!selectedTableId.value) {
    alert("Palun vali laud kaardilt.");
    return;
  }
  if (!name.value || !date.value || !time.value) {
    alert("Nimi, kuupäev ja kellaaeg on kohustuslikud.");
    return;
  }

  try {
    const formattedDate = format(date.value, 'yyyy-MM-dd');
    const reservationData = {
      customerName: name.value,
      tableId: selectedTableId.value,
      date: formattedDate,
      time: time.value,
      seats: seat.value
    };
    console.log("res " , reservationData);
    await axios.post('http://localhost:8080/bookings', reservationData);
    router.push('/conformation');
  } catch (error) {
    console.error("Broneerimisel tekkis viga:", error);
    alert("Broneerimisel tekkis viga. Palun proovi uuesti.");
  }
}

async function submitBooking() {
  if (!date.value || !time.value) {
    alert("Palun valige kuupäev ja kellaaeg.");
    return;
  }
  // Värskendame laudade vaadet iga otsinguga
  submitted.value = false;
  selectedTableId.value = null; // Nulli valik uuel otsingul
  try {
    const formattedDate = format(date.value, 'yyyy-MM-dd');

    console.log(seat.value,
      kidsCorner.value,
      window.value,
      accessible.value,
      "main",
      formattedDate,
      time.value,);
    const response = await axios.post('http://localhost:8080/available-tables', {
      seats: seat.value,
      kids: kidsCorner.value,
      hasWindow: window.value,
      accessible: accessible.value,
      location: "main",
      date: formattedDate,
      time: time.value,   
    });

    const availableTableIds = response.data.map(table => table.id);
    recommendedTables.value = availableTableIds;

    // Eeldame, et on olemas endpoint, mis tagastab KÕIK lauad.
    const allTablesResponse = await axios.get('http://localhost:8080/tables');
    const allTableIds = allTablesResponse.data.map(table => table.id);

    // Arvutame broneeritud lauad: kõik lauad, mis EI OLE vabade laudade nimekirjas.
    bookedTables.value = allTableIds.filter(id => !availableTableIds.includes(id));

    submitted.value = true;
  } catch (error) {
    console.error("Viga broneeringute pärimisel:", error);
    alert("Broneeringute pärimisel tekkis viga. Palun proovi uuesti.");
  }
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
  flex-direction: column;
  justify-content: center;
  align-items: center
}
</style>