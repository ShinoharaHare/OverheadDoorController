package io.shinoharahare.overheaddoorcontroller.modules

import com.apkfuns.jsbridge.module.JSBridgeMethod
import com.apkfuns.jsbridge.module.JsModule
import io.shinoharahare.overheaddoorcontroller.EventEmitter
import io.shinoharahare.overheaddoorcontroller.MainActivity


class Application(private val activity: MainActivity) : JsModule() {
    private val emitter by lazy { EventEmitter(webView) }

    override fun getModuleName(): String {
        return "Application"
    }

    @JSBridgeMethod
    fun exit() {
        activity.exit()
    }
}