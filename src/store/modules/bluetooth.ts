import { ConnectionState } from '@/types'

export default {
    namespaced: true,
    state: {
        state: ConnectionState.DISCONNECTED
    },
    getters: {
        disconnected: (state: any) => state.state === ConnectionState.DISCONNECTED,
        connecting: (state: any) => state.state === ConnectionState.CONNECTING,
        connected: (state: any) => state.state === ConnectionState.CONNECTED
    },
    mutations: {
        changeState(state: any, s: ConnectionState) {
            state.state = s
        }
    }
}
