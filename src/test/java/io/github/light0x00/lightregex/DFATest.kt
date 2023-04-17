package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.RegexVisualizer.Companion.nfaToPlantUML
import io.github.light0x00.lightregex.automata.*
import org.junit.jupiter.api.Test

/**
 * @author light
 * @since 2023/4/7
 */
class DFATest {

    @Test
    fun test() {

    }

}




fun test1() {
    val nfa = getNFA("(b|[b-d]|[c-h]|[e-f]|.)z")
    val plantUMLSource = nfaToPlantUML(nfa)
    println(plantUMLSource)

    val nTrans = nfa.tranTable.get(START_STATE)!!

    var dTrans: MutableList<DTransition> = mutableListOf()

    for (nTran in nTrans) {
        if (dTrans.isEmpty()) {
            dTrans.add(DTransition(nTran.input, mutableSetOf(nTran.to)))
            continue
        }

        val nInput = nTran.input

        var nRangeFragments = mutableListOf(toRange(nInput))

        var freshDTrans = mutableListOf<DTransition>() //每轮 nTran 与现有 dTran 集合 的差集运算，都会产生新的 dTran 集合

        for (dTran in dTrans) { //遍历现有 dTran 集合
            val dInput = dTran.input

            var dRange = toRange(dInput)

            var diffResults = nRangeFragments.map { Pair(it, diff(it, dRange)) }

            var resultThatHasIntersection2 = diffResults.find {it.second.third.isNotEmpty() }


            //完全不存在交集
            if (resultThatHasIntersection2 == null) {
                dTrans.add(dTran)
            } else {
                var resultThatHasIntersection =resultThatHasIntersection2.second

                var nRangeFragmentsRemaining =
                    mutableListOf<IIntRange>() //nTran 在与现有 dTran 集合的每个 dTran 的差集运算过程中，剩余 Range （即差集） 都会产生变化

                if (resultThatHasIntersection.first.isEmpty()) {
                    if (resultThatHasIntersection.second.isEmpty()) {
                        /*
                        重合的情况， nTran = dTran
                                   ┌──────D─────┐
                          ─────────┼────────────┼───────────
                                   └──────N─────┘
                        此时 nRange 没有剩余
                        */
                        freshDTrans.add(DTransition(dInput, dTran.toNStates.union(setOf(nTran.to))))
                    } else {
                        /*
                        dTran 是 nTran 的真超集， dTran ⊃ nTran
                                   ┌──────D─────┐
                          ─────────┴─┬────────┬─┴───────────
                                     └────N───┘
                        如果:
                        D:  （1～4）->(1,2)
                        N:   (2~3)->3

                        则得到:
                        D1: 1 -> (1,2)
                        D2: 2~3 -> (1,2,3)
                        D3: 4 -> (1,2)

                        此时 nRange 没有剩余
                         */
                        //交集
                        freshDTrans.add(
                            DTransition(
                                toInput(resultThatHasIntersection.third[0]),
                                dTran.toNStates.union(setOf(nTran.to))
                            )
                        )
                        //差集 dTran - nTRan
                        freshDTrans.addAll(
                            resultThatHasIntersection.second.map { r ->
                                DTransition(
                                    toInput(r),
                                    dTran.toNStates.filter { t -> t != nTran.to }.toSet()
                                )
                            }
                        )
                    }
                } else {
                    if (resultThatHasIntersection.second.isEmpty()) {
                        /*
                        nRange 是 dTran 的真超集，nTran ⊃ dTran
                                       ┌───D───┐
                          ───────────┬─┴───────┴─┬────────────
                                     └─────N─────┘
                        */
                        //交集
                        freshDTrans.add(
                            DTransition(
                                toInput(dRange),
                                dTran.toNStates.union(setOf(nTran.to))
                            )
                        )
                        //差集 nTran - dTRan, 记为 nRange 的剩余
                        nRangeFragmentsRemaining.addAll(resultThatHasIntersection.first)
                    } else {
                        /*
                        不相交
                                        ┌────D───┐
                        ──┬─────────┬───┴────────┴──
                          └────N────┘
                         */
                        if (resultThatHasIntersection.third.isEmpty()) {
                            freshDTrans.add(dTran)
                            nRangeFragmentsRemaining.addAll(resultThatHasIntersection.first)
                        }
                        //
                        else {
                            /*
                            相交
                                       ┌────D───┐
                            ────┬──────┴──┬─────┴────────
                                └────N────┘
                            */
                            //nTran - dTran
                            nRangeFragmentsRemaining.addAll(resultThatHasIntersection.first)
                            //dTran - nTran
                            freshDTrans.addAll(resultThatHasIntersection.second.map {
                                DTransition(
                                    toInput(it),
                                    setOf(nTran.to)
                                )
                            })
                            //nTran ∩ dTran
                            freshDTrans.addAll(resultThatHasIntersection.third.map {
                                DTransition(
                                    toInput(it),
                                    dTran.toNStates.union(setOf(nTran.to))
                                )
                            })
                        }

                    }
                }
                nRangeFragments = nRangeFragmentsRemaining
            }
        }
        //nTran 减去 dTran 集合的每一项后的差集
        if (nRangeFragments.isNotEmpty()) {
            freshDTrans.addAll(nRangeFragments.map {
                DTransition(
                    toInput(it), setOf(nTran.to)
                )
            })
        }
        dTrans = freshDTrans
    }
}
