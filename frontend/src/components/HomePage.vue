<template>
    <div class="container hero-bg justify-content-center align-items-center d-flex min-h-100">
        <div class="text-center  mt-5 ">
            <div>
                <h1>Tere tulemast maailma parima restorani lehele!</h1>
            </div>
            <router-link to="/booking">
                <button class="btn btn-primary mt-3">Mine broneerimise lehele</button>
            </router-link>
        </div>
    </div>
</template>

<script setup>
</script>

<style scoped>
h1 {
     color: #cfa33d;
     
}
.hero-bg {
  height: 100vh; 
}
</style>