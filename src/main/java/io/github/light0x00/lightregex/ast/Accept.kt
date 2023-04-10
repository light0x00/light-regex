package io.github.light0x00.lightregex.ast

class Accept : AST() {
    override fun toString(): String {
        return ""
    }

    override fun copy(): Accept {
        return Accept()
    }
}