package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.automata.DFA
import io.github.light0x00.lightregex.automata.nfa2Dfa
import io.github.light0x00.lightregex.common.dfaToPlantUML
import io.github.light0x00.lightregex.common.nfaToPlantUML

/**
 * @author light
 * @since 2023/4/13
 */

fun main() {
//    val nfa = getNFA("(b|[b-d]|[c-h]|[e-f]|.|.)z")
    val nfa = getNFA("(a|b)*abb")
    val plantUMLSource = nfaToPlantUML(nfa)
    println(plantUMLSource)

    val dfa = DFA()

    nfa2Dfa(nfa)

    println(dfaToPlantUML(dfa))
}

