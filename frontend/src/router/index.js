import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../components/HomePage.vue'
import BookingForm from '../components/BookingForm.vue'
import TableSelector from '../components/TableSelector.vue'
import ConformationView from '../components/ConformationView.vue'

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
    },
    {
      path: '/tables',
      name: 'tables',
      component: TableSelector
    }
    ,
    {
      path: '/conformation',
      name: 'conformation',
      component: ConformationView
    }
  
  ]
})

export default router