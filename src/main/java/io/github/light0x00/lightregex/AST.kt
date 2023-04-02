package io.github.light0x00.lightregex

/**
 * @author light
 * @since 2023/3/29
 */
abstract class AST {

}

class PrimaryExpr(val token: Token) : AST() {
    override fun toString(): String = token.toString()
}

class UnaryExpr(val expr: AST, val operator: Token) : AST() {
    override fun toString(): String = "($expr$operator)"
}

class AndExpr(val left: AST, var right: AST? = null) : AST() {
    override fun toString(): String = "($left$right)"
}

class OrExpr(val left: AST, val operator: Token, var right: AST? = null) : AST() {
    override fun toString(): String = "($left$operator$right)"
}