@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")
@file:JvmName("LightRegex")

package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.ast.RegExpr
import io.github.light0x00.lightregex.automata.*
import io.github.light0x00.lightregex.common.*
import io.github.light0x00.lightregex.lexcical.GeneralLexer
import io.github.light0x00.lightregex.lexcical.StringReader
import io.github.light0x00.lightregex.syntax.Parser
import io.github.light0x00.lightregex.visitor.FirstSetVisitor
import io.github.light0x00.lightregex.visitor.FollowSetVisitor
import io.github.light0x00.lightregex.visitor.NFAGenerator
import java.util.stream.IntStream
import java.util.stream.Stream

/**
 * @author light
 * @since 2023/4/8
 */
class LightRegex(regex: String) {
    private val dfa: DFA
    private val matchFromStart: Boolean

    init {
        val ast = parseAsAST(regex)
        val nfa = astToNFA(ast)
        matchFromStart = ast.matchFromStart
        dfa = nfaToDFA(nfa)

        println(nfaToPlantUML(nfa))
        println()
        println(dfaToPlantUML(dfa))
    }

    fun match(input: String): MatchResult {
        val result = MatchResult()
        if (matchFromStart) {
            result.ranges = matchNFA(input)
        } else {
            result.ranges = ArrayList()

            val limit = input.codePointCount(0, input.length)
            for (i in 0..limit) {
                println("输入序列:${input.substring(i)}")
                result.ranges.addAll(matchNFA(input, i))
            }
        }
        return result
    }

    private fun matchNFA(inputSequence: String, skip: Int = 0): MutableList<IntRange> {

        var curState = D_START_STATE
        var offset = 0
        val matches = ArrayList<IntRange>()

        val inputStream = IntStream.concat(inputSequence.codePoints().skip(skip.toLong()), IntStream.of(Unicode.EOF))

        for (cp in inputStream) {
            val tran = dfa.tranTable[curState]?.firstOrNull { it.input.match(cp) } ?: return matches
            println("""输入:${Unicode.toString(cp)} 进入:${tran}""")
            if (tran.to.nStates.contains(ACCEPT_STATE)) {
                /* -1 是为了不计入触发进入 Accept 的输入 */
                matches.add(IntRange(skip, Math.max(skip + offset - 1, 0))) //
                println("Accept")
            }
            curState = tran.to
            offset++
        }

        return matches
    }

}

fun parseAsAST(pattern: String): RegExpr {
    return Parser(GeneralLexer(StringReader(pattern)))
        .parse()
}

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

inline fun nfaToDFA(nfa: NFA): DFA = nfa2Dfa(nfa)