package com.example.othello.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.othello.model.GameState
import com.example.othello.model.Piece

// Wood-inspired color palette
private val BoardGreen = Color(0xFF2E7D32)
private val BoardDarkGreen = Color(0xFF1B5E20)
private val WoodLight = Color(0xFFDEB887)
private val WoodMedium = Color(0xFFC19A6B)
private val WoodDark = Color(0xFFA0522D)
private val WoodFrame = Color(0xFF5D3A1A)
private val GridLine = Color(0xFF1A3A1A)
private val ValidMoveColor = Color(0x5500E676)
private val LastMoveGlow = Color(0xCCFFD700)

@Composable
fun BoardCanvas(
    gameState: GameState,
    onCellClicked: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Animation for flipping pieces
    val flipProgress = remember(gameState.flippingPieces) { Animatable(0f) }

    LaunchedEffect(gameState.flippingPieces) {
        if (gameState.flippingPieces.isNotEmpty()) {
            flipProgress.snapTo(0f)
            flipProgress.animateTo(1f, animationSpec = tween(400))
        }
    }

    // Animation for newly placed piece
    val placeScale = remember(gameState.lastMove) { Animatable(0f) }

    LaunchedEffect(gameState.lastMove) {
        if (gameState.lastMove != null) {
            placeScale.snapTo(0f)
            placeScale.animateTo(1f, animationSpec = tween(200))
        } else {
            placeScale.snapTo(1f)
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(gameState.validMoves) {
                detectTapGestures { offset ->
                    val frameThickness = size.width * 0.03f
                    val boardSize = size.width - 2 * frameThickness
                    val cellSize = boardSize / 8f

                    val col = ((offset.x - frameThickness) / cellSize).toInt()
                    val row = ((offset.y - frameThickness) / cellSize).toInt()

                    if (row in 0..7 && col in 0..7) {
                        onCellClicked(row, col)
                    }
                }
            }
    ) {
        val frameThickness = size.width * 0.03f
        val boardSize = size.width - 2 * frameThickness
        val cellSize = boardSize / 8f

        // Draw wood frame
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(WoodMedium, WoodDark, WoodFrame)
            ),
            size = size
        )

        // Draw board background (green felt)
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(BoardGreen, BoardDarkGreen),
                center = Offset(size.width / 2, size.height / 2),
                radius = boardSize * 0.7f
            ),
            topLeft = Offset(frameThickness, frameThickness),
            size = Size(boardSize, boardSize)
        )

        // Draw grid lines
        for (i in 0..8) {
            val x = frameThickness + i * cellSize
            val y = frameThickness + i * cellSize
            // Vertical lines
            drawLine(
                color = GridLine,
                start = Offset(x, frameThickness),
                end = Offset(x, frameThickness + boardSize),
                strokeWidth = 2f
            )
            // Horizontal lines
            drawLine(
                color = GridLine,
                start = Offset(frameThickness, y),
                end = Offset(frameThickness + boardSize, y),
                strokeWidth = 2f
            )
        }

        // Draw small dots at star points (standard Othello board)
        val dotRadius = cellSize * 0.06f
        for (r in listOf(2, 6)) {
            for (c in listOf(2, 6)) {
                drawCircle(
                    color = GridLine,
                    radius = dotRadius,
                    center = Offset(
                        frameThickness + c * cellSize,
                        frameThickness + r * cellSize
                    )
                )
            }
        }

        // Draw valid move indicators
        for ((row, col) in gameState.validMoves) {
            val cx = frameThickness + col * cellSize + cellSize / 2
            val cy = frameThickness + row * cellSize + cellSize / 2
            drawCircle(
                color = ValidMoveColor,
                radius = cellSize * 0.15f,
                center = Offset(cx, cy)
            )
        }

        // Draw last move indicator
        if (gameState.lastMove != null) {
            val (lRow, lCol) = gameState.lastMove
            val cx = frameThickness + lCol * cellSize + cellSize / 2
            val cy = frameThickness + lRow * cellSize + cellSize / 2
            drawCircle(
                color = LastMoveGlow,
                radius = cellSize * 0.42f,
                center = Offset(cx, cy),
                style = Stroke(width = 3f)
            )
        }

        // Draw pieces
        for (row in 0..7) {
            for (col in 0..7) {
                val piece = gameState.board[row][col] ?: continue
                val cx = frameThickness + col * cellSize + cellSize / 2
                val cy = frameThickness + row * cellSize + cellSize / 2
                val baseRadius = cellSize * 0.38f

                val isFlipping = Pair(row, col) in gameState.flippingPieces
                val isLastPlaced = gameState.lastMove == Pair(row, col)

                if (isFlipping) {
                    drawFlippingPiece(cx, cy, baseRadius, piece, flipProgress.value)
                } else if (isLastPlaced) {
                    val scale = placeScale.value
                    drawPiece(cx, cy, baseRadius * scale, piece)
                } else {
                    drawPiece(cx, cy, baseRadius, piece)
                }
            }
        }
    }
}

private fun DrawScope.drawPiece(cx: Float, cy: Float, radius: Float, piece: Piece) {
    if (radius <= 0f) return

    val center = Offset(cx, cy)

    if (piece == Piece.BLACK) {
        // Black piece with subtle 3D gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF444444), Color(0xFF111111)),
                center = Offset(cx - radius * 0.2f, cy - radius * 0.2f),
                radius = radius * 1.5f
            ),
            radius = radius,
            center = center
        )
        // Subtle highlight
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x33FFFFFF), Color.Transparent),
                center = Offset(cx - radius * 0.3f, cy - radius * 0.3f),
                radius = radius * 0.8f
            ),
            radius = radius * 0.6f,
            center = Offset(cx - radius * 0.15f, cy - radius * 0.15f)
        )
    } else {
        // White piece with 3D gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFF5F5F5), Color(0xFFCCCCCC)),
                center = Offset(cx - radius * 0.2f, cy - radius * 0.2f),
                radius = radius * 1.5f
            ),
            radius = radius,
            center = center
        )
        // Edge shadow
        drawCircle(
            color = Color(0x22000000),
            radius = radius,
            center = center,
            style = Stroke(width = 2f)
        )
    }
}

private fun DrawScope.drawFlippingPiece(
    cx: Float,
    cy: Float,
    radius: Float,
    finalPiece: Piece,
    progress: Float,
) {
    // Flip animation: scale X goes 1->0->1, color changes at midpoint
    val scaleX = if (progress < 0.5f) {
        1f - progress * 2f
    } else {
        (progress - 0.5f) * 2f
    }

    val displayPiece = if (progress < 0.5f) finalPiece.let {
        // Before midpoint, show the OLD color (opponent of final)
        if (it == Piece.BLACK) Piece.WHITE else Piece.BLACK
    } else {
        finalPiece
    }

    // Draw with horizontal scaling effect (simulate by adjusting radius)
    val scaledRadius = radius * maxOf(scaleX, 0.05f)
    drawPiece(cx, cy, scaledRadius, displayPiece)
}
