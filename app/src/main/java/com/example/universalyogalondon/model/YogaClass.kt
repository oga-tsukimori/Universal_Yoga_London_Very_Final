package com.example.universalyogalondon.model

import com.google.android.material.chip.ChipGroup
import com.google.type.DayOfWeek
import com.google.type.TimeOfDay

data class YogaClass(
    val id: String,
    val name: String,
    val instructor: String,
    val time: String,
    val startDate: String,
    val endDate: String,
    val dayOfWeek: String,
    val duration: Int,
    val level: String,
    val description: String,
    val price: Int,
    val chipGroup: ChipGroup,
    val maximumCapacity: Int,
    val timeOfDay: Int

)
