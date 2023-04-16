@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.RegexSupport.Companion.astToNFA
import io.github.light0x00.lightregex.RegexSupport.Companion.nfaToDFA
import io.github.light0x00.lightregex.RegexSupport.Companion.parseAsAST
import io.github.light0x00.lightregex.automata.*
import io.github.light0x00.lightregex.common.*
import java.util.stream.IntStream

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
    }

    fun match(input: String, lastIndex: Int = 0, eager: Boolean = false): IntRange? {
        val seq = matchNFA(input, lastIndex)
        return if (eager)
            seq.lastOrNull()
        else
            seq.firstOrNull()
    }

    fun matchAll(input: String, lastIndex: Int): List<IntRange> {
        return if (matchFromStart) {
            matchNFA(input).toList()
        } else {
            val matches = ArrayList<IntRange>()
            val limit = input.codePointCount(0, input.length)
            for (i in 0..limit) {
                matches.addAll(matchNFA(input, i))
            }
            matches
        }
    }

    private fun matchNFA(inputSequence: String, skip: Int = 0) = sequence {
        var curState = D_START_STATE
        var offset = 0

        val inputStream = IntStream.concat(inputSequence.codePoints().skip(skip.toLong()), IntStream.of(Unicode.EOF))

        for (cp in inputStream) {
            val tran = dfa.tranTable[curState]?.firstOrNull { it.input.match(cp) } ?: break
            if (tran.to.nStates.contains(ACCEPT_STATE)) {
                /* -1 是为了不计入触发进入 Accept 的输入 */
                yield(IntRange(skip, Math.max(skip + offset - 1, 0)))
            }
            curState = tran.to
            offset++
        }
    }

}
