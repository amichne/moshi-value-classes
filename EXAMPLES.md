<details>

<summary>Example</summary>
JSON Literal:

```json
"exampleValue"
```

Kotlin Object:

```
JvmInlineString(value=exampleValue)
```

Base Moshi Result:

```
Expected BEGIN_OBJECT but was STRING at path $
```

Updated Moshi:

```json
"exampleValue"
```

</details>
<details>

<summary>Example</summary>
JSON Literal:

```json
10
```

Kotlin Object:

```
JvmInlineInt(value=10)
```

Base Moshi Result:

```
Expected BEGIN_OBJECT but was NUMBER at path $
```

Updated Moshi:

```json
10
```

</details>
<details>

<summary>Example</summary>
JSON Literal:

```json
0.5
```

Kotlin Object:

```
JvmInlineDouble(value=0.5)
```

Base Moshi Result:

```
Expected BEGIN_OBJECT but was NUMBER at path $
```

Updated Moshi:

```json
0.5
```

</details>
<details>

<summary>Example</summary>
JSON Literal:

```json
{
  "stringValue": "a string",
  "intValue": 10
}
```

Kotlin Object:

```
JvmInlineComplexClass(value=ExampleNestedClass(stringValue=a string, intValue=10))
```

Base Moshi Result:

```
Required value 'value' missing at $
```

Updated Moshi:

```json
{
  "stringValue": "a string",
  "intValue": 10
}
```

</details>
<details>

<summary>Example</summary>
JSON Literal:

```json
`[0, 2, 99]
```

Kotlin Object:

```
JvmInlineListInt(list=[0, 2, 99])
```

Base Moshi Result:

```
Expected BEGIN_OBJECT but was BEGIN_ARRAY at path $
```

Updated Moshi:

```json
[
  0,
  2,
  99
]
```

</details>
<details>

<summary>Example</summary>
JSON Literal:

```json
{
  "first": 1,
  "missing": null
}
```

Kotlin Object:

```
JvmInlineMapStringNullableInt(map={first=1, missing=null})
```

Base Moshi Result:

```
Required value 'map' missing at $
```

Updated Moshi:

```json
{
  "first": 1
}
```

</details>
<details>

<summary>Example</summary>
JSON Literal:

```json
{
  "key": {
    "stringValue": "a string",
    "intValue": 10
  }
}
```

Kotlin Object:

```
JvmInlineMapComplexClass(parameterizedValue={key=JvmInlineComplexClass(value=ExampleNestedClass(stringValue=a string, intValue=10))})
```

Base Moshi Result:

```
Required value 'parameterizedValue' missing at $
```

Updated Moshi:

```json
{
  "key": {
    "stringValue": "a string",
    "intValue": 10
  }
}
```

</details>
<details>

<summary>Example</summary>
JSON Literal:

```json
"baseAppended"
```

Kotlin Object:

```
JvmInlineString(value=baseAppended)
```

Base Moshi Result:

```
Expected BEGIN_OBJECT but was STRING at path $
```

Updated Moshi:

```json
"baseAppended"
```

</details>
<details>

<summary>Example</summary>
JSON Literal:

```json
"notNull"
```

Kotlin Object:

```
JvmInlineNullableString(value=notNull)
```

Base Moshi Result:

```
Expected BEGIN_OBJECT but was STRING at path $
```

Updated Moshi:

```json
"notNull"
```

</details>
<details>

<summary>Example</summary>
JSON Literal:

```json
null
```

Kotlin Object:

```
JvmInlineNullableString(value=null)
```

Base Moshi Result:

```
null
```

Updated Moshi:

```json
null
```

</details>
<details>

<summary>Example</summary>
JSON Literal:

```json
{
  "strings": [
    "i",
    "have",
    "strings"
  ],
  "ints": [
    5,
    10
  ]
}
```

Kotlin Object:

```
JvmInlineComplexClassWithParameterizedField(value=ExampleNestedClassWithParameterizedField(strings=[i, have, strings], ints=[5, 10]))
```

Base Moshi Result:

```
Required value 'value' missing at $
```

Updated Moshi:

```json
{
  "strings": [
    "i",
    "have",
    "strings"
  ],
  "ints": [
    5,
    10
  ]
}
```

</details>
<details>

<summary>Example</summary>
JSON Literal:

```json
99
```

Kotlin Object:

```
JvmInlineUInt(unsignedValue=99)
```

Base Moshi Result:

```
Platform class kotlin.UInt requires explicit JsonAdapter to be registered for class kotlin.UInt unsignedValue for class io.amichne.moshi.extension.JvmInlineUInt
```

Updated Moshi:

```json
99
```

</details>
<details>

<summary>Example</summary>
JSON Literal:

```json
{
  "uInt": 89
}
```

Kotlin Object:

```
DataClassWithUInt(uInt=89)
```

Base Moshi Result:

```
Platform class kotlin.UInt requires explicit JsonAdapter to be registered for class kotlin.UInt uInt for class io.amichne.moshi.extension.DataClassWithUInt
```

Updated Moshi:

```json
{
  "uInt": 89
}
```

</details>
<details>

<summary>Example</summary>
JSON Literal:

```json
{
  "uLong": 2147516414
}
```

Kotlin Object:

```
DataClassWithULong(uLong=2147516414)
```

Base Moshi Result:

```
Platform class kotlin.ULong requires explicit JsonAdapter to be registered for class kotlin.ULong uLong for class io.amichne.moshi.extension.DataClassWithULong
```

Updated Moshi:

```json
{
  "uLong": 2147516414
}
```

</details>
<details>

<summary>Example</summary>
JSON Literal:

```json
{
  "uShort": 13
}
```

Kotlin Object:

```
DataClassWithUShort(uShort=13)
```

Base Moshi Result:

```
Platform class kotlin.UShort requires explicit JsonAdapter to be registered for class kotlin.UShort uShort for class io.amichne.moshi.extension.DataClassWithUShort
```

Updated Moshi:

```json
{
  "uShort": 13
}
```

</details>
<details>

<summary>Example</summary>
JSON Literal:

```json
{
  "uByte": 137
}
```

Kotlin Object:

```
DataClassWithUByte(uByte=137)
```

Base Moshi Result:

```
Platform class kotlin.UByte requires explicit JsonAdapter to be registered for class kotlin.UByte uByte for class io.amichne.moshi.extension.DataClassWithUByte
```

Updated Moshi:

```json
{
  "uByte": 137
}
```

</details>
<details>

<summary>Example</summary>
JSON Literal:

```json
{
  "stringValue": "foo",
  "unsignedValue": 10
}
```

Kotlin Object:

```
DataClassWithUIntAndString(stringValue=foo, unsignedValue=10)
```

Base Moshi Result:

```
Platform class kotlin.UInt requires explicit JsonAdapter to be registered for class kotlin.UInt unsignedValue for class io.amichne.moshi.extension.DataClassWithUIntAndString
```

Updated Moshi:

```json
{
  "stringValue": "foo",
  "unsignedValue": 10
}
```
