package io.amichne.moshi.extension

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import assertk.fail
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

private val moshi: Moshi = Moshi.Builder()
  .add(UnsignedAdapterFactory)
  .addLast(KotlinJsonAdapterFactory())
  .build()

private fun <T : Any> Moshi.deserialize(value: String, type: Class<T>): T = adapter(type).fromJson(value)!!

private val dataClassWithUInt = DataClassWithUInt(Int.MAX_VALUE.toUInt() + Short.MAX_VALUE.toUInt())
private val dataClassWithULong = DataClassWithULong(Long.MAX_VALUE.toULong() + Int.MAX_VALUE.toUInt())
private val dataClassWithUShort = DataClassWithUShort(
  (Short.MAX_VALUE.toUShort() + Byte.MAX_VALUE.toUShort()).toUShort()
)
private val dataClassWithUByte = DataClassWithUByte((Byte.MAX_VALUE.toUByte() + 10u).toUByte())

@Language("JSON")
private val unsignedToStringRepresentation: Map<Any, String> = mapOf(
  dataClassWithULong to """{"uLong":${dataClassWithULong.uLong}}""", // uLong=9223372039002259454
  dataClassWithUInt to """{"uInt":${dataClassWithUInt.uInt}}""", // uInt=2147516414
  dataClassWithUShort to """{"uShort":${dataClassWithUShort.uShort}}""", // uShort=32894
  dataClassWithUByte to """{"uByte":${dataClassWithUByte.uByte}}""", // uByte=137
)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UnsignedAdapterFactoryTest {
  private fun assertSerializedDeserialized(original: Any) =
    when (val stringRepresentation = unsignedToStringRepresentation[original]) {
      null -> fail("Missing string representation of $original")
      else -> {
        val actual = moshi.adapter(original::class.java).fromJson(stringRepresentation)!!
        assertThat(actual).isEqualTo(original)
        actual::class.java.declaredFields.map { field ->
          assertThat(field.genericType).isEqualTo(
            original::class.java.declaredFields
              .find { it.name == field.name }?.genericType
          )
        }
      }
    }

  @Suppress("unused")
  private fun unsignedNumbers(): Stream<Arguments> = Stream.of(
    Arguments.of(dataClassWithULong),
    Arguments.of(dataClassWithUInt),
    Arguments.of(dataClassWithUShort),
    Arguments.of(dataClassWithUByte),
  )

  @Suppress("unused")
  private fun dataClassPropertyNames(): Stream<Arguments> = Stream.of(
    Arguments.of(DataClassWithULong::class.java, "uLong"),
    Arguments.of(DataClassWithUInt::class.java, "uInt"),
    Arguments.of(DataClassWithUShort::class.java, "uShort"),
    Arguments.of(DataClassWithUByte::class.java, "uByte"),
  )

  @ParameterizedTest
  @MethodSource("unsignedNumbers")
  fun `When an unsigned value would be negative if it was signed, then it's positivity is properly retained`(
    value: Any,
  ) {
    assertSerializedDeserialized(value)
  }

  @ParameterizedTest
  @MethodSource("dataClassPropertyNames")
  fun `When a negative value as attempted to be deserialized, then an exception is thrown`(
    type: Class<*>,
    propertyName: String,
  ) {
    val negativeValue = -1

    @Language("JSON")
    val stringRepresentation = """{"$propertyName":$negativeValue}"""
    assertThat(
      requireNotNull(
        assertThrows<JsonDataException> {
          moshi.adapter(type).fromJson(stringRepresentation)
        }.cause
      ).message
    ).isEqualTo(
      other = "$negativeValue"
    )
  }

  @Test
  fun `When a unsigned field is not null nullable, and the value is null, than an exception is thrown`() {
    @Language("JSON")
    val stringRepresentation = """{"uLong": null}"""
    assertThat(
      requireNotNull(
        assertThrows<JsonDataException> {
          moshi.deserialize(stringRepresentation, DataClassWithULong::class.java)
        }.message
      )
    ).isEqualTo("Non-null value 'uLong' was null at $.uLong")
  }

  @Test
  fun `When a unsigned field is nullable, and the value is not null, it is deserialized properly`() {
    @Language("JSON")
    val stringRepresentation = """{"nullableULong": ${dataClassWithULong.uLong}}"""
    assertThat(
      moshi.deserialize(stringRepresentation, DataClassWithNullableULong::class.java)
        .nullableULong
    ).isEqualTo(dataClassWithULong.uLong)
  }

  @Test
  fun `When a unsigned field is nullable, and the value is null, it is deserialized properly`() {
    @Language("JSON")
    val stringRepresentation = """{"nullableULong": null}"""
    assertThat(
      moshi.deserialize(stringRepresentation, DataClassWithNullableULong::class.java)
        .nullableULong
    ).isNull()
  }
}
