package io.github.light0x00.lightregex.syntax

import io.github.light0x00.lightregex.NFAState
import io.github.light0x00.lightregex.Transition
import java.util.*

abstract class AST(vararg val children: AST) {
    var state: NFAState? = null
    lateinit var firstSet: Set<Transition>
    lateinit var followSet: Set<Transition>
    var nullable: Boolean = false
}

val EOF_TOKEN = Token(TokenType.EOF)

enum class TokenType(val label: String) {
    LITERAL(""), LITERAL_RANGE(""), LITERAL_SEQUENCE(""), LITERAL_ANY("."),
    STAR("*"), OR("|"), LEFT_PARENTHESIS("("), RIGHT_PARENTHESIS(")"),
    EOF("$")

}

class LiteralToken(val lexeme: String) : Token(TokenType.LITERAL) {

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
        return lexeme
    }
}

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
}

class UnaryExpr(expr: AST, val operator: Token) : AST(expr) {
    val expr: AST
        get() = this.children[0]

    override fun toString(): String = "($expr$operator)"
}

abstract class BinaryExpr(vararg children: AST) : AST(*children) {
    abstract val left: AST
    abstract val right: AST
}

class OrExpr(left: AST, val operator: Token, right: AST) : BinaryExpr(left, right) {

    override val left: AST
        get() = this.children[0]

    override val right: AST
        get() = this.children[1]

    override fun toString(): String = "($left$operator$right)"
}

class AndExpr(left: AST, right: AST) : BinaryExpr(left, right) {
    override fun toString(): String = "($left$right)"

    override val left: AST
        get() = this.children[0]

    override val right: AST
        get() = this.children[1]
}

class Accept : AST() {

}

class RegExpr(expr: AST, accept: Accept) : AST(expr, accept) {

    val expr: AST
        get() = this.children[0]
    val accept
        get() = this.children[1]

    override fun toString(): String = expr.toString()
}

