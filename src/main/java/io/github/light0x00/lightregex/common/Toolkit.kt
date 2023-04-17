@file:JvmName("Toolkit")

package io.github.light0x00.lightregex.common

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

fun <T> Array<T>.containsIdenticalElement(obj: Any): Boolean {
    for (i in this) {
        if (i === obj)
            return true
    }
    return false
}

fun <T : ITraversable<T>> traversePostOrder(ast: T, visit: (ast: T) -> Unit) {
    val stack = Stack<T>()
    stack.push(ast)
    var lastVisited: T? = null
    while (stack.isNotEmpty()) {
        var lookahead = stack.peek()
        while (true) {
            //子节点已经访问过，说明已经装入，不必重复装入
            if (lastVisited != null && lookahead.children.containsIdenticalElement(lastVisited)) {
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