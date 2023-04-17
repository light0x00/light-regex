package io.github.light0x00.lightregex.ast

class LiteralRangeToken(val from: Int, val to: Int) : AbstractToken(TokenType.RANGE_LITERAL) {
    override fun toString(): String {
        return "${Character.toString(from)}-${Character.toString(to)}"
    }

    override fun copy(): LiteralRangeToken {
        return LiteralRangeToken(from, to)
    }
}