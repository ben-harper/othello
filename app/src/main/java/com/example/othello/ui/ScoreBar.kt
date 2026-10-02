package com.example.othello.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.othello.model.Piece

private val ScoreBarBg = Color(0xFF3E2723)

@Composable
fun ScoreBar(
    blackScore: Int,
    whiteScore: Int,
    currentPlayer: Piece,
    isAiThinking: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ScoreBarBg)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Black score (human)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF222222))
            )
            Text(
                text = "  You: $blackScore",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = if (currentPlayer == Piece.BLACK) FontWeight.Bold else FontWeight.Normal,
            )
        }

        // Turn indicator
        Text(
            text = when {
                isAiThinking -> "AI thinking..."
                currentPlayer == Piece.BLACK -> "Your turn"
                else -> ""
            },
            color = Color(0xFFFFD54F),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )

        // White score (AI)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "AI: $whiteScore  ",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = if (currentPlayer == Piece.WHITE) FontWeight.Bold else FontWeight.Normal,
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5F5F5))
            )
        }
    }
}
