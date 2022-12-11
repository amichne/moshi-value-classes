@file:Suppress("TooGenericExceptionCaught")

package io.amichne.moshi.extension

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

private inline fun <reified T> Moshi.serialize(value: T): String = adapter(T::class.java).toJson(value)

private fun <T : Any> Moshi.deserialize(value: String, type: Class<T>): T = adapter(type).fromJson(value)!!

private val plainMoshi: Moshi = Moshi.Builder()
  .addLast(KotlinJsonAdapterFactory())
  .build()
private val valueAdaptedMoshi: Moshi = Moshi.Builder()
  .addLast(KotlinJsonAdapterFactory())
  .add(ValueClassAdapterFactory)
  .add(UnsignedAdapterFactory)
  .build()

private fun Map<Any, String>.compareSerialization() {
  forEach {
    println("Input JSON: \t${it.value}")
    println("Original Value: \t${it.key}")
    try {
      println("Plain Moshi: \t\t${plainMoshi.serialize(plainMoshi.deserialize(it.value, it.key.javaClass))}")
    } catch (exception: Exception) {
      println("Plain Moshi: [${exception.message}]")
    }
    println(
      "Value Adapted Moshi: \t" +
      valueAdaptedMoshi.serialize(
        valueAdaptedMoshi.deserialize(it.value, it.key.javaClass)
      )
    )
    println("\n${"-----------------------".repeat(3)}\n")
  }
}

fun main() {
  jvmInlineValuesToStringRepresentation.compareSerialization()
}
