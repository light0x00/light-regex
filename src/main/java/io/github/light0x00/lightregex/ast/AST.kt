package io.github.light0x00.lightregex.ast

import io.github.light0x00.lightregex.ITraversable
import io.github.light0x00.lightregex.NFAState
import io.github.light0x00.lightregex.Transition
import io.github.light0x00.lightregex.common.Cloneable

abstract class AST(override vararg val children: AST) : ITraversable<AST>, Cloneable<AST> {
    var id = -1;
    var state: NFAState? = null
    var firstSet: Set<Transition> = emptySet()
    var followSet: Set<Transition> = emptySet()
//    var indirectFollowSet: Set<Transition> = emptySet()
    var nullable: Boolean = false
}