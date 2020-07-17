package com.urop.mobilecloud

import com.urop.common.Task
import com.urop.common.toBArr
import com.urop.common.toIArr
import com.urop.common.toJson

typealias Chessboard = IntArray
//typealias Pos =  Pair<Int, Int>
//typealias boolboard = Array<BooleanArray>

// meta: {remaining, step}
fun queens(t: Task): Task {

    val board = t.data.toIArr()
    val meta = t.meta.toIArr()

    val res = recQueens(board, board.size - meta[0], meta[1]).toTypedArray()
//        repeat(meta[1]){

    val tmp = meta[0] - meta[1]
    meta[0] = if (tmp > 0) tmp else 0
    t.meta = meta.toJson()
    t.data = res.toBArr()
    return t
}

fun recQueens(board: Chessboard, c: Int, re: Int): Set<Chessboard> {
    if (re == 0 || c == board.size) {
        return hashSetOf(board)
    }
    val allAns = HashSet<Chessboard>()
    for (new in 0 until board.size) {
        var ok = true
        for (i in 0 until c) {
            if (board[i] == new || board[i] + i == new + c || board[i] - i == new - c) {
                ok = false
            }
        }
        if (ok) {
            val newBoard = board.clone()
            newBoard[c] = new
            allAns.addAll(recQueens(newBoard, c + 1, re - 1))
        }
    }
    return allAns
}
