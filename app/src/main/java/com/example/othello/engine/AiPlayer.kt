package com.example.othello.engine

import com.example.othello.model.Piece
import com.example.othello.model.opponent

object AiPlayer {

    private val POSITION_WEIGHTS = arrayOf(
        intArrayOf(100, -25,  10,   5,   5,  10, -25,  100),
        intArrayOf(-25, -50,  -5,  -5,  -5,  -5, -50,  -25),
        intArrayOf( 10,  -5,   5,   1,   1,   5,  -5,   10),
        intArrayOf(  5,  -5,   1,   1,   1,   1,  -5,    5),
        intArrayOf(  5,  -5,   1,   1,   1,   1,  -5,    5),
        intArrayOf( 10,  -5,   5,   1,   1,   5,  -5,   10),
        intArrayOf(-25, -50,  -5,  -5,  -5,  -5, -50,  -25),
        intArrayOf(100, -25,  10,   5,   5,  10, -25,  100)
    )

    fun chooseBestMove(board: List<List<Piece?>>, aiPiece: Piece): Pair<Int, Int>? {
        val validMoves = OthelloEngine.getValidMoves(board, aiPiece)
        if (validMoves.isEmpty()) return null

        var bestScore = Int.MIN_VALUE
        val bestMoves = mutableListOf<Pair<Int, Int>>()

        val opponentPiece = aiPiece.opponent()

        for (move in validMoves) {
            val (row, col) = move
            val simulatedBoard = OthelloEngine.placePiece(board, row, col, aiPiece)

            var aiPositionalScore = 0
            var opponentPositionalScore = 0

            for (r in 0 until 8) {
                for (c in 0 until 8) {
                    val p = simulatedBoard[r][c]
                    if (p == aiPiece) {
                        aiPositionalScore += POSITION_WEIGHTS[r][c]
                    } else if (p == opponentPiece) {
                        opponentPositionalScore += POSITION_WEIGHTS[r][c]
                    }
                }
            }

            val aiMobility = OthelloEngine.getValidMoves(simulatedBoard, aiPiece).size
            val opponentMobility = OthelloEngine.getValidMoves(simulatedBoard, opponentPiece).size

            var score = aiPositionalScore - opponentPositionalScore + 5 * aiMobility - 5 * opponentMobility
            score += POSITION_WEIGHTS[row][col]

            if (score > bestScore) {
                bestScore = score
                bestMoves.clear()
                bestMoves.add(move)
            } else if (score == bestScore) {
                bestMoves.add(move)
            }
        }

        return bestMoves.random()
    }
}
