package com.example.universalyogalondon.data

import androidx.room.TypeConverter
import com.example.universalyogalondon.data.db.entry.ClassEntry
import com.example.universalyogalondon.model.ClassItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ItemListConverter {
    @TypeConverter
    fun fromItemList(value: List<ClassEntry>): String = Gson().toJson(value)

    @TypeConverter
    fun toItemListList(value: String): List<ClassEntry> {
        val listType = object : TypeToken<List<ClassEntry>>() {}.type
        return Gson().fromJson(value, listType)
    }
}