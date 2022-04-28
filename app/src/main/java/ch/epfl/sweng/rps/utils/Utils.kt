package ch.epfl.sweng.rps.utils

import android.os.Bundle
import android.os.Parcelable

fun consume(block: () -> Any?): () -> Unit = { block() }

fun Map<String, Any>.toBundle(): Bundle {
    val bundle = Bundle()
    forEach {
        when (it.value) {
            is String -> bundle.putString(it.key, it.value as String)
            is Int -> bundle.putInt(it.key, it.value as Int)
            is Boolean -> bundle.putBoolean(it.key, it.value as Boolean)
            is Float -> bundle.putFloat(it.key, it.value as Float)
            is Double -> bundle.putDouble(it.key, it.value as Double)
            is Char -> bundle.putChar(it.key, it.value as Char)
            is Long -> bundle.putLong(it.key, it.value as Long)
            is Byte -> bundle.putByte(it.key, it.value as Byte)
            is Short -> bundle.putShort(it.key, it.value as Short)
            is Parcelable -> bundle.putParcelable(it.key, it.value as Parcelable)
            else -> throw IllegalArgumentException("Unsupported type: ${it.value::class.java}")
        }
    }
    return bundle
}
