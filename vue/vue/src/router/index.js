import {createRouter, createWebHistory} from 'vue-router'
import Home from "@/views/home/Home";
import Layout from "@/layout/Layout";
import home from "@/views/home/Home";
import request from "@/utils/request";


const routes = [
    {
        path: '/',
        name: 'Home',
        component: home
    },
    {
        path: '/layout',
        name: 'Layout',
        component: Layout,
        redirect: "/",
        children: [
            {
                path: '/',
                name: 'Home',
                component: () => import("@/views/home/Home")
            },
        ]
    },
    {
        path: '/login',
        name: 'Login',
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () => import('@/views/login/Login')
    },
    {
        path: '/person',
        name: 'Person',
        component: () => import('@/views/Person')
    },
    {
        path: '/password',
        name: 'Password',
        component: () => import('@/views/Password')
    },
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

// router.beforeEach((to, from, next) => {
//     if (to.path === '/login' || to.path === '/register') {
//         localStorage.removeItem("user")
//         sessionStorage.removeItem("user")
//         next()
//         return
//     }
//     let user = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null
//     //let user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : null
//     //console.log(user)
//     if (!user) {
//         next("/login")
//     } else {
//         next()
//     }
// })
/*router.beforeEach((to, from, next) => {
    if (to.path.startsWith('/login')) {
        localStorage.removeItem('user')
        next()
    } else {
        let admin = JSON.parse(localStorage.getItem('user'))
        if (!admin) {
            next({path: '/login'})
        } else {
            //校验token合法性
            axios({
                url:'http://localhost:8080/checkToken',
                method:'get',
                headers:{
                    token:admin.token
                }
            }).then((response) => {
                console.log(response.data)
                if(!response.data){
                    console.log('校验失败')
                    next({path: '/error'})
                }
            })
            next()
        }
    }
})*/
export default router
