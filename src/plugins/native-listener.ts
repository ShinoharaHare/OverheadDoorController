import { NativeEvent, INativeEvent, INativeListener, INativeHandler } from '@/types'

class NativeListener implements INativeListener {
    handlers: Map<number, INativeHandler> = new Map()
    map: Map<NativeEvent, Set<number>> = new Map()
    releasedIds: Array<number> = new Array()
    currentId: number = 0

    generateHandlerId() : number {
        if (this.releasedIds.length) {
            return this.releasedIds.pop()!
        } else {
            return this.currentId++
        }
    }

    on(event: NativeEvent, handler: INativeHandler) {
        const id = this.generateHandlerId()
        this.handlers.set(id, handler)
        if (!this.map.has(event)) {
            this.map.set(event, new Set([id]))
        } else {
            this.map.get(event)?.add(id)
        }
        return id
    }

    off(event: NativeEvent, id: number) {
        this.map.get(event)?.delete(id)
        this.handlers.delete(id)
        this.releasedIds.push(id)
    }

    receive(event: NativeEvent, data?: INativeEvent) {
        // console.log(event, data)
        // console.log(this.handlers.get(event))
        for (let id of this.map.get(event)!) {
            this.handlers.get(id)!(Object.assign({ type: event }, data))
        }
    }
}

window.$NativeListener = new NativeListener()
