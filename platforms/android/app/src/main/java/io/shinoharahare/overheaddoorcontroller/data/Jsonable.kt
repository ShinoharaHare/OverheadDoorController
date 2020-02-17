package io.shinoharahare.overheaddoorcontroller.data

import com.apkfuns.jsbridge.module.JsObject
import com.google.gson.Gson


interface Jsonable : JsObject {
    companion object {
        val gson = Gson()
    }

    override fun convertJS(): String {
        return "JSON.parse('${gson.toJson(this)}')"
    }
}