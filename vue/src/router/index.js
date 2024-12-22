import Vue from 'vue';
import VueRouter from 'vue-router';
import i18n from '@/i18n';
import ElementLocale from 'element-ui/lib/locale';
import enLocale from 'element-ui/lib/locale/lang/en';
import zhLocale from 'element-ui/lib/locale/lang/zh-CN';
import HomeView from '@/views/HomeView.vue';
import UserView from '@/views/UserView.vue';
import AdminView from '@/views/AdminView.vue';

Vue.use(VueRouter);

const routes = [
  {
    path: '/',
    redirect: '/us-en/home',
  },
  {
    path: '/:lang(us-en|zh-cn)/home',
    name: 'home',
    component: HomeView,
  },
  {
    path: '/:lang(us-en|zh-cn)/user',
    name: 'user',
    component: UserView,
  },
  {
    path: '/:lang(us-en|zh-cn)/admin',
    name: 'admin',
    component: AdminView,
  },
];

const router = new VueRouter({
  mode: 'history',
  routes,
});

router.beforeEach((to, from, next) => {
  const lang = to.params.lang || 'us-en';
  if (i18n.locale !== lang) {
    i18n.locale = lang;
  }
  ElementLocale.use(lang === 'us-en' ? enLocale : zhLocale);
  next();
});

export default router;