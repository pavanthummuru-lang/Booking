package com.rcb.tickets.model

import com.google.gson.annotations.SerializedName

data class EventListResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("result") val result: List<Event>?
)

data class Event(
    @SerializedName("event_Group_Code") val eventGroupCode: Int?,
    @SerializedName("event_Code") val eventCode: Int?,
    @SerializedName("event_Name") val eventName: String?,
    @SerializedName("event_Button_Text") val eventButtonText: String?,
    @SerializedName("event_Display_Date") val eventDisplayDate: String?,
    @SerializedName("event_Date") val eventDate: String?,
    @SerializedName("team_1") val team1: String?,
    @SerializedName("team_2") val team2: String?,
    @SerializedName("team_1_Logo") val team1Logo: String?,
    @SerializedName("team_2_Logo") val team2Logo: String?,
    @SerializedName("venue_Name") val venueName: String?,
    @SerializedName("city_Name") val cityName: String?,
    @SerializedName("event_Price_Range") val eventPriceRange: String?,
    @SerializedName("event_Banner") val eventBanner: String?
)
