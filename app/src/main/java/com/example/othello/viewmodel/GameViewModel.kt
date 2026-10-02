package com.example.othello.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.othello.engine.AiPlayer
import com.example.othello.engine.OthelloEngine
import com.example.othello.model.GameSnapshot
import com.example.othello.model.GameState
import com.example.othello.model.GameStatus
import com.example.othello.model.Piece
import com.example.othello.model.opponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    var gameState by mutableStateOf(createInitialState())
        private set

    var isAiThinking by mutableStateOf(false)
        private set

    private fun createInitialState(): GameState {
        val board = OthelloEngine.createInitialBoard()
        return OthelloEngine.createGameState(board, Piece.BLACK)
    }

    fun onCellClicked(row: Int, col: Int) {
        val state = gameState
        // Ignore taps when it's not the human's turn, game is over, or AI is thinking
        if (state.currentPlayer != Piece.BLACK) return
        if (state.gameStatus != GameStatus.PLAYING) return
        if (isAiThinking) return
        if (Pair(row, col) !in state.validMoves) return

        makeHumanMove(row, col)
    }

    private fun makeHumanMove(row: Int, col: Int) {
        val state = gameState
        val snapshot = GameSnapshot(state.board, state.currentPlayer)
        val flipped = OthelloEngine.getFlippedPieces(state.board, row, col, Piece.BLACK)
        val newBoard = OthelloEngine.placePiece(state.board, row, col, Piece.BLACK)
        val newHistory = state.moveHistory + snapshot

        // Show the flipping pieces for animation
        val nextPlayer = Piece.WHITE
        gameState = OthelloEngine.createGameState(
            board = newBoard,
            currentPlayer = nextPlayer,
            moveHistory = newHistory,
            lastMove = Pair(row, col)
        ).copy(flippingPieces = flipped)

        // After a brief animation delay, clear the flipping state and trigger AI
        viewModelScope.launch {
            delay(400) // match flip animation duration
            gameState = gameState.copy(flippingPieces = emptyList())

            // Check if AI can move, or if turn passes back, or game is over
            advanceGame()
        }
    }

    private suspend fun advanceGame() {
        val state = gameState

        if (state.gameStatus != GameStatus.PLAYING) return

        if (state.currentPlayer == Piece.WHITE) {
            if (state.validMoves.isEmpty()) {
                // AI has no moves — pass back to human
                val humanMoves = OthelloEngine.getValidMoves(state.board, Piece.BLACK)
                if (humanMoves.isEmpty()) {
                    // Neither can move — game over
                    gameState = OthelloEngine.createGameState(
                        state.board, Piece.BLACK, state.moveHistory, state.lastMove
                    )
                } else {
                    gameState = OthelloEngine.createGameState(
                        state.board, Piece.BLACK, state.moveHistory, state.lastMove
                    )
                }
                return
            }

            // AI's turn
            isAiThinking = true
            delay(500) // Small delay so AI doesn't feel instant

            val aiMove = AiPlayer.chooseBestMove(state.board, Piece.WHITE)
            if (aiMove != null) {
                val (aiRow, aiCol) = aiMove
                val aiSnapshot = GameSnapshot(state.board, state.currentPlayer)
                val aiFlipped = OthelloEngine.getFlippedPieces(state.board, aiRow, aiCol, Piece.WHITE)
                val aiBoard = OthelloEngine.placePiece(state.board, aiRow, aiCol, Piece.WHITE)
                val aiHistory = state.moveHistory + aiSnapshot

                // Show AI's flipping animation
                gameState = OthelloEngine.createGameState(
                    board = aiBoard,
                    currentPlayer = Piece.BLACK,
                    moveHistory = aiHistory,
                    lastMove = Pair(aiRow, aiCol)
                ).copy(flippingPieces = aiFlipped)

                delay(400)
                gameState = gameState.copy(flippingPieces = emptyList())
                isAiThinking = false

                // Check if human can move
                val updatedState = gameState
                if (updatedState.gameStatus == GameStatus.PLAYING && updatedState.validMoves.isEmpty()) {
                    // Human has no moves — pass back to AI
                    gameState = OthelloEngine.createGameState(
                        updatedState.board, Piece.WHITE, updatedState.moveHistory, updatedState.lastMove
                    )
                    advanceGame()
                }
            } else {
                isAiThinking = false
            }
        } else {
            // Human's turn
            if (state.validMoves.isEmpty()) {
                // Human can't move — pass to AI
                gameState = OthelloEngine.createGameState(
                    state.board, Piece.WHITE, state.moveHistory, state.lastMove
                )
                advanceGame()
            }
        }
    }

    fun undoMove() {
        val state = gameState
        if (state.moveHistory.isEmpty()) return
        if (isAiThinking) return

        // Undo back to the last human move state (undo both AI + human)
        // Find the last snapshot where it was the human's (BLACK's) turn
        var history = state.moveHistory
        var restored: GameSnapshot? = null

        // Pop until we find a BLACK turn snapshot (the state before the human moved)
        while (history.isNotEmpty()) {
            val last = history.last()
            history = history.dropLast(1)
            if (last.currentPlayer == Piece.BLACK) {
                restored = last
                break
            }
        }

        if (restored != null) {
            gameState = OthelloEngine.createGameState(
                board = restored.board,
                currentPlayer = restored.currentPlayer,
                moveHistory = history
            )
        }
    }

    fun newGame() {
        isAiThinking = false
        gameState = createInitialState()
    }
}
