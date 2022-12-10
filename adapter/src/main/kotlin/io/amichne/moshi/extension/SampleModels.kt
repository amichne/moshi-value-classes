package io.amichne.moshi.extension

// Generified value classes are being added in Kotlin 1.8 (as experimental), need to support at that point
// https://kotlinlang.org/docs/inline-classes.html#mangling
// @JvmInline
// value class JvmInlineGeneric<ValueT: Any>(val value: ValueT)

@JvmInline
value class JvmInlineNullableString(val value: String?)

@JvmInline
value class JvmInlineString(val value: String) {
  constructor(
    baseString: String,
    appendedString: String,
  ) : this(baseString + appendedString)

  val secondValue: Char
    get() = value.first()
}

@JvmInline
value class JvmInlineInt(val value: Int)

@JvmInline
value class JvmInlineUInt(val value: UInt)

data class DataClassWithUInt(val value: UInt)

@JvmInline
value class JvmInlineDouble(val value: Double)

@JvmInline
value class JvmInlineComplexClass(
  val value: ExampleNestedClass,
) {
  data class ExampleNestedClass(
    val stringValue: String,
    val intValue: Int,
  )
}

@JvmInline
value class JvmInlineListInt(val list: List<Int>)

@JvmInline
value class JvmInlineMapStringNullableInt(val map: Map<String, Int?>)

@JvmInline
value class JvmInlineMapComplexClass(val parameterizedValue: Map<String, JvmInlineComplexClass>)

@JvmInline
value class JvmInlineComplexClassWithParameterizedField(
  val value: ExampleNestedClassWithParameterizedField,
) {
  data class ExampleNestedClassWithParameterizedField(
    val strings: List<String>,
    val ints: List<Int>,
  )
}
