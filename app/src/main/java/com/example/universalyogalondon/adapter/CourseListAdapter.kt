package com.example.universalyogalondon.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.universalyogalondon.databinding.ItemViewCourseListBinding
import com.example.universalyogalondon.model.DataVO

class CourseListAdapter (var itemList : MutableList<DataVO>) : RecyclerView.Adapter<CourseListAdapter.CourseListViewHolder>() {

    fun updateData(dataList : MutableList<DataVO>) {
        itemList.clear()
        itemList.addAll(dataList)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseListViewHolder {
        return CourseListViewHolder(ItemViewCourseListBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: CourseListViewHolder, position: Int) {
        val item = itemList[position]
        holder.bindData(item)
    }

    class CourseListViewHolder(val binding : ItemViewCourseListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item : DataVO) {
            binding.tvCourseName.text = item.titleName

            //setSavedList(item.itemList)
        }

        /*fun setSavedList(dataList : MutableList<YogaClassVO>) {
            val classListAdapter = ClassListAdapter(mutableListOf())
            classListAdapter.updateData(dataList)
            binding.rvClassList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = classListAdapter
            }
        }*/
    }


}