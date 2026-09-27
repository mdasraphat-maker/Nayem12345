package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversation_messages")
data class ConversationMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String, // "USER" or "MAYA"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val actionExecuted: String? = null,
    val isProactive: Boolean = false
)
