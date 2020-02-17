<template>
    <v-app>
        <v-tabs-items v-model="tab">
            <v-tab-item>
                <Controller />
            </v-tab-item>

            <v-tab-item>
                <Setting />
            </v-tab-item>
        </v-tabs-items>

        <v-bottom-navigation app grow shift dark v-model="tab">
            <v-btn>
                <span>遙控器</span>
                <v-icon>mdi-remote</v-icon>
            </v-btn>

            <v-btn>
                <span>設定</span>
                <v-icon>mdi-settings</v-icon>
            </v-btn>
        </v-bottom-navigation>

        <v-dialog persistent max-width="290" v-model="dialog">
            <v-card>
                <v-card-title>
                    <v-icon color="yellow darken-3">mdi-alert-outline</v-icon>
                    開啟藍芽
                </v-card-title>
                <v-card-text>本軟體必須開啟藍芽才能使用</v-card-text>
                <v-card-text></v-card-text>
                <v-card-actions>
                    <v-spacer />
                    <v-btn text color="red darken-1" :disabled="enabling" @click="exit">關閉程式</v-btn>
                    <v-btn text color="green darken-1" :loading="enabling" @click="enableBluetooth">
                        開啟藍芽
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-app>
</template>

<script lang="ts">
import Vue from 'vue'
import { mapState, mapMutations } from 'vuex'

import Controller from '@/views/Controller.vue'
import Setting from '@/views/Setting.vue'

import { NativeEvent } from './types'

export default Vue.extend({
    name: 'App',
    components: { Controller, Setting },
    data: () => ({
        tab: 0,
        dialog: false,
        enabling: false
    }),
    computed: {},
    methods: {
        ...mapMutations('bluetooth', ['changeState']),
        async enableBluetooth() {
            this.enabling = true
            await $Native.Bluetooth.enable()
            this.enabling = false
            this.dialog = false
        },
        exit() {
            $Native.Application.exit()
        }
    },
    mounted() {
        $NativeListener.on(NativeEvent.DISABLING_BLUETOOTH, () => (this.dialog = true))
        $NativeListener.on(NativeEvent.ENABLED_BLUETOOTH, () => {
            this.enabling = false
            this.dialog = false
        })
        $NativeListener.on(NativeEvent.STATE_CHANGE, event => {
            this.changeState(event.state)
        })
    }
})
</script>

<style lang="scss">
@font-face {
    font-family: 'NotoSans';
    src: url('assets/fonts/NotoSansCJKtc-Light.otf');
}
* {
    font-family: 'NotoSans';
}
</style>

<style lang="scss" scoped>
.v-tabs-items,
.v-window-item {
    height: 100%;
}
</style>
