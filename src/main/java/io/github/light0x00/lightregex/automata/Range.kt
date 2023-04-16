package io.github.light0x00.lightregex.automata

import io.github.light0x00.lightregex.common.LightRegexException
import io.github.light0x00.lightregex.common.Unicode
import kotlin.reflect.KClass

/**
 * @author light
 * @since 2023/4/12
 */

interface HowIntRangeToString {
    fun infiniteToString(): String
    fun intToString(i: Int): String
}

val DEFAULT_INT_RANGE_TO_STRING = object : HowIntRangeToString {
    override fun infiniteToString(): String {
        return "∞"
    }

    override fun intToString(i: Int): String {
        return i.toString()
    }
}

val UNICODE_INT_RANGE_TO_STRING = object : HowIntRangeToString {
    override fun infiniteToString(): String {
        return "∞"
    }

    override fun intToString(i: Int): String {
        return Unicode.toString(i)
    }
}

interface IIntRange {
    operator fun contains(i: Int): Boolean;

    fun toString(how: HowIntRangeToString): String
}

data class FiniteRange(val start: Int, val end: Int) : IIntRange {
    constructor(v: Int) : this(v, v)

    override fun contains(i: Int): Boolean {
        return i in start..end
    }

    override fun toString(how: HowIntRangeToString): String {
        return if (start == end) how.intToString(start) else "(${how.intToString(start)}-${how.intToString(end)})"
    }

    override fun toString(): String {
        return toString(DEFAULT_INT_RANGE_TO_STRING)
    }
}

class InfiniteRange : IIntRange {
    override fun contains(i: Int): Boolean {
        return true
    }

    override fun toString(how: HowIntRangeToString): String {
        return how.infiniteToString()
    }

    override fun toString(): String {
        return toString(DEFAULT_INT_RANGE_TO_STRING)
    }
}

data class LeftInfiniteRange(val end: Int) : IIntRange {
    override fun contains(i: Int): Boolean {
        return i <= end
    }

    override fun toString(how: HowIntRangeToString): String {
        return "(∞-${how.intToString(end)})"
    }

    override fun toString(): String {
        return toString(DEFAULT_INT_RANGE_TO_STRING)
    }
}

data class RightInfiniteRange(val start: Int) : IIntRange {
    override fun contains(i: Int): Boolean {
        return i >= start
    }

    override fun toString(how: HowIntRangeToString): String {
        return "(${how.intToString(start)}-∞)"
    }

    override fun toString(): String {
        return toString(DEFAULT_INT_RANGE_TO_STRING)
    }
}

private val RANGE_OPERATION_IMPLEMENTS =
    mapOf<Pair<KClass<out IIntRange>, KClass<out IIntRange>>, (r1: IIntRange, r2: IIntRange) -> Triple<List<IIntRange>, List<IIntRange>, List<IIntRange>>>(
        FiniteRange::class to FiniteRange::class to { r1, r2 ->
            diffFor_Finite_Finite(r1 as FiniteRange, r2 as FiniteRange)
        },
        FiniteRange::class to InfiniteRange::class to { r1, r2 ->
            diffFor_Finite_Infinite(r1 as FiniteRange, r2 as InfiniteRange)
        },
        Pair(FiniteRange::class, LeftInfiniteRange::class) to { r1, r2 ->
            diffFor_LeftInfinite_Finite(r1 as FiniteRange, r2 as LeftInfiniteRange)
        },
        Pair(FiniteRange::class, RightInfiniteRange::class) to { r1, r2 ->
            diffForFiniteRightInfinite(r1 as FiniteRange, r2 as RightInfiniteRange)
        },

        Pair(InfiniteRange::class, FiniteRange::class) to { r1, r2 ->
            swapDiff(diffFor_Finite_Infinite(r2 as FiniteRange, r1 as InfiniteRange))
        },
        Pair(InfiniteRange::class, InfiniteRange::class) to { r1, r2 ->
            diffFor_Infinite_Infinite(r1 as InfiniteRange, r2 as InfiniteRange)
        },
        Pair(InfiniteRange::class, LeftInfiniteRange::class) to { r1, r2 ->
            diffFor_Infinite_LeftInfinite(r1 as InfiniteRange, r2 as LeftInfiniteRange)
        },
        Pair(InfiniteRange::class, RightInfiniteRange::class) to { r1, r2 ->
            diffFor_Infinite_RightInfinite(r1 as InfiniteRange, r2 as RightInfiniteRange)
        },

        Pair(LeftInfiniteRange::class, FiniteRange::class) to { r1, r2 ->
            swapDiff(diffFor_LeftInfinite_Finite(r2 as FiniteRange, r1 as LeftInfiniteRange))
        },
        Pair(LeftInfiniteRange::class, InfiniteRange::class) to { r1, r2 ->
            swapDiff(diffFor_Infinite_LeftInfinite(r2 as InfiniteRange, r1 as LeftInfiniteRange))
        },
        Pair(LeftInfiniteRange::class, LeftInfiniteRange::class) to { r1, r2 ->
            diffFor_LeftInfinite_LeftInfinite(r1 as LeftInfiniteRange, r2 as LeftInfiniteRange)
        },
        Pair(LeftInfiniteRange::class, RightInfiniteRange::class) to { r1, r2 ->
            diffFor_LeftInfinite_RightInfinite(r1 as LeftInfiniteRange, r2 as RightInfiniteRange)
        },

        Pair(RightInfiniteRange::class, FiniteRange::class) to { r1, r2 ->
            swapDiff(diffForFiniteRightInfinite(r2 as FiniteRange, r1 as RightInfiniteRange))
        },
        Pair(RightInfiniteRange::class, InfiniteRange::class) to { r1, r2 ->
            swapDiff(diffFor_Infinite_RightInfinite(r2 as InfiniteRange, r1 as RightInfiniteRange))
        },
        Pair(RightInfiniteRange::class, LeftInfiniteRange::class) to { r1, r2 ->
            swapDiff(diffFor_LeftInfinite_RightInfinite(r2 as LeftInfiniteRange, r1 as RightInfiniteRange))
        },
        Pair(RightInfiniteRange::class, RightInfiniteRange::class) to { r1, r2 ->
            diffForRightInfiniteRightInfinite(r1 as RightInfiniteRange, r2 as RightInfiniteRange)
        },

        )

