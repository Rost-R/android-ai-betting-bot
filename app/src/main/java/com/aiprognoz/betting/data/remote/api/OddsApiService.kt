package com.aiprognoz.betting.data.remote.api

import com.aiprognoz.betting.data.remote.dto.EventDto
import com.aiprognoz.betting.data.remote.dto.SportDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API интерфейс для The Odds API
 * Документация: https://the-odds-api.com/liveapi/guides/v4/
 */
interface OddsApiService {

    companion object {
        const val BASE_URL = "https://api.the-odds-api.com/v4/"
    }

    /**
     * Получить список доступных видов спорта
     */
    @GET("sports")
    suspend fun getSports(
        @Query("apiKey") apiKey: String
    ): Response<List<SportDto>>

    /**
     * Получить события для вида спорта с коэффициентами
     */
    @GET("sports/{sport}/odds")
    suspend fun getOdds(
        @Path("sport") sport: String,
        @Query("apiKey") apiKey: String,
        @Query("regions") regions: String = "eu",
        @Query("markets") markets: String = "h2h",
        @Query("oddsFormat") oddsFormat: String = "decimal",
        @Query("dateFormat") dateFormat: String = "iso"
    ): Response<List<EventDto>>

    /**
     * Получить события для вида спорта (без коэффициентов)
     */
    @GET("sports/{sport}/events")
    suspend fun getEvents(
        @Path("sport") sport: String,
        @Query("apiKey") apiKey: String,
        @Query("dateFormat") dateFormat: String = "iso"
    ): Response<List<EventDto>>

    /**
     * Получить коэффициенты для конкретного события
     */
    @GET("sports/{sport}/events/{eventId}/odds")
    suspend fun getEventOdds(
        @Path("sport") sport: String,
        @Path("eventId") eventId: String,
        @Query("apiKey") apiKey: String,
        @Query("regions") regions: String = "eu",
        @Query("markets") markets: String = "h2h",
        @Query("oddsFormat") oddsFormat: String = "decimal",
        @Query("dateFormat") dateFormat: String = "iso"
    ): Response<EventDto>
}
