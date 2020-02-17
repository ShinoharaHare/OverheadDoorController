package io.shinoharahare.overheaddoorcontroller

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.webkit.JsPromptResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apkfuns.jsbridge.JsBridge
import com.apkfuns.jsbridge.JsBridgeConfig
import io.shinoharahare.overheaddoorcontroller.enums.Event
import io.shinoharahare.overheaddoorcontroller.modules.Application
import io.shinoharahare.overheaddoorcontroller.modules.Bluetooth
import io.shinoharahare.overheaddoorcontroller.services.ClosingService


class MainActivity : AppCompatActivity() {
    private val webView: WebView by lazy { findViewById<WebView>(R.id.webView) }
    private val splash: View by lazy { findViewById<View>(R.id.splash) }
    private val emitter: EventEmitter by lazy { EventEmitter(webView) }
    private val jsBridge: JsBridge by lazy {
        JsBridgeConfig.getSetting().protocol = "_native"
        JsBridgeConfig.getSetting().setLoadReadyMethod("onJsBridgeReady")
        JsBridge.loadModule(Bluetooth(this), Application(this))
    }

    private var count = 0
    private val receivers: MutableSet<BroadcastReceiver> = mutableSetOf()
    private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkBluetooth()
        setupBroadcastReceiver()
        startService(Intent(this, ClosingService::class.java))

        WebView.setWebContentsDebuggingEnabled(true)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.loadUrl("file:///android_asset/index.html")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                jsBridge.injectJs(view!!)

                view.evaluateJavascript(
                    """
                        window.onJsBridgeReady=()=>{window.${'$'}Native={}
                        for(let[module,methods]of Object.entries(_native)){if(module!='OnJsBridgeReady'){window.${'$'}Native[module]={}
                        for(let[method,func]of Object.entries(methods)){window.${'$'}Native[module][method]=async(...args)=>new Promise((resolve,reject)=>func(...args,resolve,reject))}}}}
                    """
                ) {}
            }

            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
                showWebView()
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onJsPrompt(
                view: WebView?,
                url: String?,
                message: String?,
                defaultValue: String?,
                result: JsPromptResult?
            ): Boolean {
                jsBridge.callJsPrompt(message!!, result!!)
                return true
            }
        }
    }

    override fun onDestroy() {
        exit()
        super.onDestroy()
    }

    private fun showWebView() {
        count += 1
        if (count == 2) {
            webView.visibility = View.VISIBLE
            splash.animate().setDuration(2500).alpha(0.0f)
        }
    }

    override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter?): Intent? {
        receivers.add(receiver!!)
        return super.registerReceiver(receiver, filter)
    }

    override fun unregisterReceiver(receiver: BroadcastReceiver?) {
        receivers.remove(receiver)
        super.unregisterReceiver(receiver)
    }

    fun exit() {
        jsBridge.release()
        for (receiver in receivers) {
            unregisterReceiver(receiver)
        }
        adapter?.disable()
        finish()
    }

    private fun checkBluetooth() {
        if (adapter != null) {
            if (!adapter.isEnabled) {
                registerReceiver(object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        when (intent?.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                            BluetoothAdapter.STATE_ON -> {
                                showWebView()
                                context?.unregisterReceiver(this)
                            }
                        }
                    }
                }, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
                adapter.enable()
            } else {
                showWebView()
            }
        } else {
            Toast.makeText(this, "此裝置不支援藍芽，無法使用本軟體", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupBroadcastReceiver() {
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                            BluetoothAdapter.STATE_TURNING_OFF -> emitter.emit(Event.DISABLING_BLUETOOTH)
                            BluetoothAdapter.STATE_ON -> emitter.emit(Event.ENABLED_BLUETOOTH)
                        }
                    }
                }
            }
        }, filter)
    }

}
