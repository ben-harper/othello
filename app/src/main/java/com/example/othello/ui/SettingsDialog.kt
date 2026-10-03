package com.example.othello.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.othello.data.GameStats
import java.util.Locale

private val DialogBg = Color(0xFF3E2723)
private val CardBg = Color(0xFF4E342E)
private val AccentGold = Color(0xFFFFD54F)

@Composable
fun SettingsDialog(
    showValidMoves: Boolean,
    onShowValidMovesChanged: (Boolean) -> Unit,
    stats: GameStats,
    onResetStats: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(DialogBg)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "⚙️ Settings & Stats",
                color = AccentGold,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Gameplay Settings
            Text(
                text = "GAMEPLAY",
                color = AccentGold.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Show Valid Moves",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Highlight legal board positions",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = showValidMoves,
                        onCheckedChange = onShowValidMovesChanged,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AccentGold,
                            checkedTrackColor = Color(0xFF2E7D32)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Score History / Statistics
            Text(
                text = "SCORE HISTORY & STATS",
                color = AccentGold.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Record Row
                    StatRow(label = "Games Played", value = "${stats.gamesPlayed}")
                    StatRow(label = "Record (W - L - D)", value = "${stats.wins} - ${stats.losses} - ${stats.draws}")
                    
                    val winRate = if (stats.gamesPlayed > 0) {
                        (stats.wins.toDouble() / stats.gamesPlayed * 100.0)
                    } else 0.0
                    StatRow(label = "Win Rate", value = String.format(Locale.US, "%.1f%%", winRate))
                    
                    StatRow(label = "Highest Score", value = "${stats.highestScore} discs")
                    StatRow(label = "Average Score", value = String.format(Locale.US, "%.1f discs", stats.averageScore))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reset Stats Button
            if (stats.gamesPlayed > 0) {
                Button(
                    onClick = onResetStats,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF795548).copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset Score History", color = Color(0xFFFFCCBC), fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Close Button
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Done",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
        Text(text = value, color = AccentGold, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}
