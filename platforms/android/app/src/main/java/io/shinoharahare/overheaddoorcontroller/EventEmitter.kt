package io.shinoharahare.overheaddoorcontroller

import android.webkit.WebView
import com.apkfuns.jsbridge.JBUtils
import io.shinoharahare.overheaddoorcontroller.enums.Event

class EventEmitter(private val webView: WebView) {
    fun emit(event: Event, data: Any? = null, field: String? = null) {
        var jsObj = JBUtils.toJsObject(data)
        jsObj = if (field != null) "{ $field: $jsObj}" else jsObj
        webView.evaluateJavascript(
            """
                ${'$'}NativeListener.receive(${event.ordinal}, $jsObj)
              """
        ) {}
    }
}