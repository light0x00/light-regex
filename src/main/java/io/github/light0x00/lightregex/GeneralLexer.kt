package io.github.light0x00.lightregex

import java.util.*

val EOF = String()
val Hex_Literal = setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')


/**
 * @author light
 * @since 2023/3/29
 */
class GeneralLexer(private val reader: IReader) : IPredictableLexer {

    private val lookaheads = LinkedList<String>();

    override fun peek(): String {
        return peek(1)
    }

    override fun peek(n: Int): String {
        assertTrue(n > 0)
        if (lookaheads.size < n) {
            for (i in 1..n - lookaheads.size) {
                lookaheads.offer(nextToken())
            }
        }
        return lookaheads.get(n - 1)
    }

    override fun hasNext(): Boolean {
        return peek() !== EOF
    }

    override fun next(): String {
        return if (lookaheads.isEmpty()) nextToken() else lookaheads.poll();
    }

    private fun nextToken(): String {
        return when (reader.peek()) {
            null -> {
                EOF
            }
            '*', '|', '.', '(', ')' -> {
                reader.read().toString()
            }
            else -> {
                readAsLiteral() ?: throw LightRegexException(
                    readUnexpectedErrorMsg(reader, "Unrecognized character")
                )
            }
        }
    }

    private fun readAsLiteral(): String? {
        val token = StringBuilder()
        while (true) {
            when {
                reader.peek() == null -> {
                    break
                }
                //转义字符
                reader.peek() == '\\' -> {
                    token.append(readEscape())
                }
                //数字字母
                isLetter(reader.peek()!!) || isDigit(reader.peek()!!) -> {
                    token.append(reader.read())
                }
                else -> {
                    break
                }
            }
        }
        return if (token.isEmpty()) null else token.toString()
    }

    private fun readEscape(): String {
        reader.match("\\")
        val lookahead = reader.peek()
        //unicode
        return if (lookahead == 'u') {
            reader.read()
            reader.match("{")
            val unicode = StringBuilder(4)
            while (reader.peek() != '}') {
                if (!Hex_Literal.contains(reader.peek())) {
                    throw LightRegexException(readErrorMsg(reader, "Invalid unicode literal"))
                }
                unicode.append(reader.read())
            }
            reader.read() //消耗掉 "}"
            unicodeHexToString(unicode.toString())
        }
        //其他
        else {
            reader.read()?.toString() ?: throw LightRegexException(readErrorMsg(reader, "Invalid escape character"))
        }
    }
}