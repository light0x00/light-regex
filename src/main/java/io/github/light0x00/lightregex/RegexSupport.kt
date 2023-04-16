package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.ast.RegExpr
import io.github.light0x00.lightregex.automata.DFA
import io.github.light0x00.lightregex.automata.NFA
import io.github.light0x00.lightregex.automata.nfa2Dfa
import io.github.light0x00.lightregex.common.traversePostOrder
import io.github.light0x00.lightregex.common.traversePreOrder
import io.github.light0x00.lightregex.lexcical.GeneralLexer
import io.github.light0x00.lightregex.lexcical.StringReader
import io.github.light0x00.lightregex.syntax.Parser
import io.github.light0x00.lightregex.visitor.FirstSetVisitor
import io.github.light0x00.lightregex.visitor.FollowSetVisitor
import io.github.light0x00.lightregex.visitor.NFAGenerator

class RegexSupport {
    companion object {

        @JvmStatic
        fun parseAsAST(pattern: String): RegExpr {
            return Parser(GeneralLexer(StringReader(pattern)))
                .parse()
        }
        @JvmStatic
        fun astToNFA(ast: RegExpr): NFA {
            //First Set
            val firstSetVisitor = FirstSetVisitor()
            traversePostOrder(ast) { node ->
                firstSetVisitor.visit(node)
            }
            //Follow Set
            val followSetVisitor = FollowSetVisitor()
            val nfaGenerator = NFAGenerator(ast.firstSet.toList())
            traversePreOrder(ast) { node ->
                followSetVisitor.visit(node)
                //NFA
                nfaGenerator.visit(node)
            }
            return nfaGenerator.nfa
        }
        @JvmStatic
        @Suppress("NOTHING_TO_INLINE")
        inline fun nfaToDFA(nfa: NFA): DFA = nfa2Dfa(nfa)
    }
}