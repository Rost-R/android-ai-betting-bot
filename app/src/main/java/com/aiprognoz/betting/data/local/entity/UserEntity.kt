package com.aiprognoz.betting.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aiprognoz.betting.domain.models.User
import java.time.Instant

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val balance: Int,
    val isVip: Boolean,
    val vipExpiresAt: Long?,
    val createdAt: Long,
    val lastLoginAt: Long
) {
    fun toDomain(): User = User(
        id = id,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl,
        balance = balance,
        isVip = isVip,
        vipExpiresAt = vipExpiresAt?.let { Instant.ofEpochMilli(it) },
        createdAt = Instant.ofEpochMilli(createdAt),
        lastLoginAt = Instant.ofEpochMilli(lastLoginAt)
    )

    companion object {
        fun fromDomain(user: User): UserEntity = UserEntity(
            id = user.id,
            email = user.email,
            displayName = user.displayName,
            photoUrl = user.photoUrl,
            balance = user.balance,
            isVip = user.isVip,
            vipExpiresAt = user.vipExpiresAt?.toEpochMilli(),
            createdAt = user.createdAt.toEpochMilli(),
            lastLoginAt = user.lastLoginAt.toEpochMilli()
        )
    }
}
