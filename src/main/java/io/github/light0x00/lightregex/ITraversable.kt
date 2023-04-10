package io.github.light0x00.lightregex

interface ITraversable<T> {
    val children: Array<out T> //List<out T> in Kotlin is equivalent to List<? extends T> in Java.
}