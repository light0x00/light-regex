package io.github.light0x00.lightregex.ast

class OrExpr(left: AST, right: AST) : BinaryExpr(left, right) {

    override val left: AST
        get() = this.children[0]

    override val right: AST
        get() = this.children[1]

    override fun toString(): String = "($left|$right)"

    override fun copy(): OrExpr {
        return OrExpr(left.copy(), right.copy())
    }
}