package com.aiprognoz.betting.domain.models

import java.time.Instant

/**
 * Доменная модель матча
 */
data class Match(
    val id: String,
    val sportKey: String,
    val sportTitle: String,
    val commenceTime: Instant,
    val homeTeam: String,
    val awayTeam: String,
    val homeOdds: Double?,
    val drawOdds: Double?,
    val awayOdds: Double?,
    val bookmaker: String?,
    val isLive: Boolean = false
) {
    val displayName: String
        get() = "$homeTeam vs $awayTeam"

    val hasOdds: Boolean
        get() = homeOdds != null && awayOdds != null
}
