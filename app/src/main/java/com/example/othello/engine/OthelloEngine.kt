package com.example.othello.engine

import com.example.othello.model.GameSnapshot
import com.example.othello.model.GameState
import com.example.othello.model.GameStatus
import com.example.othello.model.Piece
import com.example.othello.model.opponent

object OthelloEngine {
    private val DIRECTIONS = listOf(
        -1 to -1, -1 to 0, -1 to 1,
         0 to -1,           0 to 1,
         1 to -1,  1 to 0,  1 to 1
    )

    fun createInitialBoard(): List<List<Piece?>> {
        val board = MutableList(8) { MutableList<Piece?>(8) { null } }
        board[3][3] = Piece.WHITE
        board[3][4] = Piece.BLACK
        board[4][3] = Piece.BLACK
        board[4][4] = Piece.WHITE
        return board.map { it.toList() }.toList()
    }

    fun getValidMoves(board: List<List<Piece?>>, piece: Piece): Set<Pair<Int, Int>> {
        val validMoves = mutableSetOf<Pair<Int, Int>>()
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                if (board[row][col] == null && getFlippedPieces(board, row, col, piece).isNotEmpty()) {
                    validMoves.add(row to col)
                }
            }
        }
        return validMoves
    }

    fun getFlippedPieces(board: List<List<Piece?>>, row: Int, col: Int, piece: Piece): List<Pair<Int, Int>> {
        if (board[row][col] != null) return emptyList()

        val opponent = piece.opponent()
        val flippedPieces = mutableListOf<Pair<Int, Int>>()

        for ((dr, dc) in DIRECTIONS) {
            var r = row + dr
            var c = col + dc
            val currentLine = mutableListOf<Pair<Int, Int>>()

            while (r in 0 until 8 && c in 0 until 8 && board[r][c] == opponent) {
                currentLine.add(r to c)
                r += dr
                c += dc
            }

            if (r in 0 until 8 && c in 0 until 8 && board[r][c] == piece) {
                flippedPieces.addAll(currentLine)
            }
        }

        return flippedPieces
    }

    fun placePiece(board: List<List<Piece?>>, row: Int, col: Int, piece: Piece): List<List<Piece?>> {
        val flipped = getFlippedPieces(board, row, col, piece)
        if (flipped.isEmpty()) return board

        val newBoard = board.map { it.toMutableList() }.toMutableList()
        newBoard[row][col] = piece
        for ((r, c) in flipped) {
            newBoard[r][c] = piece
        }
        return newBoard.map { it.toList() }.toList()
    }

    fun getScore(board: List<List<Piece?>>): Pair<Int, Int> {
        var black = 0
        var white = 0
        for (row in board) {
            for (cell in row) {
                if (cell == Piece.BLACK) black++
                else if (cell == Piece.WHITE) white++
            }
        }
        return black to white
    }

    fun isGameOver(board: List<List<Piece?>>): Boolean {
        return getValidMoves(board, Piece.BLACK).isEmpty() && getValidMoves(board, Piece.WHITE).isEmpty()
    }

    fun determineStatus(board: List<List<Piece?>>): GameStatus {
        if (!isGameOver(board)) return GameStatus.PLAYING
        val (black, white) = getScore(board)
        return when {
            black > white -> GameStatus.BLACK_WINS
            white > black -> GameStatus.WHITE_WINS
            else -> GameStatus.DRAW
        }
    }

    fun createGameState(
        board: List<List<Piece?>>,
        currentPlayer: Piece,
        moveHistory: List<GameSnapshot> = emptyList(),
        lastMove: Pair<Int, Int>? = null
    ): GameState {
        val validMoves = getValidMoves(board, currentPlayer)
        val (blackScore, whiteScore) = getScore(board)
        val status = determineStatus(board)

        return GameState(
            board = board,
            currentPlayer = currentPlayer,
            validMoves = validMoves,
            blackScore = blackScore,
            whiteScore = whiteScore,
            gameStatus = status,
            lastMove = lastMove,
            flippingPieces = emptyList(),
            moveHistory = moveHistory
        )
    }
}
