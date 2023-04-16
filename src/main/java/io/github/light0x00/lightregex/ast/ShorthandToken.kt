package io.github.light0x00.lightregex.ast

import io.github.light0x00.lightregex.common.Unicode
import java.util.Objects

class ShorthandToken(val symbol: Int) : Token(TokenType.SHORTHAND_SYMBOL) {
    override fun toString(): String {
        return "\\" + Unicode.toString(symbol)
    }

    override fun copy(): ShorthandToken {
        return ShorthandToken(symbol)
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is ShorthandToken -> other.symbol == symbol
            else -> false
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(TokenType.SHORTHAND_SYMBOL, symbol)
    }
}