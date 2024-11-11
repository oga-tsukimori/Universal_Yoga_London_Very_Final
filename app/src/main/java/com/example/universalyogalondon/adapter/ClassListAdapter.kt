package com.example.universalyogalondon.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.universalyogalondon.data.db.entry.ClassEntry
import com.example.universalyogalondon.databinding.ItemClassSaveBinding
import com.example.universalyogalondon.model.YogaClassVO

class ClassListAdapter (var itemList : MutableList<ClassEntry>) : RecyclerView.Adapter<ClassListAdapter.SavedClassViewHolder>() {

    fun updateData(dataList : MutableList<ClassEntry>) {
        itemList.clear()
        itemList.addAll(dataList)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedClassViewHolder {
        return SavedClassViewHolder(ItemClassSaveBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: SavedClassViewHolder, position: Int) {
        val item = itemList[position]
        holder.bindData(item)
    }

    class SavedClassViewHolder(val binding : ItemClassSaveBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item : ClassEntry) {
            binding.tvClassName.text = item.className
            binding.tvTeacherName.text = item.teacherName
            binding.tvTime.text = item.date.plus(",").plus(item.time)
            binding.imvClass.setImageURI(Uri.parse(item.image))
        }
    }
}