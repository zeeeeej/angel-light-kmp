package com.yunext.angel.light.repository.http

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

@Serializable
class HttpResp<T>(
    val code: Int,
    val msg: String,
    val data: T?,
    val success: Boolean
) {
    companion object
}

typealias HttpRespDataEmpty = HttpResp<Unit?>


val HttpResp<*>.tokenExpired: Boolean
    get() = this.code == 103


object HttpRespSerializerFactory {
    fun <T> serializer(dataSerializer: KSerializer<T>): KSerializer<HttpResp<T>> =
        HttpRespSerializer(dataSerializer)
}

private class HttpRespSerializer<T>(private val dataSerializer: KSerializer<T>) :
    KSerializer<HttpResp<T>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("HttpResp") {
        element("code", Int.serializer().descriptor)
        element("msg", String.serializer().descriptor)
        element("data", dataSerializer.descriptor)
        element("success", Boolean.serializer().descriptor)
    }

    override fun serialize(encoder: Encoder, value: HttpResp<T>) {
        encoder.encodeStructure(descriptor) {
            encodeIntElement(descriptor, 0, value.code)
            encodeStringElement(descriptor, 1, value.msg)
            encodeSerializableElement(descriptor, 2, dataSerializer, value.data as T)
            encodeBooleanElement(descriptor, 3, value.success)
        }
    }

    override fun deserialize(decoder: Decoder): HttpResp<T> {
        return decoder.decodeStructure(descriptor) {
            var code = 0
            var msg = ""
            var data: T? = null
            var success = false

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> code = decodeIntElement(descriptor, index)
                    1 -> msg = decodeStringElement(descriptor, index)
                    2 -> data = decodeSerializableElement(descriptor, index, dataSerializer)
                    3 -> success = decodeBooleanElement(descriptor, index)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> throw SerializationException("Unexpected index: $index")
                }
            }
            HttpResp(code, msg, data as T, success)
        }
    }
}






