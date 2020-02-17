import Vue from 'vue'
import Vuex from 'vuex'

import bluetooth from './modules/bluetooth'
import setting from './modules/setting'

Vue.use(Vuex)

export default new Vuex.Store({
    state: {},
    mutations: {},
    actions: {},
    modules: {
        bluetooth,
        setting
    }
})
