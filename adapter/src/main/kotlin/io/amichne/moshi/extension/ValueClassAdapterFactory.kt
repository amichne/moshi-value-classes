package io.amichne.moshi.extension

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.rawType
import io.amichne.moshi.extension.UnsignedAdapterFactory.isUnsignedType
import java.lang.reflect.Constructor
import java.lang.reflect.Type
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.kotlinProperty

@Suppress("UNCHECKED_CAST")
private fun <T : Any, ValueT> T.declaredProperty(): ValueT =
  with((this::class.java.declaredFields.first().kotlinProperty as KProperty1<T, ValueT>).javaField!!) {
    isAccessible = true
    get(this@declaredProperty) as ValueT
  }

private class ValueClassAdapter<InlineT : Any, ValueT : Any>(
  val constructor: Constructor<out InlineT>,
  val adapter: JsonAdapter<ValueT>,
) : JsonAdapter<InlineT>() {
  override fun toJson(
    writer: JsonWriter,
    inlineT: InlineT?,
  ) {
    inlineT?.let {
      writer.jsonValue(
        adapter.toJsonValue(
          it.declaredProperty()
        )
      )
    }
  }

  @Suppress("TooGenericExceptionCaught")
  override fun fromJson(reader: JsonReader): InlineT = reader.readJsonValue().let { jsonValue ->
    try {
      constructor.isAccessible = true
      constructor.newInstance(adapter.fromJsonValue(jsonValue))
    } catch (throwable: Throwable) {
      throw JsonDataException(
        "Could not parse ${constructor.declaringClass.simpleName} from JSON value $this at path ${reader.path}",
        throwable
      )
    }
  }
}

object ValueClassAdapterFactory : JsonAdapter.Factory {
  override fun create(
    type: Type,
    annotations: MutableSet<out Annotation>,
    moshi: Moshi,
  ): JsonAdapter<Any>? = if (type.rawType.kotlin.isValue && !UInt::class.java.isAssignableFrom(type.rawType)) {
    val constructor = type.rawType.declaredConstructors.first { it.parameterCount == 1 } as Constructor<*>
    val valueType = type.rawType.declaredFields[0].genericType
    ValueClassAdapter(
      constructor = constructor,
      adapter = moshi.parameterizedAdapter(valueType.rawType, valueType),
    )
  } else null

  @Suppress("UNUSED_PARAMETER")
  private fun <V> Moshi.parameterizedAdapter(
    valueClass: Class<V>,
    valueType: Type,
  ): JsonAdapter<V> {
    return adapter(valueType)
  }
}
