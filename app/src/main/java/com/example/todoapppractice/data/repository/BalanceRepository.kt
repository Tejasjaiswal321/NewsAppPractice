package com.example.todoapppractice.data.repository

import com.example.todoapppractice.data.db.dao.ExpenseDao
import com.example.todoapppractice.data.db.dao.SettlementDao
import com.example.todoapppractice.data.db.dao.UserAmountTuple
import com.example.todoapppractice.data.db.dao.UserDao
import com.example.todoapppractice.data.db.entity.SettlementEntity
import com.example.todoapppractice.data.db.entity.UserEntity
import com.example.todoapppractice.domain.model.UserBalance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class BalanceRepository(
    private val expenseDao: ExpenseDao,
    private val settlementDao: SettlementDao,
    private val userDao: UserDao
) {

    /**
     * Reactive Flow of all user balances.
     * Combines expense credits/debits + settlement credits/debits.
     *
     * Formula per user:
     *   net = (total_paid) - (total_owed) + (total_settled_to_me) - (total_settled_from_me)
     */
    fun getBalancesFlow(): Flow<List<UserBalance>> = combine(
        expenseDao.getTotalPaidPerUserFlow(),
        expenseDao.getTotalOwedPerUserFlow(),
        settlementDao.getTotalSettledFromPerUserFlow(),
        settlementDao.getTotalSettledToPerUserFlow(),
        userDao.getAllFlow()
    ) { paid, owed, settledFrom, settledTo, users ->
        computeBalances(paid, owed, settledFrom, settledTo, users)
    }

    /**
     * One-shot balance computation.
     */
    suspend fun getBalances(): List<UserBalance> {
        val paid = expenseDao.getTotalPaidPerUser()
        val owed = expenseDao.getTotalOwedPerUser()
        val settledFrom = settlementDao.getTotalSettledFromPerUser()
        val settledTo = settlementDao.getTotalSettledToPerUser()
        val users = mutableMapOf<Long, UserEntity>()

        // Collect all user IDs and fetch their entities
        val allUserIds = (paid.map { it.userId } +
                owed.map { it.userId } +
                settledFrom.map { it.userId } +
                settledTo.map { it.userId }).toSet()

        for (id in allUserIds) {
            userDao.getById(id)?.let { users[id] = it }
        }

        return computeBalancesFromMaps(
            paidMap = paid.associate { it.userId to it.totalAmount },
            owedMap = owed.associate { it.userId to it.totalAmount },
            settledFromMap = settledFrom.associate { it.userId to it.totalAmount },
            settledToMap = settledTo.associate { it.userId to it.totalAmount },
            usersMap = users
        )
    }

    suspend fun insertSettlement(settlement: SettlementEntity): Long =
        settlementDao.insert(settlement)

    suspend fun insertSettlements(settlements: List<SettlementEntity>) =
        settlementDao.insertAll(settlements)

    suspend fun deleteSettlement(settlementId: Long) =
        settlementDao.delete(settlementId)

    suspend fun getAllSettlements(): List<SettlementEntity> =
        settlementDao.getAll()

    fun getAllSettlementsFlow(): Flow<List<SettlementEntity>> =
        settlementDao.getAllFlow()

    // ── Private helpers ──

    private fun computeBalances(
        paid: List<UserAmountTuple>,
        owed: List<UserAmountTuple>,
        settledFrom: List<UserAmountTuple>,
        settledTo: List<UserAmountTuple>,
        users: List<UserEntity>
    ): List<UserBalance> {
        val usersMap = users.associateBy { it.userId }
        return computeBalancesFromMaps(
            paidMap = paid.associate { it.userId to it.totalAmount },
            owedMap = owed.associate { it.userId to it.totalAmount },
            settledFromMap = settledFrom.associate { it.userId to it.totalAmount },
            settledToMap = settledTo.associate { it.userId to it.totalAmount },
            usersMap = usersMap
        )
    }

    private fun computeBalancesFromMaps(
        paidMap: Map<Long, Long>,
        owedMap: Map<Long, Long>,
        settledFromMap: Map<Long, Long>,
        settledToMap: Map<Long, Long>,
        usersMap: Map<Long, UserEntity>
    ): List<UserBalance> {
        val allUserIds = (paidMap.keys + owedMap.keys +
                settledFromMap.keys + settledToMap.keys)

        return allUserIds.mapNotNull { userId ->
            val user = usersMap[userId] ?: return@mapNotNull null

            val totalPaid = paidMap[userId] ?: 0L
            val totalOwed = owedMap[userId] ?: 0L
            val totalSettledFrom = settledFromMap[userId] ?: 0L
            val totalSettledTo = settledToMap[userId] ?: 0L

            // net = what I paid - what I owe + what others settled to me - what I settled to others
            val net = totalPaid - totalOwed + totalSettledTo - totalSettledFrom

            UserBalance(
                userId = userId,
                displayName = user.displayName,
                balancePaise = net
            )
        }.sortedByDescending { it.balancePaise }
    }
}
