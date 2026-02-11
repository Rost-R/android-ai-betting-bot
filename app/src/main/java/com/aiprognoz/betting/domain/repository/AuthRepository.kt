package com.aiprognoz.betting.domain.repository

import com.aiprognoz.betting.domain.models.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository интерфейс для авторизации
 */
interface AuthRepository {

    /**
     * Текущий пользователь (Flow)
     */
    val currentUser: Flow<User?>

    /**
     * Проверить авторизован ли пользователь
     */
    val isAuthenticated: Boolean

    /**
     * Войти по email и паролю
     */
    suspend fun signInWithEmail(email: String, password: String): Result<User>

    /**
     * Зарегистрироваться по email и паролю
     */
    suspend fun signUpWithEmail(email: String, password: String): Result<User>

    /**
     * Войти анонимно
     */
    suspend fun signInAnonymously(): Result<User>

    /**
     * Выйти
     */
    suspend fun signOut()

    /**
     * Сбросить пароль
     */
    suspend fun resetPassword(email: String): Result<Unit>

    /**
     * Обновить профиль пользователя
     */
    suspend fun updateProfile(displayName: String?, photoUrl: String?): Result<Unit>

    /**
     * Получить текущего пользователя синхронно
     */
    suspend fun getCurrentUser(): User?

    /**
     * Обновить баланс пользователя
     */
    suspend fun updateBalance(amount: Int): Result<Unit>

    /**
     * Списать баланс
     */
    suspend fun deductBalance(amount: Int): Result<Unit>

    /**
     * Активировать VIP подписку
     */
    suspend fun activateVip(durationDays: Int): Result<Unit>
}
