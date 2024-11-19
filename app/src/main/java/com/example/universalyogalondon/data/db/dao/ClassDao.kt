package com.example.universalyogalondon.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.universalyogalondon.data.db.entry.ClassEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(classEntry: ClassEntry)

    @Query("SELECT * FROM class ORDER BY timestamp DESC")  //ORDER BY id DESC
    fun getSaveList(): Flow<List<ClassEntry>>

    @Query("DELETE FROM class WHERE classId = :classId")
    suspend fun clearClassById(classId: Int)

    @Query("DELETE FROM class WHERE classId=:classId")
    suspend fun deleteByClassId(classId: Int)

    @Query("DELETE FROM class")
    suspend fun deleteAllClass()
}
