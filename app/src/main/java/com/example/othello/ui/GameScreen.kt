package com.example.othello.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.othello.model.GameStatus
import com.example.othello.viewmodel.GameViewModel

private val BgTop = Color(0xFF5D4037)
private val BgBottom = Color(0xFF3E2723)
private val ButtonColor = Color(0xFF5D4037)
private val AccentGold = Color(0xFFFFD54F)

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel(),
) {
    val gameState = viewModel.gameState
    val isAiThinking = viewModel.isAiThinking
    val showValidMoves = viewModel.showValidMoves
    val stats = viewModel.stats

    var showGameOver by remember(gameState.gameStatus) {
        mutableStateOf(gameState.gameStatus != GameStatus.PLAYING)
    }
    var showSettings by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BgTop, BgBottom)
                )
            )
            .safeDrawingPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Top Header with Title and Settings button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(40.dp)) // Balance the settings icon

            Text(
                text = "Othello",
                color = AccentGold,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
            )

            FilledTonalIconButton(
                onClick = { showSettings = true },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = ButtonColor.copy(alpha = 0.8f),
                    contentColor = AccentGold
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Text(text = "⚙️", fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Score bar
        ScoreBar(
            blackScore = gameState.blackScore,
            whiteScore = gameState.whiteScore,
            currentPlayer = gameState.currentPlayer,
            isAiThinking = isAiThinking,
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Board
        BoardCanvas(
            gameState = gameState,
            showValidMoves = showValidMoves,
            onCellClicked = { row, col -> viewModel.onCellClicked(row, col) },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Control buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        ) {
            Button(
                onClick = { viewModel.undoMove() },
                enabled = gameState.moveHistory.isNotEmpty() && !isAiThinking,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonColor,
                    disabledContainerColor = ButtonColor.copy(alpha = 0.4f),
                ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("↩ Undo", fontSize = 16.sp)
            }

            Button(
                onClick = { viewModel.newGame() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonColor,
                ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("🔄 New Game", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }

    // Game over dialog
    if (showGameOver && gameState.gameStatus != GameStatus.PLAYING) {
        GameOverDialog(
            gameStatus = gameState.gameStatus,
            blackScore = gameState.blackScore,
            whiteScore = gameState.whiteScore,
            onPlayAgain = {
                showGameOver = false
                viewModel.newGame()
            },
            onDismiss = { showGameOver = false },
        )
    }

    // Settings & Stats dialog
    if (showSettings) {
        SettingsDialog(
            showValidMoves = showValidMoves,
            onShowValidMovesChanged = { viewModel.setShowValidMovesPreference(it) },
            stats = stats,
            onResetStats = { viewModel.resetStats() },
            onDismiss = { showSettings = false }
        )
    }
}
