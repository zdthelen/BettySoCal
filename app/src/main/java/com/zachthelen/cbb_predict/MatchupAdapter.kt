package com.zachthelen.cbb_predict

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MatchupAdapter(
    private val matchups: List<Matchup>,
    private val onItemClick: (String) -> Unit // Add this listener
) : RecyclerView.Adapter<MatchupAdapter.MatchupViewHolder>() {

    class MatchupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val homeTeam: TextView = view.findViewById(R.id.home_team)
        val awayTeam: TextView = view.findViewById(R.id.away_team)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.matchup_item, parent, false)
        return MatchupViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchupViewHolder, position: Int) {
        val matchup = matchups[position]
        val homeSpread = matchup.spreads.find { it.team == matchup.home_team }
        val awaySpread = matchup.spreads.find { it.team == matchup.away_team }

        holder.homeTeam.text = homeSpread?.let {
            "Home: ${matchup.home_team} (${it.spread}) Proj. Points: ${"%.1f".format(matchup.home_score_projection)}"
        } ?: "Home: ${matchup.home_team} (Spread not available) Proj. Points: ${"%.1f".format(matchup.home_score_projection)}"

        holder.awayTeam.text = awaySpread?.let {
            "Away: ${matchup.away_team} (${it.spread}) Proj. Points: ${"%.1f".format(matchup.away_score_projection)}"
        } ?: "Away: ${matchup.away_team} (Spread not available) Proj. Points: ${"%.1f".format(matchup.away_score_projection)}"

        // Set click listener on the entire item view
        holder.itemView.setOnClickListener {
            // Assuming 'gameId' is part of your Matchup model or you can generate it
            val gameId = "${matchup.home_team}_vs_${matchup.away_team}" // Example ID
            onItemClick(gameId)
        }
    }

    override fun getItemCount(): Int = matchups.size
}