@file:JvmName("Toolkit")

package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.ast.AST
import java.util.*
import java.util.function.Supplier

fun assertTrue(condition: Boolean, exp: Supplier<Exception>) {
    if (!condition) {
        throw exp.get()
    }
}

fun assertTrue(condition: Boolean, msg: String = "Assertion failed") {
    assertTrue(condition) { throw LightRegexException(msg) }
}

fun <T : ITraversable<T>> traversePreOrder(ast: T, visit: (ast: T) -> Unit) {
    val stack = Stack<T>()
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

fun <T : ITraversable<T>> traversePostOrder(ast: T, visit: (ast: T) -> Unit) {
    val stack = Stack<T>()
    stack.push(ast)
    var lastVisited: T? = null
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

fun astToPlantUML(ast: AST): String {
    val source = StringBuilder()
    var i = 0
    source.appendLine("title ${ast.toString()}")
    traversePreOrder(ast) { node ->
        node.id = i++

        val stateId = if (node.state != null) "<${node.state!!.id}>" else ""
        source.appendLine("""state ${node.id} as "${stateId}${node.javaClass.simpleName}"""")
//        if (node.children.isEmpty())
        source.appendLine("${node.id}: $node")
        source.appendLine("${node.id}: ")
        source.appendLine("${node.id}: first={ ${node.firstSet.joinToString(separator = " , ")} }")
        source.appendLine("${node.id}: follow={ ${node.followSet.joinToString(separator = " , ")} }")
//        source.appendLine("${node.id}: indirect-follow={ ${node.indirectFollowSet.joinToString(separator = " , ")} }")
    }

    traversePostOrder(ast) { node ->
        for (child in node.children) {
            source.appendLine("${node.id}-down->${child.id}")
        }
    }
    return source.toString()
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
    for ((s, trans) in nfa.tranTable) {
        for (t in trans) {
            sb.appendLine("${if (s == START_STATE) "[*]" else s.id}-down->${if (t.to == ACCEPT_STATE) "[*]" else t.to.id} : ${t.input}")
        }
    }
    return sb.toString()
}