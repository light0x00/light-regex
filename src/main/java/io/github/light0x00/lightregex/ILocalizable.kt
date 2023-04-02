package io.github.light0x00.lightregex

/**
 * @author light
 * @since 2023/3/30
 */
interface ILocalizable {

    fun nearbyChars(): String

    fun line(): Int

    fun column(): Int
}