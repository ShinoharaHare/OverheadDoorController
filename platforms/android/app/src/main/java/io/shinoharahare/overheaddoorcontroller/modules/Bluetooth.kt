package io.shinoharahare.overheaddoorcontroller.modules

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import com.apkfuns.jsbridge.module.JBCallback
import com.apkfuns.jsbridge.module.JSBridgeMethod
import com.apkfuns.jsbridge.module.JsModule
import io.shinoharahare.overheaddoorcontroller.Communicator
import io.shinoharahare.overheaddoorcontroller.EventEmitter
import io.shinoharahare.overheaddoorcontroller.IHandler
import io.shinoharahare.overheaddoorcontroller.MainActivity
import io.shinoharahare.overheaddoorcontroller.data.JArray
import io.shinoharahare.overheaddoorcontroller.data.JDevice
import io.shinoharahare.overheaddoorcontroller.enums.ConnectionState
import io.shinoharahare.overheaddoorcontroller.enums.Event
import io.shinoharahare.overheaddoorcontroller.enums.Signal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class Bluetooth(private val activity: MainActivity) : JsModule() {
    private val adapter = BluetoothAdapter.getDefaultAdapter()
    private val emitter by lazy { EventEmitter(webView) }
    private val communicator: Communicator = Communicator(object : IHandler {
        override fun onStateChange(state: ConnectionState) {
            emitter.emit(Event.STATE_CHANGE, state.ordinal, "state")
        }

        override fun onConnectionLost(expected: Boolean) {
            emitter.emit(Event.CONNECTION_LOST, expected, "expected")
        }
    })

    override fun getModuleName(): String {
        return "Bluetooth"
    }

    @JSBridgeMethod
    fun enable(resolve: JBCallback) {
        activity.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                    BluetoothAdapter.STATE_ON -> {
                        resolve.apply()
                        activity.unregisterReceiver(this)
                    }
                }
            }
        }, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        adapter.enable()
    }

    @JSBridgeMethod
    fun startDiscovery(resolve: JBCallback) {
        if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        activity.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice? =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        emitter.emit(Event.FOUND_DEVICE, JDevice(device), "device")
                    }

                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        activity.unregisterReceiver(this)
                        resolve.apply()
                    }
                }
            }
        }, filter)
        adapter.startDiscovery()
    }

    @JSBridgeMethod
    fun cancelDiscovery(resolve: JBCallback) {
        if (adapter.isDiscovering) {
            activity.registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    resolve.apply()
                }
            }, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
            adapter.cancelDiscovery()
        } else {
            resolve.apply()
        }
    }

    @JSBridgeMethod
    fun getBondedDevices(resolve: JBCallback) {
        val list = JArray<JDevice>()
        for (device in adapter.bondedDevices) {
            list.add(JDevice(device))
        }
        resolve.apply(list)
    }

    @JSBridgeMethod
    fun connect(address: String, pin: String, resolve: JBCallback, reject: JBCallback) {
        GlobalScope.launch(Dispatchers.IO) {
            val receiver = object : BroadcastReceiver() {

                @SuppressLint("MissingPermission")
                override fun onReceive(context: Context?, intent: Intent?) {
                    val device: BluetoothDevice = intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    if (device.address == address) {
                        when (intent.action) {
                            BluetoothDevice.ACTION_PAIRING_REQUEST -> {
                                try {
                                    device.setPin(pin.toByteArray())
                                    device.setPairingConfirmation(true)
                                } catch (e: SecurityException) {
                                } finally {
                                    abortBroadcast()
                                }
                            }

                            BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                                when (intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)) {
                                    BluetoothDevice.BOND_BONDING -> emitter.emit(Event.PAIRING)
                                }
                            }
                        }
                    }
                }
            }
            val filter = IntentFilter()
            filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST)
            filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)

            try {
                activity.registerReceiver(receiver, filter)
                communicator.connect(address)
                resolve.apply()
            } catch (e: Exception) {
                reject.apply()
            } finally {
                activity.unregisterReceiver(receiver)
            }
        }
    }

    @JSBridgeMethod
    fun disconnect(resolve: JBCallback, reject: JBCallback) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                communicator.disconnect()
                resolve.apply()
            } catch (e: Exception) {
                reject.apply()
            }
        }
    }

    @JSBridgeMethod
    fun send(_signal: Int, resolve: JBCallback, reject: JBCallback) {
        GlobalScope.launch(Dispatchers.IO) {
            val signal = Signal.values()[_signal]
            try {
                when (signal) {
                    Signal.UP -> communicator.send("a")
                    Signal.PAUSE -> communicator.send("b")
                    Signal.DOWN -> communicator.send("c")
                    Signal.LOCK -> communicator.send("d")
                }
                resolve.apply()
            } catch (e: Exception) {
                reject.apply()
            }
        }
    }
}