fun swapDiff(diff: Triple<List<IIntRange>, List<IIntRange>, List<IIntRange>>): Triple<List<IIntRange>, List<IIntRange>, List<IIntRange>> {
    return Triple(diff.second, diff.first, diff.third)
}

/**
 * @return 1：范围1有范围2没有 2：范围2有范围1没有 3：共有
 */
fun diff(range1: IIntRange, range2: IIntRange): Triple<List<IIntRange>, List<IIntRange>, List<IIntRange>> {
    val fn = RANGE_OPERATION_IMPLEMENTS[range1::class to range2::class]
        ?: throw LightRegexException("Unsupported operation:${range1::class} and ${range2::class}")
    return fn(range1, range2)
}

@Suppress("UNUSED_PARAMETER")
private fun diffFor_Infinite_LeftInfinite(
    range1: InfiniteRange,
    range2: LeftInfiniteRange
): Triple<List<IIntRange>, List<IIntRange>, List<IIntRange>> {
    return Triple(
        listOf(RightInfiniteRange(range2.end + 1)),
        emptyList(),
        listOf(range2)
    )
}

@Suppress("UNUSED_PARAMETER")
private fun diffFor_Infinite_RightInfinite(
    range1: InfiniteRange,
    range2: RightInfiniteRange
): Triple<List<IIntRange>, List<IIntRange>, List<IIntRange>> {
    return Triple(
        listOf(LeftInfiniteRange(range2.start - 1)),
        emptyList(),
        listOf(range2)
    )
}


private fun diffForRightInfiniteRightInfinite(
    range1: RightInfiniteRange,
    range2: RightInfiniteRange
): Triple<List<IIntRange>, List<IIntRange>, List<IIntRange>> {
    /*
                 ┌───────range1───────►
   ──────────────┴──┬──────────────────
                    └──────range2─────►
    */
    return if (range1.start < range2.start) {
        Triple(
            listOf(FiniteRange(range1.start, range2.start - 1)),
            emptyList(),
            listOf(range2)
        )
    } else if (range1.start == range2.start) {
        /*
                     ┌───────range1──────►
       ──────────────┼────────────────────
                     └───────range2──────►
        */
        Triple(
            emptyList(),
            emptyList(),
            listOf(range1)

        )
    } else {
        /*
                          ┌─────range1────►
        ──────────────┬───┴────────────────
                      └───────range2──────►
        */
        Triple(
            emptyList(),
            listOf(FiniteRange(range2.start, range1.start - 1)),
            listOf(range1)
        )
    }
}


private fun diffFor_LeftInfinite_RightInfinite(
    range1: LeftInfiniteRange,
    range2: RightInfiniteRange
): Triple<List<IIntRange>, List<IIntRange>, List<IIntRange>> {
    /*
          ◄────range1─────┐
          ────────────────┴───┬────────────────
                              └─────range2────►
    */
    return if (range1.end < range2.start) {
        Triple(
            listOf(range1),
            listOf(range2),
            emptyList()
        )
    } else {
        /*
          ◄──────range1───────┐
          ──────────────┬─────┴────────────────
                        └────────range2───────►
        */
        Triple(
            listOf(LeftInfiniteRange(range2.start - 1)),
            listOf(RightInfiniteRange(range1.end + 1)),
            listOf(FiniteRange(range2.start, range1.end))
        )
    }
}

