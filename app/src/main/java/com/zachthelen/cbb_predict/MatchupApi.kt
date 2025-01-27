package com.zachthelen.cbb_predict

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MatchupApi {
    @GET("matchups")
    suspend fun getMatchups(): List<Matchup>

    @POST("store_matchups")
    suspend fun postMatchups(@Body matchups: List<Matchup>): Response<Unit>

    @GET("projections") // Add this to get from /store_matchups
    suspend fun getProjections(): List<Matchup>
}