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

const svgContent = ref(map)
const selectedTable = ref(null)

onMounted(() => {
    const wrapper = document.querySelector('.map-wrapper')

    wrapper.addEventListener('click', (e) => {
        const group = e.target.closest('g[id^="table"]')
        if (!group) return

        selectedTable.value = group.id

        document
            .querySelectorAll('g[id^="table"]')
            .forEach(g => g.classList.remove('selected'))

        group.classList.add('selected')

        console.log('Valiti:', group.id)
    })
})
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