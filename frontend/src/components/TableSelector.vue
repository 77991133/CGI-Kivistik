<template>
  <div>
    <h2 class="text-center mb-3">Vali laud</h2>
    <div ref="wrapper" class="map-wrapper" v-html="svgContent">

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watchEffect, watch } from 'vue'
import map from '../assets/floorPlan.svg?raw'
import axios from "axios"
const svgContent = ref(map)
const selectedTable = ref(null)

const props = defineProps({
  bookedIds: {
    type: Array,
    default: () => []
  },
  recommendedIds: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['table-selected'])

const wrapper = ref(null)

watch(selectedTable, (newSvgId) => {
  if (newSvgId) {
    const table = parseTable(newSvgId);
    emit('table-selected', table.id);
  }
})



onMounted(() => {
  if (!wrapper.value) return;

  wrapper.value.addEventListener("click", (e) => {

    const group = e.target.closest("g[id^='t']")
    // Ära lase broneeritud lauda valida
    if (!group || group.classList.contains('booked')) return

    selectedTable.value = group.id

  })

  // Vali automaatselt parim laud (esimene soovitus)
  if (props.recommendedIds.length > 0) {
    const bestId = props.recommendedIds[0];
    const groups = wrapper.value.querySelectorAll("svg g[id^='t']");
    for (const g of groups) {
      if (parseTable(g.id).id === bestId) {
        selectedTable.value = g.id;
        break;
      }
    }
  }

  // importTables()

})

watchEffect(() => {
  if (!wrapper.value) return;

  const groups = wrapper.value.querySelectorAll("svg g[id^='t']");
  groups.forEach(g => {
    const tableId = parseTable(g.id).id;
    const isSelected = selectedTable.value === g.id;

    g.classList.remove('booked', 'selected', 'recommended', 'best');

    if (tableId !== null && props.bookedIds.includes(tableId)) {
      g.classList.add('booked');
    } else if (isSelected) {
      g.classList.add('selected');
    } else if (tableId !== null && props.recommendedIds.length > 0 && props.recommendedIds[0] === tableId) {
      g.classList.add('best');
    } else if (tableId !== null && props.recommendedIds.includes(tableId)) {
      g.classList.add('recommended');
    }
  });
});

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
/* Make SVG visible and responsive */
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

/* Stiil broneeritud laudadele */
g.booked *,
g.booked:hover * {
  fill: #dc3545 !important;
  /* Punane */
  cursor: not-allowed;
}

/* Stiil soovitatud laudadele */
g.recommended * {
  fill: #0dcaf0 !important;
  /* Hele-sinine (cyan) */
}

g.best * {
  fill: #2def74 !important;
}
</style>