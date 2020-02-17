import Vue from 'vue'
import VueRouter from 'vue-router'
import Controller from '../views/Controller.vue'
import Setting from '../views/Setting.vue'

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        name: 'Controller',
        component: Controller
    },
    {
        path: '/setting',
        name: 'Setting',
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: Setting
    }
]

const router = new VueRouter({
    routes
})

export default router
