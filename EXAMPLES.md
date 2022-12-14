<details>

<summary>JvmInlineString</summary>

> JSON Literal:
> ```json
> "exampleValue"
> ```
>
> Kotlin Object:
> ```
> JvmInlineString(value=exampleValue)
> ```
>
> Base Moshi Deserialization Result:
> ```
> Expected BEGIN_OBJECT but was STRING at path $
> ```
>
> Base Moshi Serialization Result:
> ```json
> {"value":"exampleValue"}
> ```
>
> Updated Moshi Deserialization Result:
> ```
> JvmInlineString(value=exampleValue)
> ```
>
> Updated Moshi Serialization Result:
> ```json
> "exampleValue"
> ```

</details>
<br>

<details>

<summary>JvmInlineInt</summary>

> JSON Literal:
> ```json
> 10
> ```
>
> Kotlin Object:
> ```
> JvmInlineInt(value=10)
> ```
>
> Base Moshi Deserialization Result:
> ```
> Expected BEGIN_OBJECT but was NUMBER at path $
> ```
>
> Base Moshi Serialization Result:
> ```json
> {"value":10}
> ```
>
> Updated Moshi Deserialization Result:
> ```
> JvmInlineInt(value=10)
> ```
>
> Updated Moshi Serialization Result:
> ```json
> 10
> ```

</details>
<br>

<details>

<summary>JvmInlineDouble</summary>

> JSON Literal:
> ```json
> 0.5
> ```
>
> Kotlin Object:
> ```
> JvmInlineDouble(value=0.5)
> ```
>
> Base Moshi Deserialization Result:
> ```
> Expected BEGIN_OBJECT but was NUMBER at path $
> ```
>
> Base Moshi Serialization Result:
> ```json
> {"value":0.5}
> ```
>
> Updated Moshi Deserialization Result:
> ```
> JvmInlineDouble(value=0.5)
> ```
>
> Updated Moshi Serialization Result:
> ```json
> 0.5
> ```

</details>
<br>

<details>

<summary>JvmInlineComplexClass</summary>

> JSON Literal:
> ```json
> {"stringValue":"a string","intValue":10}
> ```
>
> Kotlin Object:
> ```
> JvmInlineComplexClass(value=ExampleNestedClass(stringValue=a string, intValue=10))
> ```
>
> Base Moshi Deserialization Result:
> ```
> Required value 'value' missing at $
> ```
>
> Base Moshi Serialization Result:
> ```json
> {"value":{"stringValue":"a string","intValue":10}}
> ```
>
> Updated Moshi Deserialization Result:
> ```
> JvmInlineComplexClass(value=ExampleNestedClass(stringValue=a string, intValue=10))
> ```
>
> Updated Moshi Serialization Result:
> ```json
> {"stringValue":"a string","intValue":10}
> ```

</details>
<br>

<details>

<summary>JvmInlineListInt</summary>

> JSON Literal:
> ```json
> [0,2,99]
> ```
>
> Kotlin Object:
> ```
> JvmInlineListInt(list=[0, 2, 99])
> ```
>
> Base Moshi Deserialization Result:
> ```
> Expected BEGIN_OBJECT but was BEGIN_ARRAY at path $
> ```
>
> Base Moshi Serialization Result:
> ```json
> {"list":[0,2,99]}
> ```
>
> Updated Moshi Deserialization Result:
> ```
> JvmInlineListInt(list=[0, 2, 99])
> ```
>
> Updated Moshi Serialization Result:
> ```json
> [0,2,99]
> ```

</details>
<br>

<details>

<summary>JvmInlineMapStringNullableInt</summary>

> JSON Literal:
> ```json
> {"first":1,"missing":null}
> ```
>
> Kotlin Object:
> ```
> JvmInlineMapStringNullableInt(map={first=1, missing=null})
> ```
>
> Base Moshi Deserialization Result:
> ```
> Required value 'map' missing at $
> ```
>
> Base Moshi Serialization Result:
> ```json
> {"map":{"first":1}}
> ```
>
> Updated Moshi Deserialization Result:
> ```
> JvmInlineMapStringNullableInt(map={first=1, missing=null})
> ```
>
> Updated Moshi Serialization Result:
> ```json
> {"first":1}
> ```

</details>
<br>

<details>

<summary>JvmInlineMapComplexClass</summary>

