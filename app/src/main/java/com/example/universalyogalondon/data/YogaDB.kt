package com.example.universalyogalondon.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.universalyogalondon.data.db.dao.ClassDao
import com.example.universalyogalondon.data.db.dao.CourseDao
import com.example.universalyogalondon.data.db.entry.ClassEntry
import com.example.universalyogalondon.data.db.entry.CourseEntry

@Database(
    entities = [
        ClassEntry::class,
        CourseEntry::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(ItemListConverter::class)
abstract class YogaDB : RoomDatabase() {
    abstract fun classDao(): ClassDao
    abstract fun courseDao(): CourseDao
}
