import Vue from 'vue';
import VueRouter from 'vue-router';
import Now from '../views/After/Now.vue';
import VueMaterial from 'vue-material';
import 'vue-material/dist/vue-material.min.css';
import 'vue-material/dist/theme/default.css'; // This line here
import VueToastr from 'vue-toastr';

//webRTC
import Home from '@/views/Home.vue';
import store from '@/store';

//Main
import Main from '@/views/Main.vue';

//accounts
import Login from '@/views/accounts/Login.vue';
import Join from '@/views/accounts/Join.vue';
import Klogin from '@/views/accounts/kakaologin.vue';

//Vote
import Vote from '@/views/After/Vote.vue';
// import Wallet from '../views/After/Wallet.vue'
// import Blockduce from '../views/After/Blockduce.vue'

//Blockchain
import Account from '@/views/Account.vue';
import Election from '@/views/Election.vue';
import Information from '@/views/Information.vue';

//pagenotfound
import PageNotFound from '@/views/PageNotFound'

Vue.use(VueMaterial);
Vue.use(VueToastr, {
  defaultPosition: 'toast-top-left',
  defaultTimeout: 3000,
  defaultProgressBar: false,
  defaultProgressBarValue: 0,
});
Vue.use(VueRouter);

const routes = [
  {
    path: '/home',
    name: 'home',
    component: Home,
    beforeEnter: (to, from, next) => {
      store.state.room && store.state.username ? next('/chat') : next();
    },
  },
  {
    path: '/chat',
    name: 'chat',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ './../views/Chat.vue'),
    beforeEnter: (to, from, next) => {
      !store.state.room && !store.state.username ? next('/') : next();
    },
  },
  //Main
  {
    path: '/',
    name: 'Main',
    component: Main,
  },
  //accounts
  {
    path: '/login',
    name: 'Login',
    component: Login,
  },
  {
    path: '/join',
    name: 'Join',
    component: Join,
  },
  {
    path: '/kakaologin',
    name: 'Klogin',

    component: Klogin,
  },

  
  //Vote
  {
    path: '/after/vote',
    name: 'Vote',
    component: Vote,
  },
  {
    path: '/after/now',
    name: 'Now',
    component: Now,
  },
  //Blockchain
  {
    path: '/account',
    name: 'account',
    component: Account,
  },
  {
    path: '/election',
    name: 'election',
    component: Election,
  },
  {
    path: '/information',
    name: 'information',
    component: Information,
  },
 
  // {
  //   path: '/after/wallet',
  //   name: 'Wallet',
  //   component: Wallet
  // },

  // {
  //   path: '/after/blockduce',
  //   name: 'Blockduce',
  //   component: Blockduce
  // },

    // 404 Pages
    {
      path: '*',
      name: 'PageNotFound',
      component: PageNotFound
  
    }
];

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes,
  scrollBehavior (to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { x: 0, y: 0 }
    }
  }
});

// router.beforeEach((to, from, next) => {
//   const publicPages = ['Login', 'Join', 'Klogin'] // Login 안해도 됨
//   const authPages = ['Login', 'Join', 'Klogin'] // Login 되어있으면 안됨
//   // const pubicPages = ['Login', 'Signup'] // Login 안해도 됨
//   // const authPages = ['Login', 'Signup'] // Login 되어있으면 안됨
//   const authRequired = !publicPages.includes(to.name) // 로그인 해야하는 페이지면 true 반환
//   const unauthRequired = authPages.includes(to.name)
//   const isLoggedIn = Vue.$cookies.isKey('auth-token')

//   if (unauthRequired && isLoggedIn){
//     next('/')
//   }
  
//   if (authRequired && !isLoggedIn) {
//     next({ name: 'Login' })
//   } else {
//     next()
//   }
// })

export default router;
