import Vue from 'vue'
import Vuetify from 'vuetify/lib'
import zhHant from 'vuetify/src/locale/zh-Hant'
import { Touch } from 'vuetify/lib/directives'

// import 'material-design-icons-iconfont/dist/material-design-icons.css'
import '@mdi/font/css/materialdesignicons.css'

Vue.use(Vuetify, {
    directives: { Touch }
})

export default new Vuetify({
    theme: {
        themes: {
            light: {
                primary: '#3f51b5',
                secondary: '#b0bec5',
                accent: '#8c9eff',
                error: '#b71c1c'
            }
        }
    },
    icons: { iconfont: 'mdi' },
    lang: {
        locales: { zhHant },
        current: 'zh-Hant'
    }
})
