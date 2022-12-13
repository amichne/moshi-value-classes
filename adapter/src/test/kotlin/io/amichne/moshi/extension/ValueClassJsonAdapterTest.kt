package io.amichne.moshi.extension

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.fail
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private val moshi: Moshi = Moshi.Builder()
  .add(ValueClassJsonAdapter.Factory)
  .add(UnsignedNumberJsonAdapter.Factory)
  .addLast(KotlinJsonAdapterFactory())
  .build()

private inline fun <reified T : Any> Moshi.serialize(value: T): String = adapter(T::class.java).toJson(value)

private fun <T : Any> Moshi.deserialize(value: String, type: Class<T>): T = adapter(type).fromJson(value)!!

@Suppress("MaxLineLength")
internal class ValueClassJsonAdapterTest {
  fun assertSerializedDeserialized(original: Any) =
    when (val stringRepresentation = instanceToJsonStringMap[original]) {
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
  inner class JsonTypeConversionTests {
    @Suppress("unused")
    private fun inlineInstances(): Stream<Arguments> = instanceToJsonStringMap.keys.map { Arguments.of(it) }.stream()

    @ParameterizedTest
    @MethodSource("inlineInstances")
    fun `Validate result of serialization is equal`(
      value: Any,
    ) {
      // This check is here as we don't serialize key with null values by default
      if (value is JvmInlineMapStringNullableInt) {
        assertThat(moshi.serialize(value)).isEqualTo("""{"first":1}""")
      } else {
        assertThat(moshi.serialize(value)).isEqualTo(instanceToJsonStringMap[value])
      }
    }

    @ParameterizedTest
    @MethodSource("inlineInstances")
    fun `Validate result of serialization and subsequent deserialization is equal`(inline: Any) {
      assertSerializedDeserialized(inline)
    }

    @Test
    fun `When a class has multiple constructors, and can be created using a secondary constructor, then it is still serialized and deserialized`() {
      assertSerializedDeserialized(jvmInlineStringMultipleConstructorUsage)
    }

    @Test
    fun `When a class is deserialized from an invalid representation, then an exception is thrown`() {
      @Language("JSON")
      val invalidJson = """{"stringValue":"someString", "malformedIntKey": 100}"""
      assertThat(
        requireNotNull(
          assertThrows<JsonDataException> {
            moshi.deserialize(invalidJson, JvmInlineComplexClass::class.java)
          }.message
        )
      ).isEqualTo("Required value 'intValue' missing at $")
    }
  }

  @Nested
  inner class NullabilityScenarios {
    @Test
    fun `When a value class field is not nullable, and the serialized value is null, then an exception is thrown`() {
      val illegallyNullFieldString = """null"""
      assertThrows<JsonDataException> {
        moshi.deserialize(illegallyNullFieldString, JvmInlineInt::class.java)
      }
    }

    @Test
    fun `When a value class field is nullable, and the serialized value is not null, then the deserialized field is not null`() {
      assertSerializedDeserialized(jvmInlineNotNullNullableString)
      assertNotNull(jvmInlineNotNullNullableString.value)
    }

    @Test
    fun `When a value class field is nullable, and the serialized value is null, then the deserialized field is null`() {
      assertSerializedDeserialized(jvmInlineNullNullableString)
      assertNull(jvmInlineNullNullableString.value)
    }
  }

  @Nested
  inner class TypeValidationScenarios {
    @Test
    fun `When a value class has a backing field of a complex type containing a field with a type parameter, then it is deserialized as expected`() {
      assertSerializedDeserialized(jvmInlineComplexClassWithParameterizedField)
    }
  }
}

private val jvmInlineString = JvmInlineString("exampleValue")
private val jvmInlineInt = JvmInlineInt(10)
private val jvmInlineDouble = JvmInlineDouble(0.5)
private val exampleNestedClass = JvmInlineComplexClass.ExampleNestedClass(
  stringValue = "a string",
  intValue = 10
)
private val jvmInlineComplexClass = JvmInlineComplexClass(value = exampleNestedClass)
private val jvmInlineListInt = JvmInlineListInt(list = listOf(0, 2, 99))
private val jvmInlineMapStringNullableInt = JvmInlineMapStringNullableInt(mapOf("first" to 1, "missing" to null))
private val jvmInlineMapComplexClass = JvmInlineMapComplexClass(mapOf("key" to jvmInlineComplexClass))
private val jvmInlineComplexClassWithParameterizedField = JvmInlineComplexClassWithParameterizedField(
  value = JvmInlineComplexClassWithParameterizedField.ExampleNestedClassWithParameterizedField(
    strings = listOf("i", "have", "strings"),
    ints = listOf(5, 10)
  )
)

private val jvmInlineStringMultipleConstructorUsage = JvmInlineString("base", "Appended")
private val jvmInlineNotNullNullableString = JvmInlineNullableString("notNull")
private val jvmInlineNullNullableString = JvmInlineNullableString(null)
private val jvmInlineUInt = JvmInlineUInt(unsignedValue = 99u)
private val dataClassWithUInt = DataClassWithUInt(Int.MAX_VALUE.toUInt() + Short.MAX_VALUE.toUInt())
private val dataClassWithULong = DataClassWithULong(Long.MAX_VALUE.toULong() + Int.MAX_VALUE.toUInt())
private val dataClassWithUShort = DataClassWithUShort(
  (Short.MAX_VALUE.toUShort() + Byte.MAX_VALUE.toUShort()).toUShort()
)
private val dataClassWithUByte = DataClassWithUByte((Byte.MAX_VALUE.toUByte() + 10u).toUByte())
private val dataClassWithUIntAndString = DataClassWithUIntAndString(
  stringValue = "foo",
  unsignedValue = dataClassWithUInt.uInt
)

@Language("JSON")
private val instanceToJsonStringMap: MutableMap<Any, String> = mutableMapOf(
  jvmInlineString to """"${jvmInlineString.value}"""",
  jvmInlineInt to "${jvmInlineInt.value}",
  jvmInlineDouble to "${jvmInlineDouble.value}",
  jvmInlineComplexClass to
    """{"stringValue":"${jvmInlineComplexClass.value.stringValue}",""" +
    """"intValue":${jvmInlineComplexClass.value.intValue}}""",
  jvmInlineListInt to """[0,2,99]""",
  jvmInlineMapStringNullableInt to """{"first":${jvmInlineMapStringNullableInt.map["first"]},"missing":null}""",
  jvmInlineMapComplexClass to
    """{"key":{"stringValue":"${jvmInlineComplexClass.value.stringValue}",""" +
    """"intValue":${jvmInlineComplexClass.value.intValue}}}""",
  jvmInlineStringMultipleConstructorUsage to """"${jvmInlineStringMultipleConstructorUsage.value}"""",
  jvmInlineNotNullNullableString to """"${jvmInlineNotNullNullableString.value}"""",
  jvmInlineNullNullableString to """null""",
  jvmInlineComplexClassWithParameterizedField to """{"strings":["i","have","strings"],"ints":[5,10]}""",
  jvmInlineUInt to """${jvmInlineUInt.unsignedValue}""",
  dataClassWithULong to """{"uLong":${dataClassWithULong.uLong}}""",
  dataClassWithUInt to """{"uInt":${dataClassWithUInt.uInt}}""",
  dataClassWithUShort to """{"uShort":${dataClassWithUShort.uShort}}""",
  dataClassWithUByte to """{"uByte":${dataClassWithUByte.uByte}}""",
  dataClassWithUIntAndString to
    """{"stringValue":"${dataClassWithUIntAndString.stringValue}",""" +
    """"unsignedValue":${dataClassWithUIntAndString.unsignedValue}}"""
)
