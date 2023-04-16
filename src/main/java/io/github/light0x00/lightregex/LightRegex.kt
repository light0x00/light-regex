@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.RegexSupport.Companion.astToNFA
import io.github.light0x00.lightregex.RegexSupport.Companion.nfaToDFA
import io.github.light0x00.lightregex.RegexSupport.Companion.parseAsAST
import io.github.light0x00.lightregex.automata.*
import io.github.light0x00.lightregex.common.*

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

    @Suppress("NOTHING_TO_INLINE")
    inline fun match(input: String, eager: Boolean = false): IntRange? = match(input, 0, eager)

    /**
     * 返回从指定索引位置开始的匹配子串
     *
     * @param input 要匹配的字符串
     * @param fromIndex 匹配起始索引位置
     * @param eager 是否饥渴模式,是则会尝试查找最长匹配子串,否则查找最短匹配子串,比如正则表达式为: a*a,输入: aaa, 最短匹配串为 a,最长匹配串为 aaa
     * @return 如果匹配成功,返回匹配子串所处的索引范围
     */
    fun match(input: String, fromIndex: Int, eager: Boolean = false): IntRange? {
        val seq = matchDFA(input, fromIndex)
        return if (eager)
            seq.lastOrNull()
        else
            seq.firstOrNull()
    }

    /**
     * @return 返回所有匹配的子串
     */
    fun matchAll(input: String): List<IntRange> {
        return matchAll0(input).flatMap { it.toList() }.toList()
    }

    private fun matchAll0(input: String) = sequence {
        if (matchFromStart) {
            yield(matchDFA(input))
        } else {
            val it = utf16Iterator(input)
            while (it.hasNext()) {
                yield(matchDFA(input, it.currentUnitIndex))
                it.next()
            }
        }
    }

    fun matchDFA(input: String, fromUnitIdx: Int = 0) = sequence {
        var curState = D_START_STATE

        var lastUnitIdx = fromUnitIdx
        for ((codePoint, range) in utf16SequenceWithEOF(input, fromUnitIdx)) {
            val tran = dfa.tranTable[curState]?.firstOrNull { it.input.match(codePoint) } ?: break
            if (tran.to.nStates.contains(ACCEPT_STATE)) {
                yield(IntRange(fromUnitIdx, lastUnitIdx))
            }
            curState = tran.to
            lastUnitIdx = range.last
        }

    }

}
