package io.github.light0x00.lightregex.ast

open class Token(val type: TokenType) : AST() {

    override fun toString(): String {
        return type.label
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Token -> type == other.type
            else -> false
        }
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }

    override fun copy(): Token {
        return Token(type)
    }
}