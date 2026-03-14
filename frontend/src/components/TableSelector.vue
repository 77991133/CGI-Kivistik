<template>
  <div>
    <h2 class="text-center mb-3">Vali kohad</h2>
    <div ref="wrapper" class="map-wrapper" v-html="svgContent">

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watchEffect, watch } from 'vue'
// Import the raw SVG content of the floor plan.
import map from '../assets/floorPlan.svg?raw'
import axios from "axios"

// Reactive reference to hold the SVG content.
const svgContent = ref(map)
// Reactive reference to hold the SVG IDs of the currently selected tables.
const selectedTables = ref([])

// Define component props to receive data from the parent (BookingForm.vue).
const props = defineProps({
  // An array of table IDs that are already booked for the selected time.
  bookedIds: {
    type: Array,
    default: () => []
  },
  // An array of table IDs that are recommended by the backend.
  recommendedIds: {
    type: Array,
    default: () => []
  },
  // An array of table IDs that should be selected by default when the component mounts.
  // This can be a single best table or a combination of tables.
  initialSelectionIds: {
    type: Array,
    default: () => []
  },
  // Number of people in the booking
  userSeats: {
    type: Number,
    default: 0
  },
  isCombined: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['table-selected'])
// A template ref to get direct access to the wrapper div element in the DOM.
const wrapper = ref(null)

// Watch for changes in the selectedTables array.
watch(selectedTables, (newSvgIds) => {
  // When a selection is made, parse the real table IDs and emit them to the parent.
  if (newSvgIds && newSvgIds.length > 0) {
    const ids = newSvgIds.map(id => parseTable(id).id);
    emit('table-selected', ids);
  }
})



// onMounted is a lifecycle hook that runs after the component's DOM has been created.
onMounted(() => {
  if (!wrapper.value) return;

  // Add a single click event listener to the wrapper element to handle table clicks.
  wrapper.value.addEventListener("click", (e) => {

    // Find the closest parent <g> element that represents a table.
    const group = e.target.closest("g[id^='t']")
    // Do nothing if the click was not on a table or if the table is already booked.
    if (!group || group.classList.contains('booked')) return

    const clickedId = group.id
    const clickedTableData = parseTable(clickedId)

    // If already selected, deselect it
    if (selectedTables.value.includes(clickedId)) {
      selectedTables.value = selectedTables.value.filter(id => id !== clickedId)
      return
    }

    // If the user clicks a table that is not part of the recommended set,
    // treat it as a manual override, replacing the current selection.
    if (!props.recommendedIds.includes(clickedTableData.id)) {
      // If we found valid single tables (combined is false), prevent selecting non-recommended ones
      if (!props.isCombined) {
        return
      }
      selectedTables.value = [clickedId]
      return
    }

    // --- Logic for managing recommended combinations ---
    // Add new table to selection
    let newSelection = [...selectedTables.value, clickedId]

    // If we know the number of seats needed, manage the selection queue
    if (props.userSeats > 0) {
      const calculateTotalSeats = (ids) => ids.reduce((sum, id) => sum + (parseTable(id).seats || 0), 0)
      
      // Try to remove tables from the beginning if they are no longer needed
      while (newSelection.length > 1 && calculateTotalSeats(newSelection) - (parseTable(newSelection[0]).seats || 0) >= props.userSeats) {
        newSelection.shift()
      }
    }

    selectedTables.value = newSelection

  })

  // Automatically select the initial tables provided by the parent component.
  if (props.initialSelectionIds.length > 0) {
    const groups = wrapper.value.querySelectorAll("svg g[id^='t']");
    const recommendedGroups = [];
    for (const g of groups) {
      // Find the corresponding SVG group for each initial selection ID.
      if (props.initialSelectionIds.includes(parseTable(g.id).id)) {
        recommendedGroups.push(g.id);
      }
    }
    selectedTables.value = recommendedGroups;
  }

  // importTables() // This is a helper function to populate the backend DB from the SVG.

})

// watchEffect runs immediately and re-runs whenever its dependencies change.
// It's used here to dynamically apply CSS classes to the SVG tables.
watchEffect(() => {
  if (!wrapper.value) return;

  const groups = wrapper.value.querySelectorAll("svg g[id^='t']");
  groups.forEach(g => {
    const tableId = parseTable(g.id).id;
    const isSelected = selectedTables.value.includes(g.id);

    g.classList.remove('booked', 'selected', 'recommended', 'best');

    // Apply 'booked' class if the table is in the bookedIds list.
    if (tableId !== null && props.bookedIds.includes(tableId)) {
      g.classList.add('booked');
    // Apply 'selected' class if the table is currently selected.
    } else if (isSelected) {
      g.classList.add('selected');
    // Apply 'recommended' class if the table is in the recommendedIds list.
    } else if (tableId !== null && props.recommendedIds.includes(tableId)) {
      g.classList.add('recommended');
    }
  });
});

/**
 * Parses the table properties from the SVG group ID string.
 * NOTE: This is a fragile approach. Ideally, all table properties should be fetched
 * from the backend, and the SVG ID should only be used to link the DOM element
 * to the data object. This would decouple the frontend logic from the SVG structure.
 * @param {string} id The SVG group ID (e.g., "t1-s4-w1-lmain").
 * @returns {object} A table object with parsed properties.
 */
function parseTable(id) {

  const parts = id.split("-")
  const table = {
    id: null,
    seats: null,
    hasWindow: false,
    accessible: false,
    location: "",
    kids: false,
    neighbours: []
  }

  // Loop through each part of the ID and parse the property.
  parts.forEach(p => {

    if (p.startsWith("t"))
      table.id = parseInt(p.slice(1))

    if (p.startsWith("s"))
      table.seats = parseInt(p.slice(1))

    if (p.startsWith("w"))
      table.hasWindow = p.slice(1) === "1"

    if (p.startsWith("a"))
      table.accessible = p.slice(1) === "1"

    if (p.startsWith("k"))
      table.kids = p.slice(1) === "1"

    if (p.startsWith("l"))
      table.location = p.slice(1)

    if (p.startsWith("n"))
      table.neighbours = p.slice(1).split(",").map(n => parseInt(n))

  })

  return table
}

/**
 * A utility function to send table data parsed from the SVG to the backend.
 * This is useful for a one-time database population.
 */
function importTables() {

  const groups = wrapper.value.querySelectorAll("svg g[id^='t']")

  groups.forEach(g => {

    const table = parseTable(g.id)
    console.log(table)
    if (table.id != null && table.seats != null) {
      axios.post("http://localhost:8080/tables", table)
    }


  })

}
</script>

<style>

.map-wrapper svg {
  max-width: 600px;
  width: 100%;
  height: 286px;
}

g[id^="t"] {
  cursor: pointer;
  transition: 0.2s ease;
}

g[id^="t"]:hover * {
  fill: #0d6efd !important;
}

g.selected * {
  fill: #198754 !important;
}

g.booked *,
g.booked:hover * {
  fill: #dc3545 !important;
  cursor: not-allowed;
}

g.recommended * {
  fill: #90EE90 !important;
}

g.best * {
  fill: #2def74 !important;
}
</style>