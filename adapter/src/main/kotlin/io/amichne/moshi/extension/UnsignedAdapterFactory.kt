package io.amichne.moshi.extension

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.rawType
import java.lang.reflect.Type
import kotlin.math.sign

private class UnsignedTypeAdapter<UnsignedT, SignedT>(
  private val typeMapper: TypeMapper<UnsignedT, SignedT>,
) : JsonAdapter<UnsignedT>() {
  override fun toJson(writer: JsonWriter, value: UnsignedT?) {
    when (value) {
      null -> writer.nullValue()
      else -> writer.value(typeMapper.unsignedToSigned(value) as Number)
    }
  }

  override fun fromJson(reader: JsonReader): UnsignedT? = reader.readJsonValue()?.let { value ->
    when (value) {
      is Double -> {
        if (!value.rem(1).equals(0.0)) {
          error("non-integer value")
        } else if (value.sign < 0) {
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

private class TypeMapper<UnsignedT, SignedT> private constructor(
  val unsignedToSigned: UnsignedT.() -> SignedT,
  val signedToUnsigned: Long.() -> UnsignedT,
) {
  companion object {
    val Integer = TypeMapper({ toInt() }, { toUInt() })
    val Long = TypeMapper({ toLong() }, { toULong() })
    val Short = TypeMapper({ toShort() }, { toUShort() })
    val Byte = TypeMapper({ toByte() }, { toUByte() })
  }
}

object UnsignedAdapterFactory : JsonAdapter.Factory {
  private val typeMapperMap: Map<Class<*>, TypeMapper<*, *>> = mapOf(
    ULong::class.java to TypeMapper.Long,
    UInt::class.java to TypeMapper.Integer,
    UShort::class.java to TypeMapper.Short,
    UByte::class.java to TypeMapper.Byte
  )

  val Type.isUnsignedType: Boolean
    get() = typeMapperMap.keys.any { it.isAssignableFrom(rawType) }

  override fun create(
    type: Type,
    annotations: MutableSet<out Annotation>,
    moshi: Moshi,
  ): JsonAdapter<*>? = if (type.isUnsignedType) {
    UnsignedTypeAdapter(typeMapperMap[type.rawType]!!)
  } else null
}
