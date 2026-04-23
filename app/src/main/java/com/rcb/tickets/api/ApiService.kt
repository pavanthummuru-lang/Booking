package com.rcb.tickets.api

import com.rcb.tickets.model.EventListResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("ticket/eventlist/O")
    suspend fun getEventList(): Response<EventListResponse>
}
