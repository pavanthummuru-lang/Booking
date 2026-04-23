package com.rcb.tickets.api

import com.rcb.tickets.model.EventListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {

    @GET("ticket/eventlist/O")
    suspend fun getEventList(
        @Header("Authorization") authorization: String? = null
    ): Response<EventListResponse>
}
