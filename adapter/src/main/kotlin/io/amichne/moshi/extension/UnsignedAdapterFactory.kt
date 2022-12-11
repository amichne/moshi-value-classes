package io.amichne.moshi.extension

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonReader.Token
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.rawType
import java.lang.reflect.Type

private class UnsignedTypeAdapter<UnsignedT>(
  private val toUnsignedT: ULong.() -> UnsignedT,
) : JsonAdapter<UnsignedT>() {
  override fun toJson(writer: JsonWriter, value: UnsignedT?) {
    when (value) {
      null -> writer.nullValue()
      else -> writer.valueSink().use { it.writeUtf8(value.toString()) }
    }
  }

  override fun fromJson(reader: JsonReader): UnsignedT? = when (val next = reader.peek()) {
    Token.NUMBER -> {
      val stringOfNumber = reader.nextString()
      if (stringOfNumber.startsWith('-')) {
        throw JsonDataException(
          "Expected an unsigned number but got $stringOfNumber, " +
          "a signed number, at path ${reader.path}",
          IllegalArgumentException(stringOfNumber)
        )
      }
      stringOfNumber.toULong().toUnsignedT()
    }

    else -> throw JsonDataException(
      "Expected an unsigned number but was ${reader.peekJson().readJsonValue()}, " +
      "a $next, at path ${reader.path}",
      IllegalArgumentException(next.name)
    )
  }
}

object UnsignedAdapterFactory : JsonAdapter.Factory {
  private val unsignedTypesMapperMap: Map<Class<*>, ULong.() -> Any> = mapOf(
    ULong::class.java to { toULong() },
    UInt::class.java to { toUInt() },
    UShort::class.java to { toUShort() },
    UByte::class.java to { toUByte() }
  )

  private val Type.isUnsignedType: Boolean
    get() = unsignedTypesMapperMap.keys.any { it.isAssignableFrom(rawType) }

  private val Type.mapper: ULong.() -> Any
    get() = unsignedTypesMapperMap[rawType]!!

  override fun create(
    type: Type,
    annotations: MutableSet<out Annotation>,
    moshi: Moshi,
  ): JsonAdapter<*>? = if (type.isUnsignedType) UnsignedTypeAdapter(type.mapper) else null
}
