import Vue from 'vue';
import VueI18n from 'vue-i18n';
import langs from './langs';

Vue.use(VueI18n);

const i18n = new VueI18n({
  locale: 'us-en',
  fallbackLocale: 'zh-cn',
  messages: langs,
});

export default i18n;