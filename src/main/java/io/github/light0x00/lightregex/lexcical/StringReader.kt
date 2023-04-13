@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package io.github.light0x00.lightregex.lexcical

import io.github.light0x00.lightregex.common.LightRegexException
import io.github.light0x00.lightregex.common.Unicode
import io.github.light0x00.lightregex.common.assertTrue
import io.github.light0x00.lightregex.common.readUnexpectedErrorMsg
import kotlin.math.max
import kotlin.math.min


/**
 * @author light
 * @since 2023/3/24
 */
class StringReader(private val str: String) : IReader {

    private var line = 1
    private var column = 0
    private var idx = 0

    val codePoints: IntArray = str.codePoints().toArray()

    override fun read(): Int {
        val c = get(idx)
        if (c == Unicode.LINE_FEED) {
            line++
            column = 0
        } else {
            column++
        }
        if (c != Unicode.EOF)
            idx++
        return c
    }

    @Deprecated("暂时无用")
    fun match(vararg expectation: String): String {
        var matchStr: String? = null
        for (expStr in expectation) {
            var isAllCharsMatch = true
            var i = 0
            for (code in expStr.codePoints()) {
                if (get(idx + i++) != code) {
                    isAllCharsMatch = false
                    break
                }
            }
            if (isAllCharsMatch) {
                matchStr = expStr
                break
            }
        }
        if (matchStr == null) {
            throw LightRegexException(
                readUnexpectedErrorMsg(
                    this,
                    expectation.joinToString(separator = " or ", prefix = "\"", postfix = "\"")
                )
            )
        }
        for (i in matchStr.indices) {
            read()
        }
        return matchStr
    }

    override fun match(vararg expectation: Int): Int {
        for (exp in expectation) {
            if (exp == lookahead()) {
                return read()
            }
        }
        throw LightRegexException(
            readUnexpectedErrorMsg(
                this,
                expectation.joinToString(
                    separator = " or",
                    prefix = "\"",
                    postfix = "\"",
                    transform = Character::toString
                )
            )
        )
    }

    override fun lookahead(n: Int): Int {
        assertTrue(n > 0)
        return this[idx + (n - 1)]
    }

    override fun skip(n: Int) {
        for (i in 1..n) {
            read()
        }
    }

    override fun line(): Int {
        return line
    }

    override fun column(): Int {
        return column
    }

    override fun nearbyChars(): String {
        val start = max(idx - 20, 0)
        val end = min(idx + 20, str.length - 1)
        val spaceNum = if ((idx - start) >= column) column - 1 else idx - start - 1
        val builder = StringBuilder(end - start + 1 + spaceNum + 2)
        for (i in start..end) {
            builder.append(str[i])
        }
        builder.append('\n')
        for (i in 1..spaceNum) {
            builder.append(' ')
        }
        builder.append('^')
        return builder.toString()
    }

    private operator fun get(idx: Int): Int {
        return if (idx >= codePoints.size) Unicode.EOF else codePoints[idx]
    }

}