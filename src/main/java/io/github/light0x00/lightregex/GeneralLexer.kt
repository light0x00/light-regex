package io.github.light0x00.lightregex

import java.util.*

val Hex_Literal = setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
val Special_Char = setOf('*', '|', '.', '(', ')')


/**
 * @author light
 * @since 2023/3/29
 */
class GeneralLexer(private val reader: IReader) : ILexer, ILocalizable by reader {

    private val lookaheads = LinkedList<Token>()

    override fun lookahead(): Token {
        return lookahead(1)
    }

    override fun lookahead(n: Int): Token {
        assertTrue(n > 0)
        if (lookaheads.size < n) {
            for (i in 1..n - lookaheads.size) {
                lookaheads.offer(nextToken())
            }
        }
        return lookaheads[n - 1]
    }

    override fun next(): Token {
        return if (lookaheads.isEmpty()) nextToken() else lookaheads.poll()
    }

    private fun nextToken(): Token {
        return when (reader.peek()) {
            null -> {
                EOF
            }
            in Special_Char -> {
                Token(TokenType.SPECIAL, reader.read().toString())
            }
            else -> {
                readAsLiteral()?.let { Token(TokenType.LITERAL, it) }
                    ?: throw LightRegexException(
                        readUnexpectedErrorMsg(this, expected = "Unrecognized character")
                    )
            }
        }
    }

    private fun readAsLiteral(): String? {
        val lookahead = reader.peek()
        return when {
            lookahead == null -> {
                null
            }
            //转义字符
            lookahead == '\\' -> {
                readEscape()
            }
            //数字字母
            isLetter(lookahead) || isDigit(lookahead) -> {
                reader.read()!!.toString()
            }
            else -> {
                null
            }
        }
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
                    throw LightRegexException(readErrorMsg(this, "Invalid unicode literal"))
                }
                unicode.append(reader.read())
            }
            reader.read() //消耗掉 "}"
            unicodeHexToString(unicode.toString())
        }
        //其他
        else {
            reader.read()?.toString() ?: throw LightRegexException(readErrorMsg(this, "Invalid escape character"))
        }
    }

}