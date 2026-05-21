package com.example.todoapppractice.data.repository

import com.example.todoapppractice.data.db.dao.UserDao
import com.example.todoapppractice.data.db.entity.UserEntity

class UserRepository(private val userDao: UserDao) {

    /**
     * Normalize a display name for deduplication.
     * "  Rahul  Sharma " → "rahul sharma"
     */
    fun normalizeName(displayName: String): String =
        displayName.trim().replace(Regex("\\s+"), " ").lowercase()

    /**
     * Get or create a user by display name.
     * Returns the userId. Uses normalized name for dedup.
     */
    suspend fun getOrCreateUser(displayName: String): Long {
        val normalized = normalizeName(displayName)
        val existingId = userDao.getUserIdByNormalizedName(normalized)
        if (existingId != null) return existingId

        val trimmedDisplay = displayName.trim().replace(Regex("\\s+"), " ")
        return userDao.insert(
            UserEntity(
                displayName = trimmedDisplay,
                normalizedName = normalized
            )
        )
    }

    suspend fun getUserById(userId: Long): UserEntity? = userDao.getById(userId)

    suspend fun getAllUsersById(): Map<Long, UserEntity> =
        userDao.getAll().associateBy { it.userId }

    suspend fun getUserByName(displayName: String): UserEntity? {
        val normalized = normalizeName(displayName)
        return userDao.getByNormalizedName(normalized)
    }
}
