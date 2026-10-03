package com.example.othello.data

import android.content.Context
import android.content.SharedPreferences

data class GameStats(
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val highestScore: Int = 0,
    val totalScore: Int = 0,
    val gamesPlayed: Int = 0
) {
    val averageScore: Double
        get() = if (gamesPlayed > 0) totalScore.toDouble() / gamesPlayed else 0.0
}

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("othello_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SHOW_VALID_MOVES = "show_valid_moves"
        private const val KEY_WINS = "stats_wins"
        private const val KEY_LOSSES = "stats_losses"
        private const val KEY_DRAWS = "stats_draws"
        private const val KEY_HIGHEST_SCORE = "stats_highest_score"
        private const val KEY_TOTAL_SCORE = "stats_total_score"
        private const val KEY_GAMES_PLAYED = "stats_games_played"
    }

    var showValidMoves: Boolean
        get() = prefs.getBoolean(KEY_SHOW_VALID_MOVES, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_VALID_MOVES, value).apply()

    fun getStats(): GameStats {
        return GameStats(
            wins = prefs.getInt(KEY_WINS, 0),
            losses = prefs.getInt(KEY_LOSSES, 0),
            draws = prefs.getInt(KEY_DRAWS, 0),
            highestScore = prefs.getInt(KEY_HIGHEST_SCORE, 0),
            totalScore = prefs.getInt(KEY_TOTAL_SCORE, 0),
            gamesPlayed = prefs.getInt(KEY_GAMES_PLAYED, 0)
        )
    }

    fun recordGameResult(isWin: Boolean, isDraw: Boolean, humanScore: Int) {
        val current = getStats()
        val newWins = if (isWin && !isDraw) current.wins + 1 else current.wins
        val newLosses = if (!isWin && !isDraw) current.losses + 1 else current.losses
        val newDraws = if (isDraw) current.draws + 1 else current.draws
        val newHighest = maxOf(current.highestScore, humanScore)
        val newTotal = current.totalScore + humanScore
        val newPlayed = current.gamesPlayed + 1

        prefs.edit()
            .putInt(KEY_WINS, newWins)
            .putInt(KEY_LOSSES, newLosses)
            .putInt(KEY_DRAWS, newDraws)
            .putInt(KEY_HIGHEST_SCORE, newHighest)
            .putInt(KEY_TOTAL_SCORE, newTotal)
            .putInt(KEY_GAMES_PLAYED, newPlayed)
            .apply()
    }

    fun resetStats() {
        prefs.edit()
            .remove(KEY_WINS)
            .remove(KEY_LOSSES)
            .remove(KEY_DRAWS)
            .remove(KEY_HIGHEST_SCORE)
            .remove(KEY_TOTAL_SCORE)
            .remove(KEY_GAMES_PLAYED)
            .apply()
    }
}
