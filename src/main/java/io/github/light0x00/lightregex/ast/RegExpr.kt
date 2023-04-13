package io.github.light0x00.lightregex.ast

class RegExpr(expr: AST, accept: Accept, val matchFromStart: Boolean = false) : AST(expr, accept) {

    val expr: AST
        get() = this.children[0]
    val accept: Accept
        get() = this.children[1] as Accept

    override fun toString(): String = expr.toString()

    override fun copy(): RegExpr {
        return RegExpr(expr.copy(), accept.copy())
    }
}