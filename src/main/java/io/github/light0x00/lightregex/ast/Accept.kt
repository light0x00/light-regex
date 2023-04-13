package io.github.light0x00.lightregex.ast

class Accept(val matchToEnd: Boolean) : AST() {
    override fun toString(): String {
        return ""
    }

    override fun copy(): Accept {
        return Accept(matchToEnd)
    }
}