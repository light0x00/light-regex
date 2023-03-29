package io.github.light0x00.lightregex

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

    override fun read(): Char? {
        val c = get(idx)
        if (c == '\n') {
            line++
            column = 0
        } else {
            column++
        }
        if (c != null)
            idx++
        return c
    }

    override fun match(vararg expectation: String): String {
        var matchStr: String? = null
        for (expStr in expectation) {
            var isAllCharsMatch = true
            for (i in expStr.indices) {
                if (get(idx + i) != expStr[i]) {
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

    override fun peek(): Char? {
        return this[idx]
    }

    override fun line(): Int {
        return line
    }

    override fun column(): Int {
        return column
    }

    override fun nearbyChars(): String {
        val start = max(idx - 10, 0)
        val end = min(idx + 10, str.length - 1)
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

    private operator fun get(idx: Int): Char? {
        return if (idx >= str.length) null else str[idx]
    }

}