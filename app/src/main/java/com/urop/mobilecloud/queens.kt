package com.urop.mobilecloud


//import com.urop.common.Chessboard
import com.urop.common.NQueenTask
import java.lang.Integer.max


typealias Chessboard = IntArray
// meta: {remaining, step}


val nqueens: NQueenTask.() -> Unit = {
    solution = recQueens(
        chessboard,
        chessboard.size - remaining, step
    ).toTypedArray()
    remaining = max(remaining - step, 0)
}


fun recQueens(board: Chessboard, c: Int, re: Int): Set<Chessboard> {
    if (re == 0 || c == board.size) {
        return hashSetOf(board)
    }
    val allAns = HashSet<Chessboard>()
    for (new in board.indices) {
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
