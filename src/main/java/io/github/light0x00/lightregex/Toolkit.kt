@file:JvmName("Toolkit")

package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.syntax.AST
import java.util.*

fun assertTrue(condition: Boolean, msg: String = "Assertion failed") {
    if (!condition) {
        throw LightRegexException(msg)
    }
}

fun isLetter(chr: Char): Boolean {
    return isLetterSmallCase(chr) || isLetterCapitalCase(chr)
}

fun isLetterCapitalCase(chr: Char): Boolean {
    return chr in '\u0041'..'\u005A'
}

fun isLetterSmallCase(chr: Char): Boolean {
    return chr in '\u0061'..'\u007A'
}

fun isDigit(chr: Char): Boolean {
    return chr in '\u0030'..'\u0039'
}

fun unicodeHexToString(unicode: String): String {
    return String(Character.toChars(Integer.parseInt(unicode, 16)))
}

fun traversePreOrder(ast: AST, visit: (ast: AST) -> Unit) {
    val stack = Stack<AST>()
    stack.push(ast)
    while (stack.isNotEmpty()) {
        val node = stack.pop()
        visit(node)
        //倒序装入子节点
        var idx = node.children.size - 1
        while (idx >= 0) {
            stack.push(node.children[idx--])
        }
    }
}

fun traversePostOrder(ast: AST, visit: (ast: AST) -> Unit) {
    val stack = Stack<AST>()
    stack.push(ast)
    var lastVisited: AST? = null
    while (stack.isNotEmpty()) {
        var lookahead = stack.peek()
        while (true) {
            //子节点已经访问过，说明已经装入，不必重复装入
            if (lookahead.children.contains(lastVisited)) {
                break
            }
            //倒序装入子节点
            var idx = lookahead.children.size - 1
            while (idx >= 0) {
                stack.push(lookahead.children[idx--])
            }
            //栈顶没有变化，说明已经到了叶节点
            if (lookahead == stack.peek()) {
                break
            }
            lookahead = stack.peek()
        }
        stack.pop()
        visit(lookahead)
        lastVisited = lookahead
    }
}

fun nfaToPlantUML(nfa: NFA): String {
    val sb = StringBuilder()
    sb.appendLine("hide empty description")
    for (s in nfa.states) {
        if (s in listOf(START_STATE, ACCEPT_STATE)) {
            continue
        }
        sb.appendLine("state ${s.id}")
    }
    for ((s, trans) in nfa.transitionTable) {
        for (t in trans) {
            sb.appendLine("${if (s == START_STATE) "[*]" else s.id}-down->${if (t.to == ACCEPT_STATE) "[*]" else t.to.id} : ${t.input}")
        }
    }
    return sb.toString()
}