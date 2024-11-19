package com.example.universalyogalondon

import com.example.universalyogalondon.model.YogaClass

//object YogaClassStorage {
//    private val classes = mutableListOf<YogaClass>()
//
//    fun addClass(yogaClass: YogaClass) {
//        classes.add(yogaClass)
//    }
//
//    fun getAllClasses(): List<YogaClass> = classes.toList()
//
//    fun getClassesForDay(dayOfWeek: String): List<YogaClass> {
//        return classes.filter { it.dayOfWeek.equals(dayOfWeek, ignoreCase = true) }
//    }
//}
object YogaClassStorage {
    private val courseClassesMap = mutableMapOf<String, MutableList<YogaClass>>()

    fun addClassToCourse(courseId: String, yogaClass: YogaClass) {
        val classList = courseClassesMap.getOrPut(courseId) { mutableListOf() }
        classList.add(yogaClass)
    }

    fun getAllClassesForCourse(courseId: String): List<YogaClass> {
        return courseClassesMap[courseId]?.toList() ?: emptyList()
    }

    fun getClassesForDayInCourse(courseId: String, dayOfWeek: String): List<YogaClass> {
        return courseClassesMap[courseId]?.filter {
            it.dayOfWeek.equals(dayOfWeek, ignoreCase = true)
        } ?: emptyList()
    }

    fun getAllCourses(): List<String> {
        return courseClassesMap.keys.toList()
    }
}
