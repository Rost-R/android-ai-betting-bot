package com.aiprognoz.betting.data.remote.dto

import com.aiprognoz.betting.data.local.entity.MatchEntity
import com.aiprognoz.betting.data.local.entity.SportEntity
import com.google.gson.annotations.SerializedName
import java.time.Instant

/**
 * DTO для спорта из The Odds API
 */
data class SportDto(
    @SerializedName("key") val key: String,
    @SerializedName("group") val group: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("active") val active: Boolean,
    @SerializedName("has_outrights") val hasOutrights: Boolean
) {
    fun toEntity(): SportEntity = SportEntity(
        key = key,
        group = group,
        title = title,
        description = description,
        active = active,
        hasOutrights = hasOutrights
    )
}

/**
 * DTO для события/матча из The Odds API
 */
data class EventDto(
    @SerializedName("id") val id: String,
    @SerializedName("sport_key") val sportKey: String,
    @SerializedName("sport_title") val sportTitle: String,
    @SerializedName("commence_time") val commenceTime: String,
    @SerializedName("home_team") val homeTeam: String,
    @SerializedName("away_team") val awayTeam: String,
    @SerializedName("bookmakers") val bookmakers: List<BookmakerDto>?
) {
    fun toEntity(): MatchEntity {
        val bestBookmaker = bookmakers?.firstOrNull()
        val h2hMarket = bestBookmaker?.markets?.find { it.key == "h2h" }
        val outcomes = h2hMarket?.outcomes

        val homeOdds = outcomes?.find { it.name == homeTeam }?.price
        val awayOdds = outcomes?.find { it.name == awayTeam }?.price
        val drawOdds = outcomes?.find { it.name == "Draw" }?.price

        return MatchEntity(
            id = id,
            sportKey = sportKey,
            sportTitle = sportTitle,
            commenceTime = Instant.parse(commenceTime).toEpochMilli(),
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            homeOdds = homeOdds,
            drawOdds = drawOdds,
            awayOdds = awayOdds,
            bookmaker = bestBookmaker?.key,
            isLive = Instant.parse(commenceTime).isBefore(Instant.now())
        )
    }
}

/**
 * DTO для букмекера
 */
data class BookmakerDto(
    @SerializedName("key") val key: String,
    @SerializedName("title") val title: String,
    @SerializedName("last_update") val lastUpdate: String,
    @SerializedName("markets") val markets: List<MarketDto>
)

/**
 * DTO для рынка ставок
 */
data class MarketDto(
    @SerializedName("key") val key: String,
    @SerializedName("last_update") val lastUpdate: String,
    @SerializedName("outcomes") val outcomes: List<OutcomeDto>
)

/**
 * DTO для исхода
 */
data class OutcomeDto(
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double
)
