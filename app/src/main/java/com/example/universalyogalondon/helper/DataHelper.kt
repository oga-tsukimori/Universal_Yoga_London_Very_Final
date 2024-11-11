package com.example.universalyogalondon.helper

import com.example.universalyogalondon.R
import com.example.universalyogalondon.model.DataVO
import com.example.universalyogalondon.model.YogaClassVO

class DataHelper {

    companion object {

        fun getSaveList() : MutableList<DataVO> {
            val dataList : MutableList<DataVO> = mutableListOf()
            val itemList : MutableList<YogaClassVO> = mutableListOf()
            itemList.add(YogaClassVO(R.drawable.ic_dummy,"Morning Class","Tr. Morry","Nov 2, 2024","12 P.M"))
            itemList.add(YogaClassVO(R.drawable.ic_dummy,"Morning Class","Tr. Morry","Nov 2, 2024","12 P.M"))
            dataList.add(DataVO("Weight Loss November","Nov 2, 2024 - Nov 6, 2024","14 min",itemList))
            dataList.add(DataVO("Weight Loss November","Nov 2, 2024 - Nov 6, 2024","14 min",itemList))
            return dataList
        }
    }

}