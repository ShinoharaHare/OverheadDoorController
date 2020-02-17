import { INative, INativeListener } from './types'

declare global {
    const $Native: INative
    const $NativeListener: INativeListener

    interface Window {
        $NativeListener: INativeListener
        test: any
    }
}
