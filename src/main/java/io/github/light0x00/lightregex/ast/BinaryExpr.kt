package io.github.light0x00.lightregex.ast

abstract class BinaryExpr(vararg children: AST) : AST(*children) {
    abstract val left: AST
    abstract val right: AST
}