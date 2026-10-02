package com.example.othello.model

enum class Piece { BLACK, WHITE }
fun Piece.opponent(): Piece = if (this == Piece.BLACK) Piece.WHITE else Piece.BLACK
