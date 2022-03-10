package ch.epfl.sweng.rps.serialization

import com.google.firebase.Timestamp
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonArray

object TimestampAsListSerializer : KSerializer<Timestamp> {
    private val listSerializer = ListSerializer(Long.serializer())
    override val descriptor: SerialDescriptor = listSerializer.descriptor

    override fun serialize(encoder: Encoder, value: Timestamp) {
        listSerializer.serialize(encoder, listOf(value.seconds, value.nanoseconds.toLong()))
    }

    override fun deserialize(decoder: Decoder): Timestamp = with(decoder as JsonDecoder) {
        val a = decodeJsonElement().jsonArray
        return Timestamp(
            json.decodeFromJsonElement(Long.serializer(), a[0]),
            json.decodeFromJsonElement(Int.serializer(), a[1]),
        )
    }

    object TimestampSurrogateSerializer : KSerializer<Timestamp> {
        override val descriptor: SerialDescriptor = TimestampSurrogate.serializer().descriptor
        override fun serialize(encoder: Encoder, value: Timestamp) {
            val surrogate = TimestampSurrogate(value.seconds, value.nanoseconds)
            encoder.encodeSerializableValue(TimestampSurrogate.serializer(), surrogate)
        }

        override fun deserialize(decoder: Decoder): Timestamp {
            val surrogate = decoder.decodeSerializableValue(TimestampSurrogate.serializer())
            return Timestamp(surrogate.seconds, surrogate.nanoseconds)
        }

        @Serializable
        @SerialName("Timestamp")
        private data class TimestampSurrogate(
            @SerialName("sec")
            val seconds: Long,
            @SerialName("nano")
            val nanoseconds: Int,
        )

    }
}
