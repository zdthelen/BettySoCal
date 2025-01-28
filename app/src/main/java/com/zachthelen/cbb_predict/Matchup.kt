package com.zachthelen.cbb_predict

import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.parcelize.Parcelize

@Parcelize
data class Matchup(
    val home_team: String,
    val away_team: String,
    val home_score_projection: Double,
    val away_score_projection: Double,
    val spreads: List<Spread>
) : Parcelable

@Parcelize
data class Spread(
    val team: String,
    val spread: Double
) : Parcelable