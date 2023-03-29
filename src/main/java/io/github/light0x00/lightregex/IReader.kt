package io.github.light0x00.lightregex

/**
 * @author light
 * @since 2023/3/24
 */
interface IReader {

    /**
     * 当读到最后一个字符，继续读则返回空
     */
    fun read(): Char?

    fun match(vararg expectation: String): String

    fun peek(): Char?

    fun line(): Int

    fun column(): Int

    fun nearbyChars(): String
}