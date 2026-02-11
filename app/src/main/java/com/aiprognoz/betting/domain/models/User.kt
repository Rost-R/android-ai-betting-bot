package com.aiprognoz.betting.domain.models

import java.time.Instant

/**
 * Доменная модель пользователя
 */
data class User(
    val id: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val balance: Int = 0,              // Количество доступных прогнозов
    val isVip: Boolean = false,
    val vipExpiresAt: Instant? = null,
    val createdAt: Instant,
    val lastLoginAt: Instant
) {
    val isVipActive: Boolean
        get() = isVip && vipExpiresAt != null && vipExpiresAt.isAfter(Instant.now())
}