> JSON Literal:
> ```json
> {"key":{"stringValue":"a string","intValue":10}}
> ```
>
> Kotlin Object:
> ```
> JvmInlineMapComplexClass(parameterizedValue={key=JvmInlineComplexClass(value=ExampleNestedClass(stringValue=a string, intValue=10))})
> ```
>
> Base Moshi Deserialization Result:
> ```
> Required value 'parameterizedValue' missing at $
> ```
>
> Base Moshi Serialization Result:
> ```json
> {"parameterizedValue":{"key":{"value":{"stringValue":"a string","intValue":10}}}}
> ```
>
> Updated Moshi Deserialization Result:
> ```
> JvmInlineMapComplexClass(parameterizedValue={key=JvmInlineComplexClass(value=ExampleNestedClass(stringValue=a string, intValue=10))})
> ```
>
> Updated Moshi Serialization Result:
> ```json
> {"key":{"stringValue":"a string","intValue":10}}
> ```

</details>
<br>

<details>

<summary>JvmInlineString</summary>

> JSON Literal:
> ```json
> "baseAppended"
> ```
>
> Kotlin Object:
> ```
> JvmInlineString(value=baseAppended)
> ```
>
> Base Moshi Deserialization Result:
> ```
> Expected BEGIN_OBJECT but was STRING at path $
> ```
>
> Base Moshi Serialization Result:
> ```json
> {"value":"baseAppended"}
> ```
>
> Updated Moshi Deserialization Result:
> ```
> JvmInlineString(value=baseAppended)
> ```
>
> Updated Moshi Serialization Result:
> ```json
> "baseAppended"
> ```

</details>
<br>

<details>

<summary>JvmInlineNullableString</summary>

> JSON Literal:
> ```json
> "notNull"
> ```
>
> Kotlin Object:
> ```
> JvmInlineNullableString(value=notNull)
> ```
>
> Base Moshi Deserialization Result:
> ```
> Expected BEGIN_OBJECT but was STRING at path $
> ```
>
> Base Moshi Serialization Result:
> ```json
> {"value":"notNull"}
> ```
>
> Updated Moshi Deserialization Result:
> ```
> JvmInlineNullableString(value=notNull)
> ```
>
> Updated Moshi Serialization Result:
> ```json
> "notNull"
> ```

</details>
<br>

<details>

<summary>JvmInlineNullableString</summary>

> JSON Literal:
> ```json
> null
> ```
>
> Kotlin Object:
> ```
> JvmInlineNullableString(value=null)
> ```
>
> Base Moshi Deserialization Result:
> ```
> null
> ```
>
> Base Moshi Serialization Result:
> ```json
> {}
> ```
>
> Updated Moshi Deserialization Result:
> ```
> JvmInlineNullableString(value=null)
> ```
>
> Updated Moshi Serialization Result:
> ```json
> null
> ```

</details>
<br>

<details>

<summary>JvmInlineComplexClassWithParameterizedField</summary>

> JSON Literal:
> ```json
> {"strings":["i","have","strings"],"ints":[5,10]}
> ```
>
> Kotlin Object:
> ```
> JvmInlineComplexClassWithParameterizedField(value=ExampleNestedClassWithParameterizedField(strings=[i, have, strings], ints=[5, 10]))
> ```
>
> Base Moshi Deserialization Result:
> ```
> Required value 'value' missing at $
> ```
>
> Base Moshi Serialization Result:
> ```json
> {"value":{"strings":["i","have","strings"],"ints":[5,10]}}
> ```
>
> Updated Moshi Deserialization Result:
> ```
> JvmInlineComplexClassWithParameterizedField(value=ExampleNestedClassWithParameterizedField(strings=[i, have, strings], ints=[5, 10]))
> ```
>
> Updated Moshi Serialization Result:
> ```json
> {"strings":["i","have","strings"],"ints":[5,10]}
> ```

</details>
<br>

<details>

<summary>JvmInlineUInt</summary>

> JSON Literal:
> ```json
> 99
> ```
>
> Kotlin Object:
> ```
> JvmInlineUInt(unsignedValue=99)
> ```
>
> Base Moshi Deserialization Result:
> ```
> Platform class kotlin.UInt requires explicit JsonAdapter to be registered for class kotlin.UInt unsignedValue for class io.amichne.moshi.extension.JvmInlineUInt
> ```
>
> Base Moshi Serialization Result:
> ```
> Platform class kotlin.UInt requires explicit JsonAdapter to be registered for class kotlin.UInt unsignedValue for class io.amichne.moshi.extension.JvmInlineUInt
> ```
>
> Updated Moshi Deserialization Result:
> ```
> JvmInlineUInt(unsignedValue=99)
> ```
>
> Updated Moshi Serialization Result:
> ```json
> 99
> ```

</details>
<br>

<details>

<summary>DataClassWithULong</summary>

