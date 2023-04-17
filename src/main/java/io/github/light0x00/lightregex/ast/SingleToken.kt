package io.github.light0x00.lightregex.ast

import io.github.light0x00.lightregex.common.Unicode
import java.util.*

class SingleToken(val lexeme: Int) : AbstractToken(TokenType.SINGLE_LITERAL) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is SingleToken -> lexeme == other.lexeme
            else -> false
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(lexeme)
    }

    override fun toString(): String {
        return Unicode.toString(lexeme)
    }

    override fun copy(): SingleToken {
        return SingleToken(lexeme)
    }
}