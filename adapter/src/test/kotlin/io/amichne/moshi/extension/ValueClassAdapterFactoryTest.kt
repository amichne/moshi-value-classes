package io.amichne.moshi.extension

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.fail
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.lang.reflect.Type
import java.util.stream.Stream
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private val moshi: Moshi = Moshi.Builder()
  .addLast(KotlinJsonAdapterFactory())
  .add(ValueClassAdapterFactory)
  .build()

private inline fun <reified T> Moshi.serialize(value: T): String = adapter(T::class.java).toJson(value)

private inline fun <reified K, reified V> Moshi.mapAdapter(
  keyType: Type = K::class.java,
  valueType: Type = V::class.java,
): JsonAdapter<Map<K, V>> = adapter(Types.newParameterizedType(Map::class.java, keyType, valueType))

@Suppress("MaxLineLength")
internal class ValueClassAdapterFactoryTest {
  fun assertSerializedDeserialized(original: Any) =
    when (val stringRepresentation = jvmInlineValuesToStringRepresentation[original]) {
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
  @Suppress("unused")
  inner class JsonTypeConversionTests {
    private fun inlineInstances(): Stream<Arguments> = Stream.of(
      Arguments.of(jvmInlineString),
      Arguments.of(jvmInlineInt),
      Arguments.of(jvmInlineDouble),
      Arguments.of(jvmInlineComplexClass),
      Arguments.of(jvmInlineListInt),
      Arguments.of(jvmInlineMapStringNullableInt),
      Arguments.of(jvmInlineMapComplexClass),
    )

    @ParameterizedTest
    @MethodSource("inlineInstances")
    fun `Validate result of serialization is equal`(
      value: Any,
    ) {
      // This check is here as we don't serialize key with null values by default
      if (value is JvmInlineMapStringNullableInt) {
        assertThat(moshi.serialize(value)).isEqualTo("""{"first":1}""")
      } else {
        assertThat(moshi.serialize(value)).isEqualTo(jvmInlineValuesToStringRepresentation[value])
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
  }

  @Nested
  inner class NullabilityScenarios {
    @Test
    fun `When a value class field is not nullable, and the serialized value is null, then an exception is thrown`() {
      val illegallyNullFieldString = """{"inner": null}"""
      assertThrows<JsonDataException> {
        moshi.mapAdapter<String, JvmInlineInt>().fromJson(illegallyNullFieldString)!!
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
