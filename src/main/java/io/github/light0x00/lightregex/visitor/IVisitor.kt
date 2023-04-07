package io.github.light0x00.lightregex.visitor

import io.github.light0x00.lightregex.syntax.AST


interface IVisitor {
    fun visit(ast: AST)
}