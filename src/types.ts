export interface INative {
    Bluetooth: {
        enable(): Promise<any>
        startDiscovery(): Promise<any>
        cancelDiscovery(): Promise<any>
        getBondedDevices(): Promise<Array<any>>
        connect(address: string, pin: string): Promise<any>
        disconnect(): Promise<any>
        send(signal: Signal): Promise<any>
    }
    Application: {
        exit(): Promise<any>
    }
}

export interface IDevice {
    name: string
    address: string
    type: string
    state: string
}

export interface INativeEvent {
    type: NativeEvent
    device?: IDevice
    state?: ConnectionState
    expected?: Boolean
}

export interface INativeHandler {
    (event: INativeEvent): void
}

export interface INativeListener {
    on(event: NativeEvent, handler: INativeHandler): number
    off(event: NativeEvent, id: number): void
}

export const enum NativeEvent {
    DISABLING_BLUETOOTH,
    ENABLED_BLUETOOTH,
    FOUND_DEVICE,
    STATE_CHANGE,
    CONNECTION_LOST,
    PAIRING
}

export const enum ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED
}

export const enum Signal {
    UP,
    PAUSE,
    DOWN,
    LOCK
}

export class DeviceType {
    public static MISC = 0x0000
    public static COMPUTER = 0x0100
    public static PHONE = 0x0200
    public static NETWORKING = 0x0300
    public static AUDIO_VIDEO = 0x0400
    public static PERIPHERAL = 0x0500
    public static IMAGING = 0x0600
    public static WEARABLE = 0x0700
    public static TOY = 0x0800
    public static HEALTH = 0x0900
    public static UNCATEGORIZED = 0x1F00
}