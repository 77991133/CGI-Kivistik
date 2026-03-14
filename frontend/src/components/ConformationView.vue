<template>
    <div class="container hero-bg justify-content-center align-items-center d-flex min-h-100">
        <div class="text-center mt-5 card p-4" v-if="reservationData">
            <h1>Broneering kinnitatud!</h1>
            <p>Täname broneeringu eest, {{ reservationData.customerName }}!</p>
            <p>Ootame teid {{ reservationData.date }} kell {{ reservationData.time }}.</p>
            <p>Laud on broneeritud {{ reservationData.seats }} inimesele.</p>
            <p>Kohtumiseni!</p>
            <router-link to="/">
                <button class="btn btn-primary mt-3">Avalehele</button>
            </router-link>
        </div>
        <div class="text-center mt-5" v-else>
            <h1>Midagi läks valesti</h1>
            <p>Broneeringu andmeid ei leitud.</p>
        </div>
    </div>

</template>

<script setup>

const reservationData = history.state.reservationData;
</script>

<style scoped>
h1 {
    color: #cfa33d;
    margin: 1rem;
}

.hero-bg {
    height: 100vh;
}

.card {
    background-color: rgba(255, 255, 255, 0.9);
}
</style>