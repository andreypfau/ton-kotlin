package org.ton.tl

import io.ktor.utils.io.core.*
import org.intellij.lang.annotations.Language
import org.ton.crypto.crc32
import org.ton.tl.constructors.readIntTl
import org.ton.tl.constructors.writeIntTl
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

abstract class TlConstructor<T : Any>(
    val type: KType,
    @Language("TL")
    val schema: String,
    val id: Int = crc32(schema)
) : TlCodec<T> {
    constructor(type: KClass<T>, schema: String, id: Int = crc32(schema)) : this(type.createType(), schema, id)

    override fun encodeBoxed(value: T): ByteArray = buildPacket {
        encodeBoxed(this, value)
    }.readBytes()

    override fun encodeBoxed(output: Output, value: T) {
        output.writeIntTl(id)
        encode(output, value)
    }

    fun <R : Any> Output.writeBoxedTl(codec: TlCodec<R>, value: R) = codec.encodeBoxed(this, value)

    override fun decodeBoxed(input: Input): T {
        val actualId = input.readIntTl()
        require(actualId == id) { "Invalid ID. expected: $id actual: $actualId" }
        return decode(input)
    }

    fun <R : Any> Input.readBoxedTl(codec: TlConstructor<R>) = codec.decodeBoxed(this)

    override fun toString(): String = schema
}

