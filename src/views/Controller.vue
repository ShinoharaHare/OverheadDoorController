<template>
    <v-content>
        <v-container grid-list-md>
            <v-row>
                <v-col align="center">
                    <ConnectButton @click="connect" />
                </v-col>
            </v-row>

            <v-row class="mt-8">
                <v-col align="center">
                    <v-card dark height="inherit">
                        <v-card-actions class="justify-center" v-for="btn in buttons" :key="btn.signal">
                            <v-btn fab x-large class="hvr-outline-out hvr-bounce-out" @click="send(btn.signal)">
                                <v-icon>{{ btn.icon }}</v-icon>
                            </v-btn>
                        </v-card-actions>
                    </v-card>
                </v-col>
            </v-row>
        </v-container>

        <v-snackbar class="mb-12" color="blue-grey darken-3" :timeout="0" :value="connecting || connected">
            <v-progress-circular indeterminate color="white" v-if="connecting" />
            <span class="ml-4">{{ connectionMsg }}</span>
            <v-spacer v-if="connecting" />
            <v-btn text v-else color="red" @click="disconnect">中斷連線</v-btn>
        </v-snackbar>

        <v-snackbar class="mb-12" v-model="snackbar" :timeout="1500" :color="color">
            {{ message }}
        </v-snackbar>
    </v-content>
</template>

<script lang="ts">
import Vue from 'vue'
import { mapState, mapGetters } from 'vuex'

import { Signal, NativeEvent, ConnectionState } from '@/types'
import ConnectButton from '@/components/ConnectButton.vue'

export default Vue.extend({
    name: 'Controller',
    components: { ConnectButton },
    data: () => ({
        snackbar: false,
        message: '',
        color: '',
        buttons: [
            { icon: 'mdi-arrow-up-bold', signal: Signal.UP },
            { icon: 'mdi-pause', signal: Signal.PAUSE },
            { icon: 'mdi-arrow-down-bold', signal: Signal.DOWN },
            { icon: 'mdi-stop', signal: Signal.LOCK }
        ],
        sounds: [] as Array<HTMLAudioElement>,
        pairing: false
    }),
    computed: {
        ...mapGetters('bluetooth', ['disconnected', 'connecting', 'connected']),
        ...mapState('setting', ['device', 'pin', 'timeout', 'exitAtTimeout']),
        connectionMsg() {
            if (this.connected) {
                return '已連線'
            } else if (this.pairing) {
                return '配對中...'
            } else if (this.connecting) {
                return '連線中...'
            }
            return ''
        }
    },
    methods: {
        showMessage(text: string, color: string = '') {
            this.message = text
            this.color = color
            this.snackbar = true
        },
        hideMessage() {
            this.message = ''
            this.snackbar = false
        },
        playSound() {
            const i = Math.floor(Math.random() * this.sounds.length)
            this.sounds[i].play()
        },
        async connect() {
            if (this.disconnected) {
                try {
                    await $Native.Bluetooth.connect(this.device.address, this.pin)
                    if (this.timeout > 0) {
                        const t = setTimeout(() => {
                            this.disconnect()
                            if (this.exitAtTimeout) {
                                $Native.Application.exit()
                            }
                        }, this.timeout * 1000)

                        const id = $NativeListener.on(NativeEvent.STATE_CHANGE, event => {
                            if (event.state != ConnectionState.CONNECTED) {
                                clearTimeout(t)
                                $NativeListener.off(NativeEvent.STATE_CHANGE, id)
                            }
                        })
                    }
                } catch (error) {
                    if (this.pairing) {
                        this.showMessage('配對失敗', 'error')
                    } else {
                        this.showMessage('連線失敗', 'error')
                    }
                }
            }
        },
        async disconnect() {
            try {
                await $Native.Bluetooth.disconnect()
            } catch (error) {
                this.showMessage('發生錯誤', 'error')
            }
        },
        async send(signal: Signal) {
            this.playSound()
            if (this.connected) {
                await $Native.Bluetooth.send(signal)
            }
        }
    },
    mounted() {
        $NativeListener.on(NativeEvent.CONNECTION_LOST, event => {
            if (!event.expected) {
                this.showMessage('失去連線', 'error')
            }
        })

        $NativeListener.on(NativeEvent.STATE_CHANGE, event => (this.pairing = false))
        $NativeListener.on(NativeEvent.PAIRING, () => (this.pairing = true))

        for (let i = 1; i <= 3; ++i) {
            const audio = new Audio(require(`@/assets/sounds/click-${i}.mp3`))
            audio.volume = 0.75
            this.sounds.push(audio)
        }
    }
})
</script>

<style lang="scss" scoped>
.v-content {
    height: inherit;
    background: #141526;
}
.v-card {
    background: linear-gradient(to left bottom, #44e2cf, #ad86fd, #7aa0ff, #79e087);
    background-size: 250%;

    animation: background-animation 10s ease infinite;

    @keyframes background-animation {
        0% {
            background-position: 0% 50%;
        }
        50% {
            background-position: 100% 50%;
        }
        100% {
            background-position: 0% 50%;
        }
    }
}
</style>
