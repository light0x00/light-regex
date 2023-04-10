package io.github.light0x00.lightregex.ast

class UnaryExpr(expr: AST, val operator: Token) : AST(expr) {
    val expr: AST
        get() = this.children[0]

    override fun toString(): String = "($expr$operator)"

    override fun copy(): UnaryExpr {
        return UnaryExpr(expr.copy(), operator.copy())
    }
}