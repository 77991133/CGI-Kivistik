<template>
    <div class="container">
        <h1>Sulle soovitatud laud:</h1>
        <div class="map-wrapper" v-html="svgContent"></div>
        <router-link to="/conformation">
            <button class="btn btn-primary mt-3">Broneeri</button>
        </router-link>
    </div>

</template>

<script setup>
import { ref, onMounted } from 'vue'
import map from '../assets/floorPlan.svg?raw'
import axios from "axios"
const svgContent = ref(map)
const selectedTable = ref(null)

onMounted(async () => {
    const wrapper = document.querySelector('.map-wrapper')

    wrapper.addEventListener('click', (e) => {
        const group = e.target.closest('g[id^="t"]')
        if (!group) return

        selectedTable.value = group.id

        document
            .querySelectorAll('g[id^="t"]')
            .forEach(g => g.classList.remove('selected'))

        group.classList.add('selected')

        console.log('Valiti:', group.id)
    })
    
    importTables()
    
})
function parseTable(id){

  const parts = id.split("-")

  const table = {
    id:null,
    seats:null,
    hasWindow:false,
    accessible:false,
    location:"",
    kids:false,
    neighbours:[]
  }

  parts.forEach(p=>{

    if(p.startsWith("t"))
      table.id = parseInt(p.slice(1))

    if(p.startsWith("s"))
      table.seats = parseInt(p.slice(1))

    if(p.startsWith("w"))
      table.hasWindow = p.slice(1) === "1"

    if(p.startsWith("a"))
      table.accessible = p.slice(1) === "1"

    if(p.startsWith("k"))
      table.kids = p.slice(1) === "1"

    if(p.startsWith("l"))
      table.location = p.slice(1)

    if(p.startsWith("n"))
      table.neighbours = p.slice(1).split(",").map(n=>parseInt(n))

  })

  return table
}

function importTables(){

  const groups = document.querySelectorAll("svg g")

  groups.forEach(g=>{

    const table = parseTable(g.id)
    console.log(table)
    if (table.id!=null) {
       axios.post("http://localhost:8080/tables",table) 
    }
    

  })

}
</script>

<style>
g[id^="table"] {
    cursor: pointer;
    transition: 0.2s ease;
}

g[id^="table"]:hover * {
    fill: #0d6efd !important;
}

g.selected * {
    fill: #198754 !important;
}
</style>