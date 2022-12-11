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

private fun Map<Any, String>.buildExamplesMd() {
  forEach {
    println("<details>\n")
    println("<summary>${it.key.javaClass.simpleName}</summary>\n")
    println("> JSON Literal:\n> ```json\n> ${it.value}\n> ```\n> ")
    println("> Kotlin Object:\n> ```\n> ${it.key}\n> ```\n> ")
    try {
      println("> Base Moshi Deserialization Result:\n> ```\n> ${base.deserialize(it.value, it.key.javaClass)}\n> ```\n> ")
    } catch (exception: Exception) {
      println("> Base Moshi Deserialization Result:\n> ```\n> ${exception.message?.replace('\n', ' ')}\n> ```\n> ")
    }
    try {
      println("> Base Moshi Serialization Result:\n> ```json\n> ${base.serialize(it.key)}\n> ```\n> ")
    } catch (exception: Exception) {
      println("> Base Moshi Serialization Result:\n> ```\n> ${exception.message?.replace('\n', ' ')}\n> ```\n> ")
    }
    println("> Updated Moshi Deserialization Result:\n> ```\n> ${custom.deserialize(it.value, it.key.javaClass)}\n> ```\n> ")
    println("> Updated Moshi Serialization Result:\n> ```json\n> ${custom.serialize(it.key)}\n> ```\n")
    println("</details>\n")
  }
}

fun main() {
  instanceToJsonStringMap.buildExamplesMd()
}