private fun diffFor_LeftInfinite_LeftInfinite(
    range1: LeftInfiniteRange,
    range2: LeftInfiniteRange
): Triple<List<IIntRange>, List<IIntRange>, List<IIntRange>> {

    if (range1.end < range2.end) {
        /*
          ◄──────range1─────┐
          ──────────────────┴───┬────────────────
          ◄───────range2────────┘
        */
        return Triple(
            emptyList(),
            listOf(FiniteRange(range1.end + 1, range2.end)),
            listOf(range1)
        )
    } else if (range1.end == range2.end) {
        /*
          ◄──────range1─────┐
          ──────────────────┼───────────────────
          ◄──────range2─────┘
        */
        return Triple(
            emptyList(),
            emptyList(),
            listOf(range1)
        )
    } else {
        /*
          ◄───────range1───────┐
          ─────────────────┬───┴───────────────────
          ◄─────range2─────┘
        */
        return Triple(
            listOf(FiniteRange(range2.end + 1, range1.end)),
            emptyList(),
            listOf(range2)
        )
    }
}

@Suppress("UNUSED_PARAMETER")
fun diffFor_Infinite_Infinite(
    range1: InfiniteRange,
    range2: InfiniteRange
): Triple<List<IIntRange>, List<IIntRange>, List<IIntRange>> {
    return Triple(emptyList(), emptyList(), listOf(range1))
}

/**
 * 有限 Range 与 无限 Range 的差异
 * @return 1: 前者有后者没有 2: 后者有前者没有 3: 交集
 */
@Suppress("UNUSED_PARAMETER")
private fun diffFor_Finite_Infinite(
    range1: FiniteRange,
    range2: InfiniteRange
): Triple<List<IIntRange>, List<IIntRange>, List<IIntRange>> {
    return Triple(
        emptyList(),
        listOf(LeftInfiniteRange(range1.start - 1), RightInfiniteRange(range1.end + 1)),
        listOf(range1)
    )
}

/**
 * 有限 Range 与 左无限 Range 的差异
 */
private fun diffFor_LeftInfinite_Finite(
    range1: FiniteRange,
    range2: LeftInfiniteRange
): Triple<List<IIntRange>, List<IIntRange>, List<IIntRange>> {
    /*
     ◄────────────────────┐
      ───────┬────────┬───┴─────────────────
     */
    if (range1.end < range2.end) {
        return Triple(
            emptyList(),
            listOf(FiniteRange(range1.end + 1, range2.end), LeftInfiniteRange(range1.start - 1)),
            listOf(range1)
        )
    }
    /*
     ◄────────────────────┐
      ─────────┬──────────┼─────────────────
     */
    else if (range1.end == range2.end) {
        return Triple(
            emptyList(),
            listOf(LeftInfiniteRange(range1.start - 1)),
            listOf(range1)
        )
    }
    /*
     ◄────────────────────┐
      ────────────────┬───┴────┬─────────────
     */
    else if (range1.start <= range2.end) {
        return Triple(
            listOf(FiniteRange(range2.end + 1, range1.end)),
            listOf(LeftInfiniteRange(range1.start - 1)),
            listOf(FiniteRange(range1.start, range2.end))
        )
    }

    /*
     ◄────────────────────┐
      ────────────────────┴────┬─────────┬────
     */
    else {
        return Triple(
            listOf(range1),
            listOf(range2),
            emptyList()
        )
    }
}

private fun diffForFiniteRightInfinite(
    range1: FiniteRange,
    range2: RightInfiniteRange
): Triple<List<IIntRange>, List<IIntRange>, List<IIntRange>> {
    /*
                 ┌──────────────────►
     ──┬───────┬─┴───────────────────
     */
    if (range1.end < range2.start) {
        return Triple(
            listOf(range1),
            listOf(range2),
            emptyList()
        )
    } else {
        /*
                      ┌──────────────────►
          ────────┬───┴───┬───────────────
                  s1
         */
        if (range1.start < range2.start) {
            return Triple(
                listOf(FiniteRange(range1.start, range2.start - 1)),
                listOf(RightInfiniteRange(range1.end + 1)),
                listOf(FiniteRange(range2.start, range1.end))
            )
        }
        /*
                      ┌──────────────────►
          ────────────┼─────────┬─────────
                     s1
         */
        else if (range1.start == range2.start) {
            return Triple(
                emptyList(),
                listOf(RightInfiniteRange(range1.end + 1)),
                listOf(range1)
            )
        } else {
            /*
                      ┌──────────────────►
          ────────────┴───┬────────┬──────
                         s1
             */
            return Triple(
                emptyList(),
                listOf(FiniteRange(range2.start, range1.start - 1), RightInfiniteRange(range1.end + 1)),
                listOf(range1)
            )
        }
    }
}

