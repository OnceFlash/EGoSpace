import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import i18n from './i18n';

import HnnuIcon from './assets/Hnnu.png';

Vue.config.productionTip = false;

const link = document.createElement('link');
link.rel = 'icon';
link.type = 'image/png';
link.href = HnnuIcon;
document.head.appendChild(link);

Vue.use(ElementUI);

new Vue({
  router,
  store,
  i18n,
  render: (h) => h(App),
}).$mount('#app');