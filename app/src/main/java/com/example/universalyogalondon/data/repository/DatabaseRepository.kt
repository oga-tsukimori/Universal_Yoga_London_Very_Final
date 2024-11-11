package com.example.universalyogalondon.data.repository

import com.example.universalyogalondon.data.db.dao.ClassDao
import com.example.universalyogalondon.data.db.dao.CourseDao
import com.example.universalyogalondon.data.db.entry.ClassEntry
import com.example.universalyogalondon.data.db.entry.CourseEntry
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val dao: ClassDao,
    private val courseDao : CourseDao
){
    suspend fun saveClass(classEntry: ClassEntry) {
        dao.insertClass(classEntry)
    }

    suspend fun getClassList() : Flow<List<ClassEntry>> {
        return dao.getSaveList()
    }

    suspend fun deleteByClassId(id : Int) {
        return dao.deleteByClassId(id)
    }

    suspend fun saveCourse(courseEntry: CourseEntry) {
        courseDao.insertCourse(courseEntry)
    }

    suspend fun getCourseList() : Flow<List<CourseEntry>> {
        return courseDao.getCourseList()
    }

    suspend fun deleteByCourseId(id : Int) {
        return courseDao.clearCourseById(id)
    }
}