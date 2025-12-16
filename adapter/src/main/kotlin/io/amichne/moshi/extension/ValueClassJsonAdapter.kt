package io.amichne.moshi.extension

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.rawType
import java.lang.reflect.Constructor
import java.lang.reflect.Type

/**
 * @param T the not-null value class instance for which we're the value of the backing field from
 * @param ValueT the nullable type stored in the single backing field of Class<[T]>
 * @return the nullable value of type [ValueT] stored in the single backing field defined by this value class
 */
@Suppress("UNCHECKED_CAST")
private fun <T : Any, ValueT> T.backingField(): ValueT = this::class.java.declaredFields.first()
    .also { field -> field.isAccessible = true }
    .get(this) as ValueT

private class ValueClassAdapter<InlineT : Any, ValueT : Any>(
    val constructor: Constructor<out InlineT>,
    val adapter: JsonAdapter<ValueT>,
) : JsonAdapter<InlineT>() {
    override fun toJson(
        writer: JsonWriter,
        inlineT: InlineT?,
    ) {
        inlineT?.let {
            writer.jsonValue(adapter.toJsonValue(it.backingField()))
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override fun fromJson(reader: JsonReader): InlineT = reader.readJsonValue().let { jsonValue ->
        try {
            constructor.isAccessible = true
            constructor.newInstance(adapter.fromJsonValue(jsonValue))
        } catch (throwable: Throwable) {
            throw JsonDataException(
                "Could not parse ${constructor.declaringClass.simpleName} from JSON value $this at path ${reader.path}",
                throwable
            )
        }
    }
}

object ValueClassAdapterFactory : JsonAdapter.Factory {
    override fun create(
        type: Type,
        annotations: MutableSet<out Annotation>,
        moshi: Moshi,
    ): JsonAdapter<Any>? =
        if (type.rawType.kotlin.isValue) {
            val constructor = type.rawType.declaredConstructors.first { it.parameterCount == 1 } as Constructor<*>
            val valueType = type.rawType.declaredFields[0].genericType
            ValueClassAdapter(
                constructor = constructor,
                adapter = moshi.parameterizedAdapter(valueType.rawType, valueType),
            )
        } else null

    @Suppress("UNUSED_PARAMETER")
    private fun <V> Moshi.parameterizedAdapter(
        valueClass: Class<V>,
        valueType: Type,
    ): JsonAdapter<V> = adapter(valueType)
}
