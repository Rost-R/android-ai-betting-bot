package com.aiprognoz.betting.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aiprognoz.betting.domain.models.Match
import java.time.Instant

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey
    val id: String,
    val sportKey: String,
    val sportTitle: String,
    val commenceTime: Long,  // Epoch millis
    val homeTeam: String,
    val awayTeam: String,
    val homeOdds: Double?,
    val drawOdds: Double?,
    val awayOdds: Double?,
    val bookmaker: String?,
    val isLive: Boolean,
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Match = Match(
        id = id,
        sportKey = sportKey,
        sportTitle = sportTitle,
        commenceTime = Instant.ofEpochMilli(commenceTime),
        homeTeam = homeTeam,
        awayTeam = awayTeam,
        homeOdds = homeOdds,
        drawOdds = drawOdds,
        awayOdds = awayOdds,
        bookmaker = bookmaker,
        isLive = isLive
    )

    companion object {
        fun fromDomain(match: Match): MatchEntity = MatchEntity(
            id = match.id,
            sportKey = match.sportKey,
            sportTitle = match.sportTitle,
            commenceTime = match.commenceTime.toEpochMilli(),
            homeTeam = match.homeTeam,
            awayTeam = match.awayTeam,
            homeOdds = match.homeOdds,
            drawOdds = match.drawOdds,
            awayOdds = match.awayOdds,
            bookmaker = match.bookmaker,
            isLive = match.isLive
        )
    }
}
