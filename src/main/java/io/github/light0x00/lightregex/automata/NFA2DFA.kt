package io.github.light0x00.lightregex.automata

import io.github.light0x00.lightregex.common.LightRegexException
import io.github.light0x00.lightregex.common.Unicode
import java.util.*

/**
 * @author light
 * @since 2023/4/14
 */
fun nfa2Dfa(nfa: NFA): DFA {
    val dfa = DFA()

    val stack = Stack<DFAState>()
    stack.push(D_START_STATE)

    var dfaStateId = 1;

    do {
        val dState = stack.pop()
        val nTrans = dState.nStates.flatMap { nfa.tranTable.get(it) ?: emptyList<NTransition>() }
        val dTrans = nTransToDTrans(nTrans)

        for (dTran in dTrans) {
            dTran.to = dfa.statesMap.getOrPut(DFAState(dTran.toNStates)) {
                DFAState(dfaStateId++, dTran.toNStates).also { stack.push(it) }
            }
        }
        dfa.addTrans(dState, dTrans)
    } while (stack.isNotEmpty())
    return dfa
}

fun toRange(i: IInput): IIntRange {
    return when (i) {
        is SingleInput -> {
            FiniteRange(i.char, i.char)
        }
        is RangeInput -> {
            i.range
        }
        is EOFInput -> {
            FiniteRange(Unicode.EOF)
        }
        else -> {
            throw LightRegexException("Unknown input type:" + i.javaClass)
        }
    }
}

fun toInput(r: IIntRange): IInput {
    return when (r) {
        is InfiniteRange, is LeftInfiniteRange, is RightInfiniteRange -> {
            RangeInput(r)
        }
        is FiniteRange -> {
            if (r.start == r.end) {
                SingleInput(r.start)
            } else {
                RangeInput(r)
            }
        }
        else -> {
            throw LightRegexException("Unknown range type:" + r.javaClass)
        }
    }
}

fun nTransToDTrans(nTrans: List<NTransition>): List<DTransition> {
    if (nTrans.isEmpty()) {
        return emptyList()
    }
    var dTrans = listOf(
        nTrans[0].let {
            DTransition(it.input, setOf(it.to))
        })

    for (nTran in nTrans.listIterator(1)) {
        //nTran 的范围碎片 （注：一开始 nTran 只有一个 range，但是后面与 dTran 相交后，差集可能会产生多个范围碎片）
        var nRangeFragments = mutableListOf(toRange(nTran.input))

        val dTransUpdated = mutableListOf<DTransition>()

        for (dTran in dTrans) {
            val dRange = toRange(dTran.input)
            //将 nRange 的剩余片段与 dRange 比较
            val diffResults = nRangeFragments.map { Pair(it, diff(it, dRange)) }
            //找到 nRange 的片段中与 dRange 存在交集的比较结果
            val diffResultWithIntersection = diffResults.find { it.second.third.isNotEmpty() }

            //不存在交集
            if (diffResultWithIntersection == null) {
                dTransUpdated.add(dTran)
            } else {
                val (nFrag, diff) = diffResultWithIntersection
                val (nDiff, dDiff, intersection) = diff
                //由于 nFrag 与 dRange 相交，其剩余范围已经发生变化,所以需要从 nRange 的 frags 中排除掉
                nRangeFragments = nRangeFragments.filter { it != nFrag }.toMutableList()
                //nFrag 的差集保存下来
                nRangeFragments.addAll(nDiff)
                //根据 dRange 的差集创建新的 dTran，to 不变
                dTransUpdated.addAll(dDiff.map {
                    DTransition(toInput(it), dTran.toNStates)
                })
                //nFrag 与 dRange 的交集，to 合并 nTran.to 与 dTran.to
                dTransUpdated.addAll(intersection.map {
                    DTransition(toInput(it), dTran.toNStates.union(setOf(nTran.to)))
                })
            }
        }
        //将 nRange 剩余的不存在交集的片段加入 dTrans
        if (nRangeFragments.isNotEmpty()) {
            dTransUpdated.addAll(nRangeFragments.map {
                DTransition(
                    toInput(it), setOf(nTran.to)
                )
            })
        }
        dTrans = dTransUpdated
    }
    return dTrans
}