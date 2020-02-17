<template>
    <v-content>
        <v-list dark two-line subheader height="100%">
            <v-subheader>一般</v-subheader>

            <v-list-item @click="toggle('connectOnStart')">
                <v-list-item-avatar>
                    <v-icon>mdi-bluetooth-connect</v-icon>
                </v-list-item-avatar>

                <v-list-item-content>
                    <v-list-item-title>自動連線</v-list-item-title>
                    <v-list-item-subtitle>應用啟動時嘗試連線</v-list-item-subtitle>
                </v-list-item-content>
                <v-list-item-action>
                    <v-switch class="no-event" color="light-blue" :ripple="false" :input-value="connectOnStart" />
                </v-list-item-action>
            </v-list-item>

            <v-list-item @click="toggle('disableBTOnExit')">
                <v-list-item-avatar>
                    <v-icon>mdi-bluetooth-off</v-icon>
                </v-list-item-avatar>

                <v-list-item-content>
                    <v-list-item-title>自動關閉藍芽</v-list-item-title>
                    <v-list-item-subtitle>應用退出時關閉藍芽</v-list-item-subtitle>
                </v-list-item-content>
                <v-list-item-action>
                    <v-switch class="no-event" color="light-blue" :ripple="false" :input-value="disableBTOnExit" />
                </v-list-item-action>
            </v-list-item>

            <v-list-item @click="toggle('exitAtTimeout')">
                <v-list-item-avatar>
                    <v-icon>mdi-power</v-icon>
                </v-list-item-avatar>

                <v-list-item-content>
                    <v-list-item-title>自動退出</v-list-item-title>
                    <v-list-item-subtitle>當計時器歸零時退出應用</v-list-item-subtitle>
                </v-list-item-content>
                <v-list-item-action>
                    <v-switch class="no-event" color="light-blue" :ripple="false" :input-value="exitAtTimeout" />
                </v-list-item-action>
            </v-list-item>

            <v-divider />
            <v-subheader>連線</v-subheader>

            <v-list-item @click="showDialog('device')">
                <v-list-item-avatar>
                    <v-icon>mdi-devices</v-icon>
                </v-list-item-avatar>

                <v-list-item-content>
                    <v-list-item-title>選擇裝置</v-list-item-title>
                    <v-list-item-subtitle>{{ getDescription('device') }}</v-list-item-subtitle>
                </v-list-item-content>

                <v-dialog fullscreen transition="dialog-bottom-transition" v-model="dialogs.device">
                    <DeviceList v-model="dialogs.device" />
                </v-dialog>
            </v-list-item>

            <v-list-item @click="showDialog('pin')">
                <v-list-item-avatar>
                    <v-icon>mdi-textbox-password</v-icon>
                </v-list-item-avatar>
                <v-list-item-content>
                    <v-list-item-title>PIN</v-list-item-title>
                    <v-list-item-subtitle>{{ getDescription('pin') }}</v-list-item-subtitle>
                </v-list-item-content>

                <v-dialog v-model="dialogs.pin" max-width="500px">
                    <v-card>
                        <v-card-title>PIN</v-card-title>
                        <v-card-text>
                            <v-form v-model="valid">
                                <v-text-field
                                    counter
                                    autofocus
                                    inputmode="numeric"
                                    type="password"
                                    v-model="values.pin"
                                    :rules="[v => v.length >= 4 || '最少4位']"
                                    @textInput="numberOnly"
                                />
                            </v-form>
                        </v-card-text>
                        <v-card-actions>
                            <v-spacer />
                            <v-btn color="blue darken-1" text @click="closeDialog('pin')">取消</v-btn>
                            <v-btn color="blue darken-1" text :disabled="!valid" @click="saveDialog('pin')">儲存</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-dialog>
            </v-list-item>

            <v-list-item @click="showDialog('timeout')">
                <v-list-item-avatar>
                    <v-icon>mdi-timer</v-icon>
                </v-list-item-avatar>

                <v-list-item-content>
                    <v-list-item-title>計時器</v-list-item-title>
                    <v-list-item-subtitle>{{ getDescription('timeout') }}</v-list-item-subtitle>
                </v-list-item-content>

                <v-dialog max-width="500px" v-model="dialogs.timeout">
                    <v-card>
                        <v-card-title>計時器</v-card-title>
                        <v-card-text>
                            <v-form v-model="valid">
                                <v-slider
                                    thumb-label
                                    inverse-label
                                    persistent-hint
                                    min="0"
                                    max="60"
                                    label="秒"
                                    ticks="always"
                                    append-icon="mdi-plus"
                                    prepend-icon="mdi-minus"
                                    v-model="values.timeout"
                                    :step="5"
                                    :hint="getDescription('timeout', true)"
                                    @click:append="values.timeout += 5"
                                    @click:prepend="values.timeout -= 5"
                                ></v-slider>
                            </v-form>
                        </v-card-text>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn color="blue darken-1" text @click="closeDialog('timeout')">取消</v-btn>
                            <v-btn color="blue darken-1" text :disabled="!valid" @click="saveDialog('timeout')">
                                儲存
                            </v-btn>
                        </v-card-actions>
                    </v-card>
                </v-dialog>
            </v-list-item>

            <v-divider />
            <v-subheader>進階</v-subheader>
            <v-list-item>
                <v-list-item-avatar>
                    <v-icon>mdi-alert</v-icon>
                </v-list-item-avatar>
                <v-list-item-content>
                    <v-list-item-title>AT-Command</v-list-item-title>
                    <v-list-item-subtitle>小心使用</v-list-item-subtitle>
                </v-list-item-content>
            </v-list-item>

            <v-divider />
            <v-subheader>介面</v-subheader>
            <v-list-item>
                <v-list-item-avatar>
                    <v-icon>mdi-earth</v-icon>
                </v-list-item-avatar>
                <v-list-item-content>
                    <v-list-item-title>語言</v-list-item-title>
                    <v-list-item-subtitle>中文(繁體)</v-list-item-subtitle>
                </v-list-item-content>
            </v-list-item>

            <v-list-item>
                <v-list-item-avatar>
                    <v-icon>mdi-flower</v-icon>
                </v-list-item-avatar>
                <v-list-item-content>
                    <v-list-item-title>主題</v-list-item-title>
                    <v-list-item-subtitle>暗色(預設)</v-list-item-subtitle>
                </v-list-item-content>
            </v-list-item>
        </v-list>

        <v-snackbar class="mb-12" color="success" :timeout="2000" v-model="snackbar">
            {{ message }}
        </v-snackbar>
    </v-content>