private fun diffFor_Finite_Finite(
    range1: FiniteRange,
    range2: FiniteRange
): Triple<List<FiniteRange>, List<FiniteRange>, List<FiniteRange>> {
    if (range1.start < range2.start) {
        /*
        没有交集
                        ┌────r2───┐
        ──┬─────────┬───┴─────────┴──
          └────r1───┘
        */
        if (range1.end < range2.start) {
            return Triple(listOf(range1), listOf(range2), emptyList())
        }
        /*
        相交 互有非空差集
                   ┌───r2───┐
        ────┬──────┴──┬─────┴────────
            └────r1───┘
        */
        else if (range1.end < range2.end) {
            return Triple(
                listOf(FiniteRange(range1.start, range2.start - 1)),
                listOf(FiniteRange(range1.end + 1, range2.end)),
                listOf(FiniteRange(range2.start, range1.end))
            )
        }
        /*
        range1 包含 range2,存在左侧差集空间
                                   ┌────r2───┐
                        ───────┬───┴─────────┼─────
                               └──────r1─────┘
        */
        else if (range1.end == range2.end) {
            return Triple(
                listOf(FiniteRange(range1.start, range2.start - 1)),
                emptyList(),
                listOf(range2),
            )
        }
        //range1 包含 range2,存在左侧、右侧差集空间
        /*
                           ┌────r2───┐
                ───────┬───┴─────────┴──┬──────
                       └──────r1────────┘
           */
        else {
            return Triple(
                listOf(FiniteRange(range1.start, range2.start - 1), FiniteRange(range2.end + 1, range1.end)),
                emptyList(),
                listOf(range2)
            )
        }
    } else if (range1.start == range2.start) {
        /*
        range1 被 range2 包含，存在右侧差集空间
                           ┌───────r2────┐
                ───────────┼──────────┬──┴────
                           └───r1─────┘
        */
        if (range1.end < range2.end) {
            return Triple(
                listOf(),
                listOf(FiniteRange(range1.end + 1, range2.end)),
                listOf(range1)
            )
        }
        /*
        range1 与 range2 重合
                   ┌──────r2─────┐
          ─────────┼─────────────┼───────────
                   └──────r1─────┘
        */
        else if (range1.end == range2.end) {
            return Triple(emptyList(), emptyList(), listOf(range1))
        }
        //
        /*
        range1 包含 range2，存在右侧差集空间
                   ┌──────r2─────┐
          ─────────┼─────────────┴───┬──────────
                   └────────r1───────┘
        */
        else {
            return Triple(
                listOf(FiniteRange(range2.end + 1, range1.end)),
                listOf(),
                listOf(range2)
            )
        }
    } else if (range1.start <= range2.end) {
        /*
        range1 被 range2 包含，存在左侧、右侧差集空间
                   ┌──────r2─────┐
          ─────────┴─┬─────────┬─┴───────────
                     └────r1───┘
        */
        if (range1.end < range2.end) {
            return Triple(
                emptyList(),
                listOf(FiniteRange(range2.start, range1.start - 1), FiniteRange(range1.end + 1, range2.end)),
                listOf(range1)
            )
        }
        /*
        range1 被 range2 包含，存在左侧差集空间
                   ┌──────r2─────┐
          ─────────┴───┬─────────┼───────────
                       └────r1───┘
        */
        else if (range1.end == range2.end) {
            return Triple(
                emptyList(),
                listOf(FiniteRange(range2.start, range1.start - 1)),
                listOf(range1)
            )
        }
        /*
        range1 range2 相交，前者对后者有右侧差集空间，后者对前者有左侧差集空间
                   ┌──────r2─────┐
          ─────────┴───┬─────────┴──┬────────
                       └────r1──────┘
        */
        else {
            return Triple(
                listOf(FiniteRange(range2.end + 1, range1.end)),
                listOf(FiniteRange(range2.start, range1.start - 1)),
                listOf(FiniteRange(range1.start, range2.end))
            )
        }
    }
    //range1 在 range2 的右侧
    /*
     没有交集
       ┌────r2───┐
     ──┴─────────┴────┬─────────┬───
                      └────r1───┘
     */
    else {
        return Triple(
            listOf(range1),
            listOf(range2),
            emptyList()
        )
    }
}
