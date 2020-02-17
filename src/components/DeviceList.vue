<template>
    <v-card dark v-touch="{ down: () => $emit('input', false) }">
        <v-expansion-panels accordion v-model="panel">
            <v-expansion-panel>
                <v-expansion-panel-header>
                    <v-btn icon height="48px" width="48px" @click.stop="$emit('input', false)">
                        <v-icon>mdi-close</v-icon>
                    </v-btn>
                    {{ panel === undefined ? '裝置列表' : '目前裝置' }}
                    <v-spacer />
                    <v-progress-circular indeterminate color="white" v-if="discovering" />
                </v-expansion-panel-header>
                <v-expansion-panel-content>
                    <v-card dark color="success" v-if="device">
                        <v-card-title> {{ device.name }} </v-card-title>
                        <v-card-subtitle> {{ device.address }} </v-card-subtitle>
                    </v-card>
                    <v-card dark color="warning" v-else>
                        <v-card-title> 尚未選擇裝置 </v-card-title>
                    </v-card>
                </v-expansion-panel-content>
            </v-expansion-panel>
        </v-expansion-panels>

        <v-list two-line subheader rounded>
            <v-subheader>
                可用裝置
                <v-spacer />
            </v-subheader>
            <v-divider />

            <v-slide-x-transition group>
                <v-list-item v-for="x in devices" :key="x.address" @click="select(x)">
                    <v-list-item-avatar>
                        <v-icon>{{ getIcon(x) }}</v-icon>
                    </v-list-item-avatar>

                    <v-list-item-content>
                        <v-list-item-title>{{ x.name }}</v-list-item-title>
                        <v-list-item-subtitle>{{ x.address }}</v-list-item-subtitle>
                    </v-list-item-content>

                    <v-list-item-action>
                        <v-btn icon>
                            <v-icon color="grey lighten-1">mdi-information</v-icon>
                        </v-btn>
                    </v-list-item-action>
                </v-list-item>
            </v-slide-x-transition>
        </v-list>

        <v-speed-dial bottom right direction="top" transition="slide-y-reverse-transition" v-model="fab">
            <template v-slot:activator>
                <v-btn v-model="fab" color="blue darken-2" dark fab>
                    <v-icon v-if="fab">mdi-close</v-icon>
                    <v-icon v-else>mdi-format-list-bulleted</v-icon>
                </v-btn>
            </template>

            <v-btn fab dark small color="green darken-1" @click="dialogs.edit = true">
                <v-icon>mdi-lead-pencil</v-icon>
            </v-btn>

            <v-btn fab dark small color="red darken-1" @click="reset">
                <v-icon>mdi-refresh</v-icon>
            </v-btn>

            <v-btn fab dark small color="yellow darken-2" @click="discover">
                <v-icon>
                    {{ discovering ? 'mdi-magnify-close' : 'mdi-magnify' }}
                </v-icon>
            </v-btn>
        </v-speed-dial>

        <v-dialog v-model="dialogs.confirm" max-width="290">
            <v-card class="mx-auto text-left">
                <v-card-title>變更設定</v-card-title>

                <v-card-text>
                    確定要將此裝置設為連線對象嗎?
                    <v-container class="pl-0">
                        <v-row no-gutters>
                            <v-col cols="3">
                                <v-card flat color="transparent" class="pa-2 grey--text text--darken-1">
                                    名稱:
                                </v-card>
                            </v-col>
                            <v-col>
                                <v-card flat color="transparent" class="pa-2 font-weight-bold orange--text text--darken-3">
                                    {{ selected.name }}
                                </v-card>
                            </v-col>
                        </v-row>

                        <v-row no-gutters>
                            <v-col cols="3">
                                <v-card flat color="transparent" class="pa-2 grey--text text--darken-1">
                                    MAC:
                                </v-card>
                            </v-col>
                            <v-col>
                                <v-card flat color="transparent" class="pa-2 font-weight-bold orange--text text--darken-3">
                                    {{ selected.address }}
                                </v-card>
                            </v-col>
                        </v-row>
                    </v-container>
                </v-card-text>

                <v-card-actions>
                    <v-spacer></v-spacer>

                    <v-btn color="green darken-1" text @click="dialogs.confirm = false">
                        取消
                    </v-btn>

                    <v-btn color="green darken-1" text @click="save">
                        確定
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

        <v-snackbar color="success" v-model="snackbar" :timeout="1500">
            已儲存
        </v-snackbar>
    </v-card>
