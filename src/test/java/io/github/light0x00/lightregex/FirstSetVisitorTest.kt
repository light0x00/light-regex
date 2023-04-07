package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.lexcical.GeneralLexer
import io.github.light0x00.lightregex.lexcical.StringReader
import io.github.light0x00.lightregex.syntax.Parser
import io.github.light0x00.lightregex.visitor.FirstSetVisitor
import io.github.light0x00.lightregex.visitor.FollowSetVisitor
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author light
 * @since 2023/4/6
 */
class FirstSetVisitorTest {

    @Test
    fun test1() {
        val ast = Parser(GeneralLexer(StringReader("a|b")))
            .parse()

        val firstSetVisitor = FirstSetVisitor()
        traversePostOrder(ast) { node ->
            firstSetVisitor.visit(node)
        }

        Assertions.assertIterableEquals(
            setOf("a→1", "b→2"),
            ast.firstSet.map(Transition::toString)
        )
    }

    @Test
    fun test2() {
        val ast = Parser(GeneralLexer(StringReader("a*b|c")))
            .parse()

        val firstSetVisitor = FirstSetVisitor()
        traversePostOrder(ast) { node ->
            firstSetVisitor.visit(node)
        }

        Assertions.assertIterableEquals(
            setOf("a→1", "b→2","c→3"),
            ast.firstSet.map(Transition::toString)
        )
    }


    @Test
    fun test() {
        val ast = Parser(GeneralLexer(StringReader("(a|b)*ab")))
            .parse()
        val firstSetVisitor = FirstSetVisitor()
        traversePostOrder(ast) { node ->
            firstSetVisitor.visit(node)
        }
        val followSetVisitor = FollowSetVisitor()
        traversePreOrder(ast) { node ->
            followSetVisitor.visit(node)
        }
//        toAutomata(ast)
    }
//
//    class DFAState(val nfaStates: Set<NFAState>, val nfaTran: Map<String?, Set<NFAState>>) {
//
//        val dfaTran = HashMap<String, DFAState>()
//
//        constructor(nfaStates: Set<AST>) : this(nfaStates, emptyMap())
//
//        override fun equals(other: Any?): Boolean {
//            if (!(other is DFAState)) {
//                return false
//            }
//            if (nfaStates.size != other.nfaStates.size) {
//                return false
//            }
//            for (nfaState in nfaStates) {
//                if (!other.nfaStates.contains(nfaState)) {
//                    return false
//                }
//            }
//            return true
//        }
//
//        override fun hashCode(): Int {
//            return Objects.hash(nfaStates.map { s -> s.hashCode() })
//        }
//    }
//
//    fun toAutomata(ast: RegExpr) {
//        val id = 1;
//
//        val nfaStates = ast.firstSet.map { tran -> tran.to }.toSet()
//        val nfaTran = ast.firstSet.stream().collect(
//            Collectors.groupingBy(
//                Transition::input,
//                Collectors.mapping(Transition::to, Collectors.toSet())
//            )
//        )
//        val startState = DFAState(nfaStates, nfaTran)
//
//        val dfaStates = HashSet<DFAState>()
//        dfaStates.add(startState)
//
//        for ((input, states) in startState.nfaTran.entries) {
//
//            if (dfaStates.contains(DFAState(states))) {
//                startState.dfaTran.set(input as String, startState)
//                continue
//            }
//
//            val nfaTran = states.stream()
//                .map { s -> s.followSet }
//                .flatMap { set -> set.stream() }
//                .collect(
//                    Collectors.groupingBy(
//                        Transition::input,
//                        Collectors.mapping(Transition::to, Collectors.toSet())
//                    )
//                )
//
//
////            DFAState(
////                states, nfaTran
////            )
//
//        }
//
//    }

}