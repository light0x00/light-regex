package io.github.light0x00.lightregex.visitor

import io.github.light0x00.lightregex.ast.AST


interface IVisitor {
    fun visit(ast: AST)
}