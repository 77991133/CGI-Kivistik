<script setup>
</script>

<template>
  <router-view />
</template>

<style>
/* Make the background image cover the entire app */
#app {
  background-image: url('./assets/hero.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed; /* Keeps the background fixed during scroll */
  min-height: 100vh;
}
</style>