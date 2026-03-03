import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../components/HomePage.vue'
import BookingForm from '../components/BookingForm.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomePage
    },
    {
      path: '/booking',
      name: 'booking',
      component: BookingForm
    }
  ]
})

export default router