</template>

<script lang="ts">
import Vue from 'vue'
import { mapState } from 'vuex'
import { NativeEvent, IDevice, DeviceType } from '@/types'

export default Vue.extend({
    name: 'DeviceList',
    data: () => ({
        devices: {} as { [key: string]: IDevice },
        discovering: false,
        fab: false,
        panel: undefined,
        dialogs: {
            confirm: false,
            edit: false
        },
        selected: {} as IDevice,
        snackbar: false
    }),
    computed: {
        ...mapState('setting', ['device'])
    },
    methods: {
        getIcon(x: IDevice) {
            return new Proxy(
                {
                    [DeviceType.COMPUTER]: 'mdi-monitor',
                    [DeviceType.PHONE]: 'mdi-cellphone',
                    [DeviceType.NETWORKING]: 'mdi-access-point-network',
                    [DeviceType.AUDIO_VIDEO]: 'mdi-headphones',
                    [DeviceType.PERIPHERAL]: 'mdi-printer-wireless',
                    [DeviceType.IMAGING]: 'mdi-camera-wireless',
                    [DeviceType.WEARABLE]: 'mdi-watch',
                    [DeviceType.TOY]: 'mdi-controller-classic',
                    [DeviceType.HEALTH]: 'mdi-heart-pulse',
                    [DeviceType.UNCATEGORIZED]: 'mdi-bluetooth'
                },
                { get: (target: any, name) => target[name] || target.UNCATEGORIZED }
            )[x.type]
        },
        async discover() {
            if (this.discovering) {
                await $Native.Bluetooth.cancelDiscovery()
            } else {
                this.discovering = true
                await $Native.Bluetooth.startDiscovery()
                this.discovering = false
            }
        },
        async getBondedDevices() {
            const array = await $Native.Bluetooth.getBondedDevices()
            array.map(x => this.$set(this.devices, x.address, x))
        },
        async reset() {
            this.devices = {}
            this.getBondedDevices()
            await $Native.Bluetooth.cancelDiscovery()
            this.discover()
        },
        select(device: IDevice) {
            this.dialogs.confirm = true
            this.selected = device
        },
        save() {
            this.dialogs.confirm = false
            this.$store.commit('setting/save', {
                key: 'device',
                value: this.selected
            })
            this.snackbar = true
        },
        edit() {}
    },
    mounted() {
        this.reset()
        $NativeListener.on(NativeEvent.FOUND_DEVICE, event => {
            const x = event!.device!
            this.$set(this.devices, x.address, x)
        })

        // for (let i = 0; i < 20; ++i) {
        //     const x = { name: `${i}`.repeat(8), address: `${i < 10 ? '0' + i : i}:9b:04:0a:eb:41` }
        //     this.$set(this.devices, x.address, x)
        // }
    }
})
</script>

<style lang="scss" scoped>
.v-speed-dial {
    position: fixed;
    z-index: 2;
}
.v-snack {
    z-index: 1;
}

.v-list {
    margin-top: 56px;
}

.v-expansion-panels {
    .v-expansion-panel {
        position: fixed;
        width: 100%;
        background-color: rgb(78, 100, 110) !important;
    }
    .v-expansion-panel-header {
        min-height: 56px;
        padding: 0 16px 0 16px;
        button {
            margin-right: 16px;
            flex: unset;
        }
    }
}
</style>