</template>

<script lang="ts">
import Vue from 'vue'
import { mapState, mapMutations } from 'vuex'
import DeviceList from '@/components/DeviceList.vue'

export default Vue.extend({
    name: 'Setting',
    components: { DeviceList },
    data: () => ({
        dialogs: {
            device: false,
            pin: false,
            timeout: false
        },
        values: {
            pin: null,
            timeout: null,
            device: null
        },
        valid: false,
        snackbar: false,
        message: ''
    }),
    computed: {
        ...mapState('setting', ['device', 'pin', 'timeout', 'disableBTOnExit', 'connectOnStart', 'exitAtTimeout'])
    },
    methods: {
        ...mapMutations('setting', ['save', 'toggle']),
        showDialog(name: 'device' | 'pin' | 'timeout') {
            this.dialogs[name] = true
            if (name != 'device') {
                this.values[name] = this[name]
            }
        },
        closeDialog(name: 'device' | 'pin' | 'timeout') {
            this.dialogs[name] = false
        },
        saveDialog(name: 'pin' | 'timeout') {
            this.dialogs[name] = false
            this.save({ key: name, value: this.values[name] })
            this.showMessage('儲存成功')
        },
        numberOnly(e: InputEvent) {
            const keyCode = e.data!!.charCodeAt(0)
            if (keyCode < 48 || keyCode > 57) {
                e.preventDefault()
            }
        },
        getDescription(name: string, dialog = false): string {
            const self = dialog ? this.values : this
            switch (name) {
                case 'device':
                    return self[name] ? `${self.device.name}(${self.device.address})` : '尚未選擇裝置'
                case 'pin':
                    return self[name] ? '*'.repeat(self.pin.length) : '尚未設定PIN'
                case 'timeout':
                    return self[name] ? `${self.timeout}秒` : '關閉'
                default:
                    return ''
            }
        },
        showMessage(text: string) {
            this.snackbar = true
            this.message = text
        }
    },
    mounted() {}
})
</script>

<style lang="scss" scoped>
.v-content {
    height: inherit;
    background: #141526;
}
</style>
