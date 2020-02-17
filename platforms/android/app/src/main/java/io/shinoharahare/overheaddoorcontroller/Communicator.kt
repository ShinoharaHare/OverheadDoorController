package io.shinoharahare.overheaddoorcontroller

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import io.shinoharahare.overheaddoorcontroller.enums.ConnectionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

interface IHandler {
    fun onStateChange(state: ConnectionState) {}
    fun onConnectionFailed() {}
    fun onConnectionLost(expected: Boolean) {}
    fun onDisconnected() {}
    fun onConnecting() {}
    fun onConnected() {}
}

class HandlerDecorator(private val handler: IHandler) {
    fun onStateChange(state: ConnectionState) = GlobalScope.launch(Dispatchers.Main) { handler.onStateChange(state) }
    fun onConnectionFailed() = GlobalScope.launch(Dispatchers.Main) { handler.onConnectionFailed() }
    fun onConnectionLost(expected: Boolean) = GlobalScope.launch(Dispatchers.Main) { handler.onConnectionLost(expected) }
    fun onDisconnected() = GlobalScope.launch(Dispatchers.Main) { handler.onDisconnected() }
    fun onConnecting() = GlobalScope.launch(Dispatchers.Main) { handler.onConnecting() }
    fun onConnected() = GlobalScope.launch(Dispatchers.Main) { handler.onConnected() }
}

class Communicator(handler: IHandler) {
    companion object {
        private val adapter = BluetoothAdapter.getDefaultAdapter()
        private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    private val handler = HandlerDecorator(handler)
    private var _state = ConnectionState.DISCONNECTED
    private var device: BluetoothDevice? = null
    private var socket: BluetoothSocket? = null
    private var expected: Boolean = false

    var state: ConnectionState
        get() = _state
        private set(v) {
            _state = v
            handler.onStateChange(v)
        }

    fun connect(address: String) {
        if (state == ConnectionState.DISCONNECTED) {
            state = ConnectionState.CONNECTING
            try {
                device = adapter.getRemoteDevice(address)
                socket = device!!.createRfcommSocketToServiceRecord(SPP_UUID)
                socket!!.connect()
                state = ConnectionState.CONNECTED
                listen()
            } catch (e: IOException) {
                onConnectionFailed()
                throw e
            }
        } else {
            throw Exception("State must be DISCONNECTED")
        }
    }

    fun send(string: String) {
        if (state == ConnectionState.CONNECTED) {
            try {
                socket?.outputStream?.write(string.toByteArray())
                socket?.outputStream?.flush()
            } catch (e: IOException) {
                throw e
            }
        } else {
            throw Exception("State must be CONNECTED")
        }
    }

    fun disconnect() {
        if (state == ConnectionState.CONNECTED) {
            try {
                expected = true
                socket?.close()
                state = ConnectionState.DISCONNECTED
            } catch (e: IOException) {
                throw e
            }
        } else {
            throw Exception("State must be CONNECTED")
        }
    }

    private fun listen() {
        GlobalScope.launch(Dispatchers.IO) {
            while (state == ConnectionState.CONNECTED) {
                try {
                    val buffer = ByteArray(128)
                    socket?.inputStream?.read(buffer)
                } catch (e: IOException) {
                    onConnectionLost()
                }
            }
        }
    }

    private fun onConnectionFailed() {
        socket?.close()
        state = ConnectionState.DISCONNECTED
        device = null
        socket = null
        handler.onConnectionFailed()
    }

    private fun onConnectionLost() {
        socket?.close()
        state = ConnectionState.DISCONNECTED
        device = null
        socket = null
        handler.onConnectionLost(expected)
        expected = false
    }
}