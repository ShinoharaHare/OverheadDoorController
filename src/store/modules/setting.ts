const store = {
    namespaced: true,
    state: new Proxy(
        {
            device: null,
            pin: '',
            timeout: 30,
            exitAtTimeout: false,
            disableBTOnExit: true,
            connectOnStart: true
        },
        {
            set(obj: any, key: string, value) {
                localStorage[key] = JSON.stringify(value)
                obj[key] = value
                return true
            }
        }
    ),
    mutations: {
        save(state: any, payload: any) {
            state[payload.key] = payload.value
        },
        toggle(state: any, key: any) {
            if (typeof state[key] === 'boolean') {
                state[key] = !state[key]
            } else {
                throw TypeError('Mutation:Toggle must be use on a boolean variable')
            }
        }
    }
}

for (let key of Object.keys(store.state)) {
    if (localStorage[key] !== undefined) {
        try {
            store.state[key] = JSON.parse(localStorage[key])
        } catch (error) {
            store.state[key] = localStorage[key]
        }
    }
}

export default store
