package io.github.light0x00.lightregex.ast

class AndExpr(left: AST, right: AST) : BinaryExpr(left, right) {
    override fun toString(): String = "($left$right)"

    override val left: AST
        get() = this.children[0]

    override val right: AST
        get() = this.children[1]

    override fun copy(): AndExpr {
        return AndExpr(left.copy(), right.copy())
    }
}