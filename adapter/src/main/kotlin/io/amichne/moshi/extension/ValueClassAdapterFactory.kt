package io.amichne.moshi.extension

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.rawType
import kotlinx.metadata.KmAnnotationArgument
import kotlinx.metadata.impl.writeAnnotationArgument
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import kotlinx.metadata.jvm.annotations
import java.lang.reflect.Constructor
import java.lang.reflect.Type
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.kotlinProperty

@Suppress("UNCHECKED_CAST")
private fun <T : Any, ValueT> T.declaredProperty(): ValueT =
  with(this::class.java.declaredFields.first().kotlinProperty as KProperty1<T, ValueT>) {
    isAccessible = true
    get(this@declaredProperty)
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
      writer.jsonValue(adapter.toJsonValue(it.declaredProperty()))
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
  @OptIn(ExperimentalUnsignedTypes::class)
  override fun create(
    type: Type,
    annotations: MutableSet<out Annotation>,
    moshi: Moshi,
  ): JsonAdapter<Any>? {
    type.rawType.annotations.forEach { annotation ->
      if (annotation is Metadata) {
//        val y = KotlinClassMetadata.read()
        val x = KotlinClassMetadata.read(
          KotlinClassHeader(
            annotation.kind,
            annotation.metadataVersion,
            annotation.data1,
            annotation.data2,
            annotation.extraString,
            annotation.packageName,
            annotation.extraInt,
          )
        )
        when (x) {
          null -> {}
          is KotlinClassMetadata.Class -> {
            x.toKmClass().properties.forEach {

            }
//            x.toKmClass().constructors.forEach { kmConstructor ->
//              kmConstructor.valueParameters.forEach { kmValueParameter ->
//                kmValueParameter.type.annotations.forEach { kmAnnotation ->
//                  kmAnnotation.arguments.values.forEach {
//                    if (it is KmAnnotationArgument.UIntValue) {
//                      println("My unsigned value: ${it.value}")
//                    }
//                  }
//                }
//                println("Name: ${kmValueParameter.name} Type: ${kmValueParameter.type.annotations}")
//
//              }
//            }
          }
          else -> {}
        }


        if (annotation.data2.contains("Lkotlin/UInt;")) {
          println("I can tell that I'm an unsigned integer")
        }
      }
      println("Annotation: ${annotation.annotationClass.simpleName}")
    }
    if (type.rawType.kotlin.isValue) {
      val constructor = type.rawType.declaredConstructors.first { it.parameterCount == 1 } as Constructor<*>
      val valueType = type.rawType.declaredFields[0].genericType
      return ValueClassAdapter(
        constructor = constructor,
        adapter = moshi.parameterizedAdapter(valueType.rawType, valueType),
      )
    } else return null
  }

  @Suppress("UNUSED_PARAMETER")
  private fun <V> Moshi.parameterizedAdapter(
    valueClass: Class<V>,
    valueType: Type,
  ): JsonAdapter<V> {
    return adapter(valueType)
  }
}
