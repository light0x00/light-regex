package io.github.light0x00.lightregex.ast

import java.util.*

data class LiteralToken(val lexeme: Int) : Token(TokenType.SINGLE_LITERAL) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is LiteralToken -> lexeme == other.lexeme
            else -> false
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(lexeme)
    }

    override fun toString(): String {
        return Character.toString(lexeme)
    }

    override fun copy(): LiteralToken {
        return LiteralToken(lexeme)
    }
}