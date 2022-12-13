@file:Suppress("MagicNumber")

package io.amichne.moshi.extension

import org.intellij.lang.annotations.Language

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
