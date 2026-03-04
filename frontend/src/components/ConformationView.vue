<template>
    <div class="container hero-bg justify-content-center align-items-center d-flex min-h-100" :style="{ backgroundImage: `url(${hero})` }">
        <div class="text-center  mt-5 ">
            <div>
                <h1>Kohtumiseni!</h1>
            </div>
            
        </div>
    </div>
</template>

<script setup>
import hero from '../assets/hero.jpg'
</script>

<style scoped>
h1 {
    color: #42b983;
}
.hero-bg {
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  height: 100vh; 
}
</style>