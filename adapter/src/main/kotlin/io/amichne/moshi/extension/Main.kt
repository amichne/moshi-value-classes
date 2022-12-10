package io.amichne.moshi.extension

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

private inline fun <reified T> Moshi.serialize(value: T): String = adapter(T::class.java).toJson(value)

fun main() {
  val plainMoshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()
  val valueAdaptedMoshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .add(ValueClassAdapterFactory)
    .build()

//  jvmInlineValuesList
//    .plus(
//      listOf(
//        JvmInlineUInt(value = 15u),
//        DataClassWithUInt(value = 25u)
//      )
//    )
  listOf(
    JvmInlineUInt(value = 15u),
    DataClassWithUInt(value = 25u)
  ).forEach {
    println("Original Value: \t$it")
    try {
      println("Plain Moshi: \t\t${plainMoshi.serialize(it)}")
    } catch (exception: IllegalArgumentException) {
      println("Plain Moshi: ERROR[IllegalArgumentException]")
    }
    println("Value Adapted Moshi: \t${valueAdaptedMoshi.serialize(it)}")
    println("\n${"-----------------------".repeat(3)}\n")
  }
}
