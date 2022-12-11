package io.amichne.moshi.extension

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.rawType
import java.lang.reflect.Type
import kotlin.math.sign

private class UnsignedTypeAdapter<UnsignedT, SignedT : Number>(
  private val typeMapper: TypeMapper<UnsignedT, SignedT>,
) : JsonAdapter<UnsignedT>() {
  override fun toJson(writer: JsonWriter, value: UnsignedT?) {
    when (value) {
      null -> writer.nullValue()
      else -> writer.value(typeMapper.unsignedToSigned(value))
    }
  }

  override fun fromJson(reader: JsonReader): UnsignedT? = reader.readJsonValue()?.let { value ->
    when (value) {
      is Double -> {
        if (!value.rem(1).equals(0.0)) {
          // TODO - throw a real JsonDataException here
          error("non-integer value")
        } else if (value.sign < 0) {
          // TODO - throw a real JsonDataException here
          error("negative value")
        } else {
          typeMapper.signedToUnsigned(value.toLong())
        }
      }

      else -> throw JsonDataException(
        "Expected an unsigned number but was $value, a ${value::class.java.simpleName}, at path ${reader.path}"
      )
    }
  }
}

private class TypeMapper<UnsignedT, SignedT : Number> private constructor(
  val unsignedToSigned: UnsignedT.() -> Long,
  val signedToUnsigned: Long.() -> UnsignedT,
) {
  companion object {
    val Int = TypeMapper<UInt, Int>({ toLong() }, { toUInt() })
    val Long = TypeMapper<ULong, Long>({ toLong() }, { toULong() })
    val Short = TypeMapper<UShort, Short>({ toLong() }, { toUShort() })
    val Byte = TypeMapper<UByte, Byte>({ toLong() }, { toUByte() })
  }
}

object UnsignedAdapterFactory : JsonAdapter.Factory {
  private val unsignedTypesMapperMap: Map<Class<*>, TypeMapper<*, *>> = mapOf(
    ULong::class.java to TypeMapper.Long,
    UInt::class.java to TypeMapper.Int,
    UShort::class.java to TypeMapper.Short,
    UByte::class.java to TypeMapper.Byte
  )

  private val Type.isUnsignedType: Boolean
    get() = unsignedTypesMapperMap.keys.any { it.isAssignableFrom(rawType) }

  private val Type.mapper: TypeMapper<*, *>
    get() = unsignedTypesMapperMap[rawType]!!

  override fun create(
    type: Type,
    annotations: MutableSet<out Annotation>,
    moshi: Moshi,
  ): JsonAdapter<*>? = if (type.isUnsignedType) UnsignedTypeAdapter(type.mapper) else null
}
