package io.amichne.moshi.extension

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.fail
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


private val moshi: Moshi = Moshi.Builder()
  .add(UnsignedAdapterFactory)
  .addLast(KotlinJsonAdapterFactory())
  .build()

private val dataClassWithUInt = DataClassWithUInt(Int.MAX_VALUE.toUInt() + Short.MAX_VALUE.toUInt())
private val dataClassWithULong = DataClassWithULong(Long.MAX_VALUE.toULong() + Int.MAX_VALUE.toUInt())
private val dataClassWithUShort = DataClassWithUShort(
  (Short.MAX_VALUE.toUShort() + Byte.MAX_VALUE.toUShort()).toUShort()
)
private val dataClassWithUByte = DataClassWithUByte((Byte.MAX_VALUE.toUByte() + 10u).toUByte())

@Language("JSON")
private val unsignedToStringRepresentation: Map<Any, String> = mapOf(
  dataClassWithULong to """{"uLong": ${dataClassWithULong.uLong}}""", // uLong=9223372039002259454
  dataClassWithUInt to """{"uInt": ${dataClassWithUInt.uInt}}""", // uInt=2147516414
  dataClassWithUShort to """{"uShort": ${dataClassWithUShort.uShort}}""", // uShort=32894
  dataClassWithUByte to """{"uByte": ${dataClassWithUByte.uByte}}""", // uByte=137
)

class UnsignedAdapterFactoryTest {
  fun assertSerializedDeserialized(original: Any) =
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

  @Nested
  @TestInstance(PER_CLASS)
  inner class MostSignificantBitIsOneTests {
    @Suppress("unused")
    private fun inlineInstances(): Stream<Arguments> = Stream.of(
      Arguments.of(dataClassWithUInt),
      Arguments.of(dataClassWithUShort),
      Arguments.of(dataClassWithULong),
      Arguments.of(dataClassWithUByte),
    )

    @ParameterizedTest
    @MethodSource("inlineInstances")
    fun `When an unsigned value would be negative if it was signed, then it's positivity is properly retained`(
      value: Any,
    ) {
      assertSerializedDeserialized(value)
    }
  }
}
