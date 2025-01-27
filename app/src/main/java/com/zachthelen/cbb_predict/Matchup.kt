package com.zachthelen.cbb_predict

data class Matchup(
    val home_team: String,
    val away_team: String,
    val home_score_projection: Double,
    val away_score_projection: Double,
    val spreads: List<Spread>
)

data class Spread(
    val team: String,
    val spread: Double
)