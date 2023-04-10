package io.github.light0x00.lightregex.ast

class LiteralRangeToken(val from: Int, val to: Int) : Token(TokenType.SINGLE_LITERAL_RANGE) {
    override fun toString(): String {
        return "${Character.toString(from)}-${Character.toString(to)}"
    }

    override fun copy(): LiteralRangeToken {
        return LiteralRangeToken(from, to)
    }
}