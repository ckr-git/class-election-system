import Vue from 'vue'
import Vuex from 'vuex'
import Cookies from 'js-cookie'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    token: Cookies.get('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}')
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      Cookies.set('token', token, { expires: 1 })
    },
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },
    LOGOUT(state) {
      state.token = ''
      state.userInfo = {}
      Cookies.remove('token')
      localStorage.removeItem('userInfo')
    }
  },
  actions: {
    login({ commit }, { token, userInfo }) {
      commit('SET_TOKEN', token)
      commit('SET_USER_INFO', userInfo)
    },
    logout({ commit }) {
      commit('LOGOUT')
    }
  }
})
