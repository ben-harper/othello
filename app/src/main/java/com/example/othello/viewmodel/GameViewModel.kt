package com.example.othello.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.othello.data.GamePreferences
import com.example.othello.data.GameStats
import com.example.othello.engine.AiPlayer
import com.example.othello.engine.OthelloEngine
import com.example.othello.model.GameSnapshot
import com.example.othello.model.GameState
import com.example.othello.model.GameStatus
import com.example.othello.model.Piece
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = GamePreferences(application)

    var gameState by mutableStateOf(createInitialState())
        private set

    var isAiThinking by mutableStateOf(false)
        private set

    var showValidMoves by mutableStateOf(prefs.showValidMoves)
        private set

    var stats by mutableStateOf(prefs.getStats())
        private set

    private var hasRecordedCurrentGame = false

    private fun createInitialState(): GameState {
        val board = OthelloEngine.createInitialBoard()
        return OthelloEngine.createGameState(board, Piece.BLACK)
    }

    fun setShowValidMovesPreference(enabled: Boolean) {
        showValidMoves = enabled
        prefs.showValidMoves = enabled
    }

    fun resetStats() {
        prefs.resetStats()
        stats = prefs.getStats()
    }

    fun onCellClicked(row: Int, col: Int) {
        val state = gameState
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

        val nextPlayer = Piece.WHITE
        gameState = OthelloEngine.createGameState(
            board = newBoard,
            currentPlayer = nextPlayer,
            moveHistory = newHistory,
            lastMove = Pair(row, col)
        ).copy(flippingPieces = flipped)

        checkAndRecordGameOver(gameState)

        viewModelScope.launch {
            delay(400)
            gameState = gameState.copy(flippingPieces = emptyList())
            advanceGame()
        }
    }

    private suspend fun advanceGame() {
        val state = gameState

        if (state.gameStatus != GameStatus.PLAYING) {
            checkAndRecordGameOver(state)
            return
        }

        if (state.currentPlayer == Piece.WHITE) {
            if (state.validMoves.isEmpty()) {
                val humanMoves = OthelloEngine.getValidMoves(state.board, Piece.BLACK)
                gameState = OthelloEngine.createGameState(
                    state.board, Piece.BLACK, state.moveHistory, state.lastMove
                )
                checkAndRecordGameOver(gameState)
                if (humanMoves.isNotEmpty()) {
                    // Turn passed to human
                }
                return
            }

            isAiThinking = true
            delay(500)

            val aiMove = AiPlayer.chooseBestMove(state.board, Piece.WHITE)
            if (aiMove != null) {
                val (aiRow, aiCol) = aiMove
                val aiSnapshot = GameSnapshot(state.board, state.currentPlayer)
                val aiFlipped = OthelloEngine.getFlippedPieces(state.board, aiRow, aiCol, Piece.WHITE)
                val aiBoard = OthelloEngine.placePiece(state.board, aiRow, aiCol, Piece.WHITE)
                val aiHistory = state.moveHistory + aiSnapshot

                gameState = OthelloEngine.createGameState(
                    board = aiBoard,
                    currentPlayer = Piece.BLACK,
                    moveHistory = aiHistory,
                    lastMove = Pair(aiRow, aiCol)
                ).copy(flippingPieces = aiFlipped)

                checkAndRecordGameOver(gameState)

                delay(400)
                gameState = gameState.copy(flippingPieces = emptyList())
                isAiThinking = false

                val updatedState = gameState
                if (updatedState.gameStatus == GameStatus.PLAYING && updatedState.validMoves.isEmpty()) {
                    gameState = OthelloEngine.createGameState(
                        updatedState.board, Piece.WHITE, updatedState.moveHistory, updatedState.lastMove
                    )
                    checkAndRecordGameOver(gameState)
                    advanceGame()
                }
            } else {
                isAiThinking = false
            }
        } else {
            if (state.validMoves.isEmpty()) {
                gameState = OthelloEngine.createGameState(
                    state.board, Piece.WHITE, state.moveHistory, state.lastMove
                )
                checkAndRecordGameOver(gameState)
                advanceGame()
            }
        }
    }

    private fun checkAndRecordGameOver(state: GameState) {
        if (!hasRecordedCurrentGame && state.gameStatus != GameStatus.PLAYING) {
            hasRecordedCurrentGame = true
            val isWin = state.gameStatus == GameStatus.BLACK_WINS
            val isDraw = state.gameStatus == GameStatus.DRAW
            prefs.recordGameResult(isWin, isDraw, state.blackScore)
            stats = prefs.getStats()
        }
    }

    fun undoMove() {
        val state = gameState
        if (state.moveHistory.isEmpty()) return
        if (isAiThinking) return

        var history = state.moveHistory
        var restored: GameSnapshot? = null

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
            hasRecordedCurrentGame = false
        }
    }

    fun newGame() {
        isAiThinking = false
        hasRecordedCurrentGame = false
        gameState = createInitialState()
    }
}
