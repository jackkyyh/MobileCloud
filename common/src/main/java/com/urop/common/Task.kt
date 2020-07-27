package com.urop.common


open class Task(@JvmField val id: String = (globalID++).toString()) {

    companion object {
        var globalID = 0
    }
}

open class SortTask(@JvmField val start: Int, @JvmField val end: Int) : Task(encodeID(start, end)) {
    lateinit var arr: IntArray

    @JvmField
    var waitCount = 0

    companion object {
        fun encodeID(start: Int, end: Int): String {
            return "[$start,$end]"
        }
    }
}


class NOPTask : Task()

class QSortTask(start: Int = 0, end: Int = 0) : SortTask(start, end)
class MSortTask(start: Int = 0, end: Int = 0) : SortTask(start, end)

class Message(val msg: String = "") : Task()
//class Profile(val profile: HashMap<String, Int>): Task(){}

typealias Chessboard = IntArray

class NQueenTask(
    val chessboard: Chessboard,
    @JvmField var remaining: Int,
    @JvmField val step: Int
) : Task() {
    lateinit var solution: Array<Chessboard>

    constructor(size: Int = 0, step: Int = 0) : this(IntArray(size), size, step)
}