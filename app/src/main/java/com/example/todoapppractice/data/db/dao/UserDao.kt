package com.example.todoapppractice.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.todoapppractice.data.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE normalized_name = :normalizedName LIMIT 1")
    suspend fun getByNormalizedName(normalizedName: String): UserEntity?

    @Query("SELECT * FROM users WHERE user_id = :userId")
    suspend fun getById(userId: Long): UserEntity?

    @Query("SELECT * FROM users ORDER BY display_name ASC")
    fun getAllFlow(): Flow<List<UserEntity>>

    /**
     * Get or create a user by display name.
     * Returns the user_id (existing or newly inserted).
     */
    @Query("SELECT user_id FROM users WHERE normalized_name = :normalizedName LIMIT 1")
    suspend fun getUserIdByNormalizedName(normalizedName: String): Long?
}
