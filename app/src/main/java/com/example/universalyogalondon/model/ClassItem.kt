package com.example.universalyogalondon.model

data class ClassItem(
    val classId : Int = 0,
    val className : String? = "",
    val teacherName : String? = "",
    val date : String = "",
    val time : String = "",
    val image : String = "",
    val timestamp: Long = System.currentTimeMillis()
)
