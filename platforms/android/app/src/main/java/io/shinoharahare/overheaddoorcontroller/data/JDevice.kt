package io.shinoharahare.overheaddoorcontroller.data

import android.bluetooth.BluetoothDevice

data class JDevice(private val device: BluetoothDevice?) : Jsonable {
    val name = device?.name ?: "null"
    val address = device?.address
    val type = device?.bluetoothClass?.majorDeviceClass
    val state = device?.bondState
}