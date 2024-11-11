package com.example.universalyogalondon.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.universalyogalondon.data.db.entry.CourseEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(courseEntry: CourseEntry)

    @Query("SELECT * FROM course ORDER BY timestamp DESC")
    fun getCourseList(): Flow<List<CourseEntry>>

    @Query("DELETE FROM course WHERE courseId = :courseId")
    suspend fun clearCourseById(courseId: Int)

    @Query("DELETE FROM class")
    suspend fun deleteAll()
}