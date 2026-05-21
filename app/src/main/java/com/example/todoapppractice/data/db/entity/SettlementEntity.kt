package com.example.todoapppractice.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "settlements",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["from_user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["to_user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["from_user_id"]),
        Index(value = ["to_user_id"])
    ]
)
data class SettlementEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "settlement_id")
    val settlementId: Long = 0,

    @ColumnInfo(name = "from_user_id")
    val fromUserId: Long,

    @ColumnInfo(name = "to_user_id")
    val toUserId: Long,

    @ColumnInfo(name = "amount")
    val amount: Long,  // stored in paise

    @ColumnInfo(name = "created_at")
    val createdAt: Long  // epoch millis
)
