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
      <TableSelector :recommended-ids="recommendedTables" :initial-selection-ids="initialSelection"
        :booked-ids="bookedTables" :user-seats="seat" :is-combined="isCombined" @table-selected="handleTableSelection"></TableSelector>
      <button class="btn btn-primary mt-3 w-100" @click="addReservation" :disabled="selectedTableIds.length === 0">
        Broneeri
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import Datepicker from 'vue3-datepicker'
// Import Estonian locale for the date picker.
import { et } from 'date-fns/locale'
import TableSelector from './TableSelector.vue';
import axios from 'axios';
import { format, isToday } from 'date-fns';

// --- Time Selection Logic ---
const time = ref('')
const allTimes = []
// Generate time slots from 10:00 to 22:30.
for (let h = 10; h <= 22; h++) {
  allTimes.push(`${String(h).padStart(2, '0')}:00`)
  if (h < 22) allTimes.push(`${String(h).padStart(2, '0')}:30`)
}

// --- Date Selection Logic ---
// Set the minimum selectable date to today.
const today = new Date();
today.setHours(0, 0, 0, 0);

const times = computed(() => {
  if (!date.value || !isToday(date.value)) {
    return allTimes;
  }

  // If the selected date is today, filter out past time slots.
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

// --- Form State ---
const name = ref('')
const date = ref(new Date())
const window = ref(false)
const kidsCorner = ref(false)
const accessible = ref(false)
const location = ref('main')
const seat = ref(null)

// --- Table Selection State ---
const submitted = ref(false) // Controls visibility of the table selector.
const recommendedTables = ref([]) // All tables recommended by the backend.
const bookedTables = ref([]) // Tables that are booked for the selected time.
const selectedTableIds = ref([])
const initialSelection = ref([]) // Tables to be pre-selected (best fit or combination).
const isCombined = ref(false)

const router = useRouter()

// Event handler for when tables are selected in the TableSelector component.
function handleTableSelection(ids) {
  selectedTableIds.value = ids;
}

// Watch for changes in the selected date.
watch(date, () => {
  // If the date changes, reset the time if the old time is no longer valid.
  if (time.value && !times.value.includes(time.value)) {
    time.value = '';
  }
});

// --- Form Actions ---

/**
 * Creates the reservation(s) by sending POST requests to the backend.
 */
async function addReservation() {
  if (selectedTableIds.value.length === 0) {
    alert("Palun vali laud kaardilt.");
    return;
  }
  // Basic validation.
  if (!name.value || !date.value || !time.value) {
    alert("Nimi, kuupäev ja kellaaeg on kohustuslikud.");
    return;
  }

  try {
    const formattedDate = format(date.value, 'yyyy-MM-dd');

    // Common data for the confirmation page and for each booking request.
    const confirmationData = {
      customerName: name.value,
      date: formattedDate,
      time: time.value,
      seats: seat.value
    };

    // Create an array of booking promises, one for each selected table.
    const bookingPromises = selectedTableIds.value.map(tableId => {
      const singleReservationData = { ...confirmationData, tableId: tableId };
      return axios.post('http://localhost:8080/bookings', singleReservationData);
    });

    // Wait for all booking requests to complete successfully.
    await Promise.all(bookingPromises);

    // Navigate to the confirmation page, passing reservation data via history state.
    router.push({
      path: '/conformation',
      state: { reservationData: confirmationData }
    });
  } catch (error) {
    console.error("Broneerimisel tekkis viga:", error);
    alert("Broneerimisel tekkis viga. Palun proovi uuesti.");
  }
}

/**
 * Submits the search form to find available tables.
 */
async function submitBooking() {
  // Basic validation.
  if (!date.value || !time.value) {
    alert("Palun valige kuupäev ja kellaaeg.");
    return;
  }
   if (seat.value <= 0) {
    alert("Palun lisage inimesi!");
    return;
  }
  // Reset the table view on each new search.
  submitted.value = false;
  selectedTableIds.value = [];
  initialSelection.value = [];
  isCombined.value = false;
  try {
    const formattedDate = format(date.value, 'yyyy-MM-dd');


    const response = await axios.post('http://localhost:8080/available-tables', {
      seats: seat.value,
      kids: kidsCorner.value,
      hasWindow: window.value,
      accessible: accessible.value,
      location: location.value,
      date: formattedDate,
      time: time.value,
    });

    const searchResult = response.data;
    console.log("Recommended tables:", searchResult.tables);
    console.log("Is combined solution:", searchResult.combined);
    console.log("Booked table IDs:", searchResult.bookedTableIds);

    // If no tables are found, inform the user and stop.
    if (!searchResult.tables || searchResult.tables.length === 0) {
      alert("Antud valikutega vabu laudu ei leitud. Palun proovige teist aega või kuupäeva.");
      return;
    }

    // Set all recommended tables for highlighting
    recommendedTables.value = searchResult.tables.map(t => t.id);

    // Determine which tables to pre-select based on whether it's a combined solution
    if (searchResult.combined) {
      initialSelection.value = searchResult.tables.map(t => t.id);
    } else {
      initialSelection.value = [searchResult.tables[0].id];
    }

    bookedTables.value = searchResult.bookedTableIds;
    isCombined.value = searchResult.combined;

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