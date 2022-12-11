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
  private val numberAdapter: NumberAdapter<UnsignedT>,
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
        error("negative value")
      }
      numberAdapter.unsignedParser(stringOfNumber.toULong())
    }

    else -> throw JsonDataException(
      "Expected an unsigned number but was ${reader.readJsonValue()}, " +
      "a $next, at path ${reader.path}"
    )
  }
}

private class NumberAdapter<UnsignedT> private constructor(
  val unsignedParser: ULong.() -> UnsignedT,
) {
  companion object {
    val Int = NumberAdapter { toUInt() }
    val Long = NumberAdapter { toULong() }
    val Short = NumberAdapter { toUShort() }
    val Byte = NumberAdapter { toUByte() }
  }
}

object UnsignedAdapterFactory : JsonAdapter.Factory {
  private val unsignedTypesMapperMap: Map<Class<*>, NumberAdapter<*>> = mapOf(
    ULong::class.java to NumberAdapter.Long,
    UInt::class.java to NumberAdapter.Int,
    UShort::class.java to NumberAdapter.Short,
    UByte::class.java to NumberAdapter.Byte
  )

  private val Type.isUnsignedType: Boolean
    get() = unsignedTypesMapperMap.keys.any { it.isAssignableFrom(rawType) }

  private val Type.mapper: NumberAdapter<*>
    get() = unsignedTypesMapperMap[rawType]!!

  override fun create(
    type: Type,
    annotations: MutableSet<out Annotation>,
    moshi: Moshi,
  ): JsonAdapter<*>? = if (type.isUnsignedType) UnsignedTypeAdapter(type.mapper) else null
}
