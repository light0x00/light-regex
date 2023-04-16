package io.github.light0x00.lightregex.ast

abstract class AbstractToken(val type: TokenType) : AST() {

}

open class Token(type: TokenType) : AbstractToken(type) {

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