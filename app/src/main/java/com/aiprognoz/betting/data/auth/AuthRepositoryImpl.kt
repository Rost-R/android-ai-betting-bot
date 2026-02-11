package com.aiprognoz.betting.data.auth

import com.aiprognoz.betting.data.local.dao.UserDao
import com.aiprognoz.betting.data.local.entity.UserEntity
import com.aiprognoz.betting.domain.models.User
import com.aiprognoz.betting.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userDao: UserDao
) : AuthRepository {

    override val currentUser: Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }.flatMapLatest { firebaseUser ->
        if (firebaseUser != null) {
            userDao.getUserById(firebaseUser.uid)
        } else {
            flowOf(null)
        }
    }.flatMapLatest { entity ->
        flowOf(entity?.toDomain())
    }

    override val isAuthenticated: Boolean
        get() = firebaseAuth.currentUser != null

    override suspend fun signInWithEmail(email: String, password: String): Result<User> =
        withContext(Dispatchers.IO) {
            try {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user ?: return@withContext Result.failure(
                    Exception("Authentication failed")
                )
                val user = syncUserToLocal(firebaseUser)
                Timber.d("Signed in user: ${user.id}")
                Result.success(user)
            } catch (e: Exception) {
                Timber.e(e, "Sign in failed")
                Result.failure(e)
            }
        }

    override suspend fun signUpWithEmail(email: String, password: String): Result<User> =
        withContext(Dispatchers.IO) {
            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user ?: return@withContext Result.failure(
                    Exception("Registration failed")
                )
                val user = syncUserToLocal(firebaseUser, isNewUser = true)
                Timber.d("Created user: ${user.id}")
                Result.success(user)
            } catch (e: Exception) {
                Timber.e(e, "Sign up failed")
                Result.failure(e)
            }
        }

    override suspend fun signInAnonymously(): Result<User> = withContext(Dispatchers.IO) {
        try {
            val result = firebaseAuth.signInAnonymously().await()
            val firebaseUser = result.user ?: return@withContext Result.failure(
                Exception("Anonymous auth failed")
            )
            val user = syncUserToLocal(firebaseUser, isNewUser = true)
            Timber.d("Signed in anonymously: ${user.id}")
            Result.success(user)
        } catch (e: Exception) {
            Timber.e(e, "Anonymous sign in failed")
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
        userDao.clearAll()
        Timber.d("User signed out")
    }

    override suspend fun resetPassword(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Timber.d("Password reset email sent to $email")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Password reset failed")
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(displayName: String?, photoUrl: String?): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val user = firebaseAuth.currentUser ?: return@withContext Result.failure(
                    Exception("Not authenticated")
                )
                val profileUpdates = userProfileChangeRequest {
                    displayName?.let { this.displayName = it }
                    photoUrl?.let { this.photoUri = android.net.Uri.parse(it) }
                }
                user.updateProfile(profileUpdates).await()
                syncUserToLocal(user)
                Timber.d("Profile updated")
                Result.success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "Profile update failed")
                Result.failure(e)
            }
        }

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser ?: return null
        return userDao.getCurrentUserSync()?.toDomain()
    }

    override suspend fun updateBalance(amount: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = firebaseAuth.currentUser?.uid ?: return@withContext Result.failure(
                Exception("Not authenticated")
            )
            userDao.addBalance(userId, amount)
            Timber.d("Added $amount to balance")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update balance")
            Result.failure(e)
        }
    }

    override suspend fun deductBalance(amount: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = firebaseAuth.currentUser?.uid ?: return@withContext Result.failure(
                Exception("Not authenticated")
            )
            val rowsAffected = userDao.deductBalance(userId, amount)
            if (rowsAffected > 0) {
                Timber.d("Deducted $amount from balance")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Insufficient balance"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to deduct balance")
            Result.failure(e)
        }
    }

    override suspend fun activateVip(durationDays: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = firebaseAuth.currentUser?.uid ?: return@withContext Result.failure(
                Exception("Not authenticated")
            )
            val expiresAt = System.currentTimeMillis() + (durationDays.toLong() * 24 * 60 * 60 * 1000)
            userDao.updateVipStatus(userId, isVip = true, expiresAt = expiresAt)
            Timber.d("VIP activated for $durationDays days")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to activate VIP")
            Result.failure(e)
        }
    }

    private suspend fun syncUserToLocal(firebaseUser: FirebaseUser, isNewUser: Boolean = false): User {
        val now = System.currentTimeMillis()
        val existingUser = userDao.getCurrentUserSync()

        val userEntity = if (existingUser != null && !isNewUser) {
            existingUser.copy(
                email = firebaseUser.email,
                displayName = firebaseUser.displayName,
                photoUrl = firebaseUser.photoUrl?.toString(),
                lastLoginAt = now
            )
        } else {
            UserEntity(
                id = firebaseUser.uid,
                email = firebaseUser.email,
                displayName = firebaseUser.displayName,
                photoUrl = firebaseUser.photoUrl?.toString(),
                balance = if (isNewUser) 1 else 0, // 1 бесплатный прогноз для новых
                isVip = false,
                vipExpiresAt = null,
                createdAt = now,
                lastLoginAt = now
            )
        }

        userDao.insertUser(userEntity)
        return userEntity.toDomain()
    }
}
