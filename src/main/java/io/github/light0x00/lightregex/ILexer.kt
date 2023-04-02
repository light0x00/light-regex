package io.github.light0x00.lightregex

import java.util.Objects

enum class TokenType {
    LITERAL, SPECIAL
}

data class Token(val type: TokenType, val lexeme: String) {

    override operator fun equals(other: Any?): Boolean {
        return when (other) {
            is Token -> type == other.type && lexeme == other.lexeme
            else -> false
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(type, lexeme)
    }

    override fun toString(): String {
        return if (type == TokenType.SPECIAL) "SPECIAL($lexeme)" else lexeme
    }

}

val EOF = Token(TokenType.SPECIAL, "")
val OR = Token(TokenType.SPECIAL, "|")
val DOT = Token(TokenType.SPECIAL, ".")
val STAR = Token(TokenType.SPECIAL, "*")
val LEFT_Parenthesis = Token(TokenType.SPECIAL, "(")
val RIGHT_Parenthesis = Token(TokenType.SPECIAL, ")")

/**
 * @author light
 * @since 2023/3/29
 */
interface ILexer : Iterator<Token>, ILocalizable {

    fun lookahead(): Token

    fun lookahead(n: Int): Token

    override fun hasNext(): Boolean {
        return lookahead() !== EOF
    }

    fun expectNext(expectation: Token): Token {
        return next().also {
            if (it != expectation) {
                throw LightRegexException(readUnexpectedErrorMsg(this, expectation.toString()))
            }
        }

    }

}