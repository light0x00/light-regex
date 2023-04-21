package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.ast.AST
import io.github.light0x00.lightregex.automata.*
import io.github.light0x00.lightregex.common.traversePostOrder
import io.github.light0x00.lightregex.common.traversePreOrder

class RegexVisualizer {

    companion object {

        @JvmStatic
        fun dfaToPlantUML(dfa: DFA): String {
            val sb = StringBuilder()
            sb.appendLine("hide empty description")
            for (s in dfa.states) {
                if (s == D_START_STATE) {
                    continue
                }
                sb.appendLine(
                    """state ${s.id}: ${s.nStates.joinToString(separator = ",")}"""
                )
            }
            for ((s, trans) in dfa.tranTable) {
                for (t in trans) {
                    if (s == D_START_STATE)
                        sb.append("[*]")
                    else
                        sb.append(s.id)
                    sb.appendLine("""-down-> ${t.to.id} : ${t.input} """)
                }
            }
            sb.setLength(sb.length - 1)
            return sb.toString()
        }

        @JvmStatic
        fun astToPlantUML(ast: AST): String {
            val source = StringBuilder()
            var i = 0
            source.appendLine("hide empty description")
            source.appendLine("title AST of $ast")
            traversePreOrder(ast) { node ->
                node.id = i++

                val stateId = if (node.state != null) "<${node.state!!.id}>" else ""
                source.appendLine("""state ${node.id} as "${stateId}${node.javaClass.simpleName}"""")
                source.appendLine("${node.id}: $node")
                if (node.firstSet.isNotEmpty())
                    source.appendLine("${node.id}: first={ ${node.firstSet.joinToString(separator = " , ")} }")
                if (node.followSet.isNotEmpty())
                    source.appendLine("${node.id}: follow={ ${node.followSet.joinToString(separator = " , ")} }")
            }

            traversePostOrder(ast) { node ->
                for (child in node.children) {
                    source.appendLine("${node.id}-down->${child.id}")
                }
            }
            source.setLength(source.length - 1)
            return source.toString()
        }

        @JvmStatic
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
            sb.setLength(sb.length - 1)
            return sb.toString()
        }
    }
}

