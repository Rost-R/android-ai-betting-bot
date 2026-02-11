package com.aiprognoz.betting.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aiprognoz.betting.domain.models.Sport

@Entity(tableName = "sports")
data class SportEntity(
    @PrimaryKey
    val key: String,
    val group: String,
    val title: String,
    val description: String,
    val active: Boolean,
    val hasOutrights: Boolean
) {
    fun toDomain(): Sport = Sport(
        key = key,
        group = group,
        title = title,
        description = description,
        active = active,
        hasOutrights = hasOutrights
    )

    companion object {
        fun fromDomain(sport: Sport): SportEntity = SportEntity(
            key = sport.key,
            group = sport.group,
            title = sport.title,
            description = sport.description,
            active = sport.active,
            hasOutrights = sport.hasOutrights
        )
    }
}
