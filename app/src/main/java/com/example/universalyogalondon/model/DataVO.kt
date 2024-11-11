package com.example.universalyogalondon.model

data class DataVO(
    val titleName : String,
    val date : String,
    val time : String,
    val itemList : MutableList<YogaClassVO> = mutableListOf()
)

data class YogaClassVO(
    val image : Int ,
    val className : String,
    val teacherName : String,
    val date : String,
    val time : String,
)

data class TeacherVO(
    val name : String
)
