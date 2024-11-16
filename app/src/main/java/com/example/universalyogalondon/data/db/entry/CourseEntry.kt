package com.example.universalyogalondon.data.db.entry

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.universalyogalondon.data.ItemListConverter
import kotlin.time.Duration


@Entity(tableName = "course")
data class CourseEntry(
//    @PrimaryKey(autoGenerate = true)
   @PrimaryKey val courseId : Int = 0,
    val courseName : String? = "",
    val duration: String? = "",
    val capacity : Int = 0,
    val classType : String? = "",
    val description : String = "",
    val timestamp: Long = System.currentTimeMillis(),
    @TypeConverters(ItemListConverter::class)
    val itemList : List<ClassEntry> = emptyList(),
    val pricing : Double = 0.0
)
