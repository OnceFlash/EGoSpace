import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    language: 'us-en',
  },
  mutations: {
    SET_LANGUAGE(state, lang) {
      state.language = lang;
    },
  },
  actions: {
    setLanguage({ commit }, lang) {
      commit('SET_LANGUAGE', lang);
    },
  },
  getters: {
    currentLanguage: (state) => state.language,
  },
});