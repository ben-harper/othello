package com.example.othello.model

data class GameSnapshot(
    val board: List<List<Piece?>>,
    val currentPlayer: Piece
)

data class GameState(
    val board: List<List<Piece?>>,
    val currentPlayer: Piece,
    val validMoves: Set<Pair<Int, Int>>,
    val blackScore: Int,
    val whiteScore: Int,
    val gameStatus: GameStatus,
    val lastMove: Pair<Int, Int>? = null,
    val flippingPieces: List<Pair<Int, Int>> = emptyList(),
    val moveHistory: List<GameSnapshot> = emptyList()
)