> JSON Literal:
> ```json
> {"uLong":9223372039002259454}
> ```
>
> Kotlin Object:
> ```
> DataClassWithULong(uLong=9223372039002259454)
> ```
>
> Base Moshi Deserialization Result:
> ```
> Platform class kotlin.ULong requires explicit JsonAdapter to be registered for class kotlin.ULong uLong for class io.amichne.moshi.extension.DataClassWithULong
> ```
>
> Base Moshi Serialization Result:
> ```
> Platform class kotlin.ULong requires explicit JsonAdapter to be registered for class kotlin.ULong uLong for class io.amichne.moshi.extension.DataClassWithULong
> ```
>
> Updated Moshi Deserialization Result:
> ```
> DataClassWithULong(uLong=9223372039002259454)
> ```
>
> Updated Moshi Serialization Result:
> ```json
> {"uLong":9223372039002259454}
> ```

</details>
<br>

<details>

<summary>DataClassWithUInt</summary>

> JSON Literal:
> ```json
> {"uInt":2147516414}
> ```
>
> Kotlin Object:
> ```
> DataClassWithUInt(uInt=2147516414)
> ```
>
> Base Moshi Deserialization Result:
> ```
> Platform class kotlin.UInt requires explicit JsonAdapter to be registered for class kotlin.UInt uInt for class io.amichne.moshi.extension.DataClassWithUInt
> ```
>
> Base Moshi Serialization Result:
> ```
> Platform class kotlin.UInt requires explicit JsonAdapter to be registered for class kotlin.UInt uInt for class io.amichne.moshi.extension.DataClassWithUInt
> ```
>
> Updated Moshi Deserialization Result:
> ```
> DataClassWithUInt(uInt=2147516414)
> ```
>
> Updated Moshi Serialization Result:
> ```json
> {"uInt":2147516414}
> ```

</details>
<br>

<details>

<summary>DataClassWithUShort</summary>

> JSON Literal:
> ```json
> {"uShort":32894}
> ```
>
> Kotlin Object:
> ```
> DataClassWithUShort(uShort=32894)
> ```
>
> Base Moshi Deserialization Result:
> ```
> Platform class kotlin.UShort requires explicit JsonAdapter to be registered for class kotlin.UShort uShort for class io.amichne.moshi.extension.DataClassWithUShort
> ```
>
> Base Moshi Serialization Result:
> ```
> Platform class kotlin.UShort requires explicit JsonAdapter to be registered for class kotlin.UShort uShort for class io.amichne.moshi.extension.DataClassWithUShort
> ```
>
> Updated Moshi Deserialization Result:
> ```
> DataClassWithUShort(uShort=32894)
> ```
>
> Updated Moshi Serialization Result:
> ```json
> {"uShort":32894}
> ```

</details>
<br>

<details>

<summary>DataClassWithUByte</summary>

> JSON Literal:
> ```json
> {"uByte":137}
> ```
>
> Kotlin Object:
> ```
> DataClassWithUByte(uByte=137)
> ```
>
> Base Moshi Deserialization Result:
> ```
> Platform class kotlin.UByte requires explicit JsonAdapter to be registered for class kotlin.UByte uByte for class io.amichne.moshi.extension.DataClassWithUByte
> ```
>
> Base Moshi Serialization Result:
> ```
> Platform class kotlin.UByte requires explicit JsonAdapter to be registered for class kotlin.UByte uByte for class io.amichne.moshi.extension.DataClassWithUByte
> ```
>
> Updated Moshi Deserialization Result:
> ```
> DataClassWithUByte(uByte=137)
> ```
>
> Updated Moshi Serialization Result:
> ```json
> {"uByte":137}
> ```

</details>
<br>

<details>

<summary>DataClassWithUIntAndString</summary>

> JSON Literal:
> ```json
> {"stringValue":"foo","unsignedValue":2147516414}
> ```
>
> Kotlin Object:
> ```
> DataClassWithUIntAndString(stringValue=foo, unsignedValue=2147516414)
> ```
>
> Base Moshi Deserialization Result:
> ```
> Platform class kotlin.UInt requires explicit JsonAdapter to be registered for class kotlin.UInt unsignedValue for class io.amichne.moshi.extension.DataClassWithUIntAndString
> ```
>
> Base Moshi Serialization Result:
> ```
> Platform class kotlin.UInt requires explicit JsonAdapter to be registered for class kotlin.UInt unsignedValue for class io.amichne.moshi.extension.DataClassWithUIntAndString
> ```
>
> Updated Moshi Deserialization Result:
> ```
> DataClassWithUIntAndString(stringValue=foo, unsignedValue=2147516414)
> ```
>
> Updated Moshi Serialization Result:
> ```json
> {"stringValue":"foo","unsignedValue":2147516414}
> ```

</details>
<br>
