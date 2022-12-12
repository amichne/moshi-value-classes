# Supporting `value class` in Moshi

## Problem Statement

As of writing the most current version of Moshi is [`1.14.0`](https://github.com/square/moshi/releases/tag/1.14.0)
and does not support serialization of classes with the
[`value`](https://kotlinlang.org/docs/inline-classes.html) modifier.

There is an open [issue](https://github.com/square/moshi/issues/1170) around this topic currently.

This also indirectly causes an issue for [unsigned integer](https://kotlinlang.org/docs/unsigned-integer-types.html)
due to the fact that:
> Unsigned numbers are implemented as [inline classes](https://kotlinlang.org/docs/inline-classes.html)
> with the single storage property of the corresponding signed counterpart type of the same width.
> Nevertheless, changing type from unsigned type to signed counterpart (and vice versa) is a binary incompatible change.

The expected behavior is the inlining of the single property present in the class, effectively "flattening"
the declared property name, such that it's not present in the serialized representation.

### Current Behavior - Serialization

If we were to serialize a `value class` by running the below code snippet:

```kotlin
@JvmInline
value class Username(val input: String)

val username = Username(input = "amichne")
val jsonString = Moshi.Builder().build()
    .adapter<Username>()
    .toJson(username)
```

We would _expect_ the below statement to return `true`:

```kotlin
jsonString == "amichne"
```

Currently, the result is `false`, as `jsonString` would actually equal:

```json
{
  "input": "amichne"
}
```

Clearly we receive an **incorrect** JSON representation. The expected result is a string token, but the actual result
is an object with a single key containing a value that is the expected string token.

### Current Behavior - Deserialization

Similarly, if we wanted to deserialize a JSON representation to a `value class` instance via the below code snippet:

```kotlin
@JvmInline
value class Username(val input: String)

val jsonStringForUsername = "amichne"
val username = Moshi.Builder().build()
    .adapter<Username>()
    .fromJson(jsonStringForUsername)
```

We will receive the below exception:

```kotlin
JsonDataException(message = "Expected BEGIN_OBJECT but was STRING at path $")
```

### Unsigned Integer Complications

Unsigned integers observe the same issue as above, but they have another issue as well.

The compiled bytecode for unsigned integers has a single constructor, which accepts a signed integer.
This causes a lookup failure when reflection is trying to fetch the constructor for the type, as it's
expecting an unsigned integer.

## Solution

### Value class

#### Adapter Resolution

1. If the type being looked up has the `value` modifier and is not in `[ULong, UInt, UShort, UByte]`
    - Else return `null`
2. Then resolve the constructor for the type, as well as the type of its declared property
    - _We can guarantee there is only a declared property, per the
      [Kotlin Specification for value classes](https://kotlinlang.org/spec/declarations.html#value-class-declaration)_
3. Resolve the Moshi adapter for the type of the declared property
4. Create the `ValueClassAdapter` with the resolved constructor and adapter

#### Serialization

1. Retrieve the declared property reflectively
2. Serialize the value of the retrieved property using `<ValueClassAdapter>.adapter`
3. Write the result of serialization to the `JsonWriter`

#### Deserialization

1. Read the next JSON value from the `JsonReader`
2. Deserialize the JSON content to the type of the declared property using `<ValueClassAdapter>.adapter`
3. Invoke `<ValueClassAdapter>.constructor` with the declared property parameter and return the result

### Unsigned integers

#### Adapter Resolution

1. If the type (`T`) being looked up is in `[ULong, UInt, UShort, UByte]`
   - Else return `null`
2. Then return the `UnsignedTypeAdapter<T>` with the type mapper `ULong.() -> T`

#### Serialization
1. Convert the unsigned value to a string representation
2. Write the string representation **without any additional encoding**
   - This is required to avoid the scenario where a unsigned number with a most significant bit being written as a negative value

#### Deserialization
1. Peek the next token in the `JsonReader`
2. If it is `NUMBER`
   - Else if it's `NULL` read the next null via `reader.nextNull()`
   - Else throw an exception, as we know it must be an illegal value
3. Then read it into a string literal
   - This is required to avoid the situation where we would read an unsigned long larger than `Long.MAX_VALUE` which would result in a error despite valid data being deserialized
4. Convert the string to an unsigned long
5. Convert the unsigned long to the requested unsigned type



The code in [`ValueClassAdapterFactory`](./adapter/src/main/kotlin/io/amichne/moshi/extension/ValueClassAdapterFactory.kt)
solves the issue for user-created `value class` declarations.

The code in [`UnsignedAdapterFactory`](./adapter/src/main/kotlin/io/amichne/moshi/extension/UnsignedAdapterFactory.kt)
solves the specific complications introduced in the case of unsigned integers.

## Highlights

### Inline class support

JSON Literal:

```json
"exampleValue"
```

Kotlin Object:

```kotlin
@JvmInline
value class JvmInlineString(val value: String)

JvmInlineString(value = exampleValue)
```

Base Moshi Deserialization Result:

```
Expected BEGIN_OBJECT but was STRING at path $
```

Base Moshi Serialization Result:

```json
{
  "value": "exampleValue"
}
```

Updated Moshi Deserialization Result:

```
JvmInlineString(value=exampleValue)
```

Updated Moshi Serialization Result:

```json
"exampleValue"
```


<br>

### Unsigned number support

JSON Literal:

```json
{
  "uLong": 9223372039002259454
}
```

Kotlin Object:

```kotlin
DataClassWithULong(
    uLong = 9223372039002259454u
)
```

Base Moshi Deserialization Result:

```
Platform class kotlin.ULong requires explicit JsonAdapter to be registered for class kotlin.ULong
```

Base Moshi Serialization Result:

```
Platform class kotlin.ULong requires explicit JsonAdapter to be registered for class kotlin.ULong
```

Updated Moshi Deserialization Result:

```kotlin
DataClassWithULong(
    uLong = 9223372039002259454u
)
```

Updated Moshi Serialization Result:

```json
{
  "uLong": 9223372039002259454
}
```

<br>

## All tested scenarios

<details>

<summary>Scenarios</summary>

> <details>
>
> <summary>JvmInlineString</summary>
>
> > JSON Literal:
> > ```json
> > "exampleValue"
> > ```
> >
> > Kotlin Object:
> > ```
> > JvmInlineString(value=exampleValue)
> > ```
> >
> > Base Moshi Deserialization Result:
> > ```
> > Expected BEGIN_OBJECT but was STRING at path $
> > ```
> >
> > Base Moshi Serialization Result:
> > ```json
> > {"value":"exampleValue"}
> > ```
> >
> > Updated Moshi Deserialization Result:
> > ```
> > JvmInlineString(value=exampleValue)
> > ```
> >
> > Updated Moshi Serialization Result:
> > ```json
> > "exampleValue"
> > ```
>
> </details>
>
> <details>
>
> <summary>JvmInlineInt</summary>
>
> > JSON Literal:
> > ```json
> > 10
> > ```
> >
> > Kotlin Object:
> > ```
> > JvmInlineInt(value=10)
> > ```
> >
> > Base Moshi Deserialization Result:
> > ```
> > Expected BEGIN_OBJECT but was NUMBER at path $
> > ```
> >
> > Base Moshi Serialization Result:
> > ```json
> > {"value":10}
> > ```
> >
> > Updated Moshi Deserialization Result:
> > ```
> > JvmInlineInt(value=10)
> > ```
> >
> > Updated Moshi Serialization Result:
> > ```json
> > 10
> > ```
>
> </details>
>
> <details>
>
> <summary>JvmInlineDouble</summary>
>
> > JSON Literal:
> > ```json
> > 0.5
> > ```
> >
> > Kotlin Object:
> > ```
> > JvmInlineDouble(value=0.5)
> > ```
> >
> > Base Moshi Deserialization Result:
> > ```
> > Expected BEGIN_OBJECT but was NUMBER at path $
> > ```
> >
> > Base Moshi Serialization Result:
> > ```json
> > {"value":0.5}
> > ```
> >
> > Updated Moshi Deserialization Result:
> > ```
> > JvmInlineDouble(value=0.5)
> > ```
> >
> > Updated Moshi Serialization Result:
> > ```json
> > 0.5
> > ```
>
> </details>
>
> <details>
>
> <summary>JvmInlineComplexClass</summary>
>
> > JSON Literal:
> > ```json
> > {"stringValue":"a string","intValue":10}
> > ```
> >
> > Kotlin Object:
> > ```
> > JvmInlineComplexClass(value=ExampleNestedClass(stringValue=a string, intValue=10))
> > ```
> >
> > Base Moshi Deserialization Result:
> > ```
> > Required value 'value' missing at $
> > ```
> >
> > Base Moshi Serialization Result:
> > ```json
> > {"value":{"stringValue":"a string","intValue":10}}
> > ```
> >
> > Updated Moshi Deserialization Result:
> > ```
> > JvmInlineComplexClass(value=ExampleNestedClass(stringValue=a string, intValue=10))
> > ```
> >
> > Updated Moshi Serialization Result:
> > ```json
> > {"stringValue":"a string","intValue":10}
> > ```
>
> </details>
>
> <details>
>
> <summary>JvmInlineListInt</summary>
>
> > JSON Literal:
> > ```json
> > [0,2,99]
> > ```
> >
> > Kotlin Object:
> > ```
> > JvmInlineListInt(list=[0, 2, 99])
> > ```
> >
> > Base Moshi Deserialization Result:
> > ```
> > Expected BEGIN_OBJECT but was BEGIN_ARRAY at path $
> > ```
> >
> > Base Moshi Serialization Result:
> > ```json
> > {"list":[0,2,99]}
> > ```
> >
> > Updated Moshi Deserialization Result:
> > ```
> > JvmInlineListInt(list=[0, 2, 99])
> > ```
> >
> > Updated Moshi Serialization Result:
> > ```json
> > [0,2,99]
> > ```
>
> </details>
>
> <details>
>
> <summary>JvmInlineMapStringNullableInt</summary>
>
> > JSON Literal:
> > ```json
> > {"first":1,"missing":null}
> > ```
> >
> > Kotlin Object:
> > ```
> > JvmInlineMapStringNullableInt(map={first=1, missing=null})
> > ```
> >
> > Base Moshi Deserialization Result:
> > ```
> > Required value 'map' missing at $
> > ```
> >
> > Base Moshi Serialization Result:
> > ```json
> > {"map":{"first":1}}
> > ```
> >
> > Updated Moshi Deserialization Result:
> > ```
> > JvmInlineMapStringNullableInt(map={first=1, missing=null})
> > ```
> >
> > Updated Moshi Serialization Result:
> > ```json
> > {"first":1}
> > ```
>
> </details>
>
> <details>
>
> <summary>JvmInlineMapComplexClass</summary>
>
> > JSON Literal:
> > ```json
> > {"key":{"stringValue":"a string","intValue":10}}
> > ```
> >
> > Kotlin Object:
> > ```
> > JvmInlineMapComplexClass(parameterizedValue={key=JvmInlineComplexClass(value=ExampleNestedClass(stringValue=a string, intValue=10))})
> > ```
> >
> > Base Moshi Deserialization Result:
> > ```
> > Required value 'parameterizedValue' missing at $
> > ```
> >
> > Base Moshi Serialization Result:
> > ```json
> > {"parameterizedValue":{"key":{"value":{"stringValue":"a string","intValue":10}}}}
> > ```
> >
> > Updated Moshi Deserialization Result:
> > ```
> > JvmInlineMapComplexClass(parameterizedValue={key=JvmInlineComplexClass(value=ExampleNestedClass(stringValue=a string, intValue=10))})
> > ```
> >
> > Updated Moshi Serialization Result:
> > ```json
> > {"key":{"stringValue":"a string","intValue":10}}
> > ```
>
> </details>
>
> <details>
>
> <summary>JvmInlineString</summary>
>
> > JSON Literal:
> > ```json
> > "baseAppended"
> > ```
> >
> > Kotlin Object:
> > ```
> > JvmInlineString(value=baseAppended)
> > ```
> >
> > Base Moshi Deserialization Result:
> > ```
> > Expected BEGIN_OBJECT but was STRING at path $
> > ```
> >
> > Base Moshi Serialization Result:
> > ```json
> > {"value":"baseAppended"}
> > ```
> >
> > Updated Moshi Deserialization Result:
> > ```
> > JvmInlineString(value=baseAppended)
> > ```
> >
> > Updated Moshi Serialization Result:
> > ```json
> > "baseAppended"
> > ```
>
> </details>
>
> <details>
>
> <summary>JvmInlineNullableString</summary>
>
> > JSON Literal:
> > ```json
> > "notNull"
> > ```
> >
> > Kotlin Object:
> > ```
> > JvmInlineNullableString(value=notNull)
> > ```
> >
> > Base Moshi Deserialization Result:
> > ```
> > Expected BEGIN_OBJECT but was STRING at path $
> > ```
> >
> > Base Moshi Serialization Result:
> > ```json
> > {"value":"notNull"}
> > ```
> >
> > Updated Moshi Deserialization Result:
> > ```
> > JvmInlineNullableString(value=notNull)
> > ```
> >
> > Updated Moshi Serialization Result:
> > ```json
> > "notNull"
> > ```
>
> </details>
>
> <details>
>
> <summary>JvmInlineNullableString</summary>
>
> > JSON Literal:
> > ```json
> > null
> > ```
> >
> > Kotlin Object:
> > ```
> > JvmInlineNullableString(value=null)
> > ```
> >
> > Base Moshi Deserialization Result:
> > ```
> > null
> > ```
> >
> > Base Moshi Serialization Result:
> > ```json
> > {}
> > ```
> >
> > Updated Moshi Deserialization Result:
> > ```
> > JvmInlineNullableString(value=null)
> > ```
> >
> > Updated Moshi Serialization Result:
> > ```json
> > null
> > ```
>
> </details>
>
> <details>
>
> <summary>JvmInlineComplexClassWithParameterizedField</summary>
>
> > JSON Literal:
> > ```json
> > {"strings":["i","have","strings"],"ints":[5,10]}
> > ```
> >
> > Kotlin Object:
> > ```
> > JvmInlineComplexClassWithParameterizedField(value=ExampleNestedClassWithParameterizedField(strings=[i, have, strings], ints=[5, 10]))
> > ```
> >
> > Base Moshi Deserialization Result:
> > ```
> > Required value 'value' missing at $
> > ```
> >
> > Base Moshi Serialization Result:
> > ```json
> > {"value":{"strings":["i","have","strings"],"ints":[5,10]}}
> > ```
> >
> > Updated Moshi Deserialization Result:
> > ```
> > JvmInlineComplexClassWithParameterizedField(value=ExampleNestedClassWithParameterizedField(strings=[i, have, strings], ints=[5, 10]))
> > ```
> >
> > Updated Moshi Serialization Result:
> > ```json
> > {"strings":["i","have","strings"],"ints":[5,10]}
> > ```
>
> </details>
>
> <details>
>
> <summary>JvmInlineUInt</summary>
>
> > JSON Literal:
> > ```json
> > 99
> > ```
> >
> > Kotlin Object:
> > ```
> > JvmInlineUInt(unsignedValue=99)
> > ```
> >
> > Base Moshi Deserialization Result:
> > ```
> > Platform class kotlin.UInt requires explicit JsonAdapter to be registered for class kotlin.UInt unsignedValue for class io.amichne.moshi.extension.JvmInlineUInt
> > ```
> >
> > Base Moshi Serialization Result:
> > ```
> > Platform class kotlin.UInt requires explicit JsonAdapter to be registered for class kotlin.UInt unsignedValue for class io.amichne.moshi.extension.JvmInlineUInt
> > ```
> >
> > Updated Moshi Deserialization Result:
> > ```
> > JvmInlineUInt(unsignedValue=99)
> > ```
> >
> > Updated Moshi Serialization Result:
> > ```json
> > 99
> > ```
>
> </details>
>
> <details>
>
> <summary>DataClassWithULong</summary>
>
> > JSON Literal:
> > ```json
> > {"uLong":9223372039002259454}
> > ```
> >
> > Kotlin Object:
> > ```
> > DataClassWithULong(uLong=9223372039002259454)
> > ```
> >
> > Base Moshi Deserialization Result:
> > ```
> > Platform class kotlin.ULong requires explicit JsonAdapter to be registered for class kotlin.ULong uLong for class io.amichne.moshi.extension.DataClassWithULong
> > ```
> >
> > Base Moshi Serialization Result:
> > ```
> > Platform class kotlin.ULong requires explicit JsonAdapter to be registered for class kotlin.ULong uLong for class io.amichne.moshi.extension.DataClassWithULong
> > ```
> >
> > Updated Moshi Deserialization Result:
> > ```
> > DataClassWithULong(uLong=9223372039002259454)
> > ```
> >
> > Updated Moshi Serialization Result:
> > ```json
> > {"uLong":9223372039002259454}
> > ```
>
> </details>
>
> <details>
>
> <summary>DataClassWithUInt</summary>
>
> > JSON Literal:
> > ```json
> > {"uInt":2147516414}
> > ```
> >
> > Kotlin Object:
> > ```
> > DataClassWithUInt(uInt=2147516414)
> > ```
> >
> > Base Moshi Deserialization Result:
> > ```
> > Platform class kotlin.UInt requires explicit JsonAdapter to be registered for class kotlin.UInt uInt for class io.amichne.moshi.extension.DataClassWithUInt
> > ```
> >
> > Base Moshi Serialization Result:
> > ```
> > Platform class kotlin.UInt requires explicit JsonAdapter to be registered for class kotlin.UInt uInt for class io.amichne.moshi.extension.DataClassWithUInt
> > ```
> >
> > Updated Moshi Deserialization Result:
> > ```
> > DataClassWithUInt(uInt=2147516414)
> > ```
> >
> > Updated Moshi Serialization Result:
> > ```json
> > {"uInt":2147516414}
> > ```
>
> </details>
>
> <details>
>
> <summary>DataClassWithUShort</summary>
>
> > JSON Literal:
> > ```json
> > {"uShort":32894}
> > ```
> >
> > Kotlin Object:
> > ```
> > DataClassWithUShort(uShort=32894)
> > ```
> >
> > Base Moshi Deserialization Result:
> > ```
> > Platform class kotlin.UShort requires explicit JsonAdapter to be registered for class kotlin.UShort uShort for class io.amichne.moshi.extension.DataClassWithUShort
> > ```
> >
> > Base Moshi Serialization Result:
> > ```
> > Platform class kotlin.UShort requires explicit JsonAdapter to be registered for class kotlin.UShort uShort for class io.amichne.moshi.extension.DataClassWithUShort
> > ```
> >
> > Updated Moshi Deserialization Result:
> > ```
> > DataClassWithUShort(uShort=32894)
> > ```
> >
> > Updated Moshi Serialization Result:
> > ```json
> > {"uShort":32894}
> > ```
>
> </details>
>
> <details>
>
> <summary>DataClassWithUByte</summary>
>
> > JSON Literal:
> > ```json
> > {"uByte":137}
> > ```
> >
> > Kotlin Object:
> > ```
> > DataClassWithUByte(uByte=137)
> > ```
> >
> > Base Moshi Deserialization Result:
> > ```
> > Platform class kotlin.UByte requires explicit JsonAdapter to be registered for class kotlin.UByte uByte for class io.amichne.moshi.extension.DataClassWithUByte
> > ```
> >
> > Base Moshi Serialization Result:
> > ```
> > Platform class kotlin.UByte requires explicit JsonAdapter to be registered for class kotlin.UByte uByte for class io.amichne.moshi.extension.DataClassWithUByte
> > ```
> >
> > Updated Moshi Deserialization Result:
> > ```
> > DataClassWithUByte(uByte=137)
> > ```
> >
> > Updated Moshi Serialization Result:
> > ```json
> > {"uByte":137}
> > ```
>
> </details>
>
> <details>
>
> <summary>DataClassWithUIntAndString</summary>
>
> > JSON Literal:
> > ```json
> > {"stringValue":"foo","unsignedValue":2147516414}
> > ```
> >
> > Kotlin Object:
> > ```
> > DataClassWithUIntAndString(stringValue=foo, unsignedValue=2147516414)
> > ```
> >
> > Base Moshi Deserialization Result:
> > ```
> > Platform class kotlin.UInt requires explicit JsonAdapter to be registered for class kotlin.UInt unsignedValue for class io.amichne.moshi.extension.DataClassWithUIntAndString
> > ```
> >
> > Base Moshi Serialization Result:
> > ```
> > Platform class kotlin.UInt requires explicit JsonAdapter to be registered for class kotlin.UInt unsignedValue for class io.amichne.moshi.extension.DataClassWithUIntAndString
> > ```
> >
> > Updated Moshi Deserialization Result:
> > ```
> > DataClassWithUIntAndString(stringValue=foo, unsignedValue=2147516414)
> > ```
> >
> > Updated Moshi Serialization Result:
> > ```json
> > {"stringValue":"foo","unsignedValue":2147516414}
> > ```
>
> </details>
</details>
