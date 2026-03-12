<template>
  <div class="container">
    <div class="booking-form p-4 border rounded">
      <h2 class="text-center mb-4">Broneeri laud</h2>
      <form @submit.prevent="submitBooking">
        <div class="mb-3">
          <input type="text" v-model="name" class="form-control" placeholder="Nimi" required />
        </div>
        <div class="mb-3">
          <input type="number" v-model="seat" class="form-control" placeholder="Inimesi" required />
        </div>
        <div class="mb-3">
          <label class="form-label">Asukoht</label>
          <div>
            <div class="form-check form-check-inline">
              <input class="form-check-input" type="radio" name="locationOptions" id="locationInside" value="main"
                v-model="location" checked>
              <label class="form-check-label" for="locationInside">Siseruum</label>
            </div>
            <div class="form-check form-check-inline">
              <input class="form-check-input" type="radio" name="locationOptions" id="locationTerrace" value="out"
                v-model="location">
              <label class="form-check-label" for="locationTerrace">Terrass</label>
            </div>
          </div>
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
          <Datepicker v-model="date" format="dd-MM-yyyy" :lower-limit="today" :locale="et" />
        </div>
        <div class="mb-3">
          <label class="form-label">Kellaaeg</label>
          <select v-model="time" class="form-select" required>
            <option value="" disabled>Vali kellaaeg</option>
            <option v-for="t in times" :key="t" :value="t">{{ t }}</option>
          </select>
        </div>
        <button type="submit" class="btn btn-primary w-100">Otsi vabu laudu</button>
      </form>
    </div>
    <div class="map-area p-4 border rounded" v-if="submitted">
      <TableSelector :recommended-ids="recommendedTables" :booked-ids="bookedTables"
        @table-selected="handleTableSelection"></TableSelector>
      <button class="btn btn-primary mt-3 w-100" @click="addReservation" :disabled="!selectedTableId">
        Broneeri
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import Datepicker from 'vue3-datepicker'
import { et } from 'date-fns/locale'
import TableSelector from './TableSelector.vue';
import axios from 'axios';
import { format, isToday } from 'date-fns';


const time = ref('')
const allTimes = []
// Gemini was used for validating date and time to be in the future
for (let h = 10; h <= 22; h++) {
  allTimes.push(`${String(h).padStart(2, '0')}:00`)
  if (h < 22) allTimes.push(`${String(h).padStart(2, '0')}:30`)
}

const today = new Date();
today.setHours(0, 0, 0, 0);

const times = computed(() => {
  if (!date.value || !isToday(date.value)) {
    return allTimes;
  }

  const now = new Date();
  const currentHour = now.getHours();
  const currentMinutes = now.getMinutes();

  return allTimes.filter(t => {
    const [hour, minute] = t.split(':').map(Number);
    if (hour > currentHour) {
      return true;
    }
    return hour === currentHour && minute > currentMinutes;
  });
});

const name = ref('')
const date = ref(new Date())
const submitted = ref(false)
const recommendedTables = ref([])
const bookedTables = ref([])
const window = ref(false)
const kidsCorner = ref(false)
const accessible = ref(false)
const location = ref('main')
const seat = ref(null)
const selectedTableId = ref(null)
const router = useRouter()

function handleTableSelection(tableId) {
  selectedTableId.value = tableId;
}

watch(date, () => {
  if (time.value && !times.value.includes(time.value)) {
    time.value = '';
  }
});

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
    console.log("res ", reservationData);
    await axios.post('http://localhost:8080/bookings', reservationData);
    router.push({
      path: '/conformation',
      state: { reservationData }
    });
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
  console.log(new Date().toISOString().split('-'));
  // Värskendame laudade vaadet iga otsinguga
  submitted.value = false;
  selectedTableId.value = null; // Nulli valik uuel otsingul
  try {
    const formattedDate = format(date.value, 'yyyy-MM-dd');

    console.log(seat.value,
      kidsCorner.value,
      window.value,
      accessible.value,
      location.value,
      formattedDate,
      time.value,);
    const response = await axios.post('http://localhost:8080/available-tables', {
      seats: seat.value,
      kids: kidsCorner.value,
      hasWindow: window.value,
      accessible: accessible.value,
      location: location.value,
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
  margin: 0 auto;
  width: 100%;
  background-color: rgba(240, 248, 255, 0.95);
}

.container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 1rem;
  width: 100%;
  box-sizing: border-box;
  min-height: 100vh;
}

.map-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 1rem;
  background-color: rgba(240, 248, 255, 0.95);
}
.booking-form form button[type="submit"] {
    grid-column: 1 / -1;
    margin-top: 1rem;
  }

/* Stiilid laiematele ekraanidele (nt desktop alates 1200px) */
@media (min-width: 1200px) {
  h2 {
    padding-bottom: 32px;
  }
  .container {
    flex-direction: row;
    align-items: center;
    /* Tsentreerib plokid vertikaalselt */
    justify-content: center;
    gap: 2rem;
  }

  .booking-form {
    margin: 0;
    flex-basis: 600px;
    flex-shrink: 0;
    max-width: 600px;
  }

  .booking-form form {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 0 1.5rem;
  }

  

  .map-area {
    margin: 0;
    flex-basis: 600px;
    flex-shrink: 0;
    max-width: 600px;
  }

  .booking-form,
  .map-area {
    display: flex;
    flex-direction: column;
    justify-content: center;
    /* Tsentreerib sisu vertikaalselt */
  }
}
</style>