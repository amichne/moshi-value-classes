@file:Suppress("TooGenericExceptionCaught")

package io.amichne.moshi.extension

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

private inline fun <reified T> Moshi.serialize(value: T): String = adapter(T::class.java).toJson(value)

private fun <T : Any> Moshi.deserialize(value: String, type: Class<T>): T = adapter(type).fromJson(value)!!

private val base: Moshi = Moshi.Builder()
  .addLast(KotlinJsonAdapterFactory())
  .build()
private val custom: Moshi = Moshi.Builder()
  .add(UnsignedAdapterFactory)
  .add(ValueClassAdapterFactory)
  .addLast(KotlinJsonAdapterFactory())
  .build()

private fun Map<Any, String>.compareSerialization() {
  forEach {
    println("Input JSON: \t\t${it.value}")
    println("Original Value: \t${it.key}")
    try {
      println("Plain Moshi: \t\t${base.serialize(base.deserialize(it.value, it.key.javaClass))}")
    } catch (exception: Exception) {
      println("Plain Moshi: \t\t[${exception.message?.replace('\n', ' ')}]")
    }
    println(
      "Value Adapted Moshi: \t" +
      custom.serialize(custom.deserialize(it.value, it.key.javaClass))
    )
    println("\n${"-----------------------".repeat(3)}\n")
  }
}

fun main() {
  jvmInlineValuesToStringRepresentation.compareSerialization()
}
