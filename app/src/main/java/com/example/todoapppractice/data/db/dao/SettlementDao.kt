package com.example.todoapppractice.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.todoapppractice.data.db.entity.SettlementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettlementDao {

    @Insert
    suspend fun insert(settlement: SettlementEntity): Long

    @Insert
    suspend fun insertAll(settlements: List<SettlementEntity>)

    @Query("DELETE FROM settlements WHERE settlement_id = :settlementId")
    suspend fun delete(settlementId: Long)

    @Query("SELECT * FROM settlements ORDER BY created_at DESC")
    fun getAllFlow(): Flow<List<SettlementEntity>>

    @Query("SELECT * FROM settlements ORDER BY created_at DESC")
    suspend fun getAll(): List<SettlementEntity>

    /**
     * Net settlement impact per user.
     * from_user loses money, to_user gains money.
     */
    @Query(
        """
        SELECT from_user_id AS userId, SUM(amount) AS totalAmount
        FROM settlements
        GROUP BY from_user_id
    """
    )
    suspend fun getTotalSettledFromPerUser(): List<UserAmountTuple>

    @Query(
        """
        SELECT to_user_id AS userId, SUM(amount) AS totalAmount
        FROM settlements
        GROUP BY to_user_id
    """
    )
    suspend fun getTotalSettledToPerUser(): List<UserAmountTuple>

    @Query(
        """
        SELECT from_user_id AS userId, SUM(amount) AS totalAmount
        FROM settlements
        GROUP BY from_user_id
    """
    )
    fun getTotalSettledFromPerUserFlow(): Flow<List<UserAmountTuple>>

    @Query(
        """
        SELECT to_user_id AS userId, SUM(amount) AS totalAmount
        FROM settlements
        GROUP BY to_user_id
    """
    )
    fun getTotalSettledToPerUserFlow(): Flow<List<UserAmountTuple>>

}
