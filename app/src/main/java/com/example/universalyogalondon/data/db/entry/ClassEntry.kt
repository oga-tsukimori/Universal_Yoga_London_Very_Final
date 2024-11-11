package com.example.universalyogalondon.data.db.entry

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "class")
data class ClassEntry(
   @PrimaryKey val classId: Int = 0,
    val className: String? = "",
    val teacherName: String? = "",
    val date: String = "",
    val time: String = "",
    val image: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "classId" to classId,
            "className" to className,
            "teacherName" to teacherName,
            "date" to date,
            "time" to time,
            "image" to image,
            "timestamp" to timestamp
        )
    }
}


