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
}
