package com.example.tvshowerappkotlin.network

import com.example.tvshowerappkotlin.data.TVShowerModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TVShowerWebServices {

    @GET("singlesearch/shows")
    fun getTVShow(
        @Query("q") showName: String?
    ): Call<TVShowerModel>

}

