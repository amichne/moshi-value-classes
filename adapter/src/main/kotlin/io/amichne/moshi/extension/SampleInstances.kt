@file:Suppress("MagicNumber")

package io.amichne.moshi.extension

val jvmInlineString = JvmInlineString("exampleValue")
val jvmInlineInt = JvmInlineInt(10)
val jvmInlineDouble = JvmInlineDouble(0.5)
val exampleNestedClass = JvmInlineComplexClass.ExampleNestedClass(
    stringValue = "a string",
    intValue = 10
)
val jvmInlineComplexClass = JvmInlineComplexClass(value = exampleNestedClass)
val jvmInlineListInt = JvmInlineListInt(list = listOf(0, 2, 99))
val jvmInlineMapStringNullableInt = JvmInlineMapStringNullableInt(mapOf("first" to 1, "missing" to null))
val jvmInlineMapComplexClass = JvmInlineMapComplexClass(mapOf("key" to jvmInlineComplexClass))
val exampleNestedClassWithParameterizedField =
    JvmInlineComplexClassWithParameterizedField.ExampleNestedClassWithParameterizedField(
        strings = listOf("i", "have", "strings"),
        ints = listOf(5, 10)
    )
val jvmInlineComplexClassWithParameterizedField = JvmInlineComplexClassWithParameterizedField(
    value = exampleNestedClassWithParameterizedField
)

val jvmInlineStringMultipleConstructorUsage = JvmInlineString("base", "Appended")
val jvmInlineNotNullNullableString = JvmInlineNullableString("notNull")
val jvmInlineNullNullableString = JvmInlineNullableString(null)

val jvmInlineValuesToStringRepresentation: MutableMap<Any, String> = mutableMapOf(
    jvmInlineString to """"exampleValue"""",
    jvmInlineInt to "10",
    jvmInlineDouble to "0.5",
    jvmInlineComplexClass to """{"stringValue":"a string","intValue":10}""",
    jvmInlineListInt to """[0,2,99]""",
    jvmInlineMapStringNullableInt to """{"first":1,"missing":null}""",
    jvmInlineMapComplexClass to """{"key":{"stringValue":"a string","intValue":10}}""",
    jvmInlineStringMultipleConstructorUsage to """"baseAppended"""",
    jvmInlineNotNullNullableString to """"notNull"""",
    jvmInlineNullNullableString to """null""",
    jvmInlineComplexClassWithParameterizedField to """{"strings":["i","have","strings"],"ints":[5, 10]}"""
)

val jvmInlineValuesList = jvmInlineValuesToStringRepresentation.keys.toList()
