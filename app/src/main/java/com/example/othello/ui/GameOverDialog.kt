package com.example.othello.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.othello.model.GameStatus

private val DialogBg = Color(0xFF3E2723)
private val GoldAccent = Color(0xFFFFD54F)

@Composable
fun GameOverDialog(
    gameStatus: GameStatus,
    blackScore: Int,
    whiteScore: Int,
    onPlayAgain: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(DialogBg)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = when (gameStatus) {
                    GameStatus.BLACK_WINS -> "🎉 You Win!"
                    GameStatus.WHITE_WINS -> "😔 AI Wins!"
                    GameStatus.DRAW -> "🤝 It's a Draw!"
                    else -> ""
                },
                color = GoldAccent,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Final Score",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$blackScore",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "You (●)",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                    )
                }

                Text(
                    text = "  —  ",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Light,
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$whiteScore",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "AI (○)",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onPlayAgain,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32),
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Play Again",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }
    }
}
