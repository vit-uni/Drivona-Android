package com.drivona.speed.api

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okio.BufferedSink

/**
 * 自定义请求body
 */
class CustomizerJSONBody(private val content: String) : RequestBody() {
    companion object {
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }

    override fun contentType(): MediaType = JSON_MEDIA_TYPE

    override fun writeTo(sink: BufferedSink) {
        sink.writeUtf8(content)
    }
}