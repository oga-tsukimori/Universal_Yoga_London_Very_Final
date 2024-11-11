package com.example.universalyogalondon.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.universalyogalondon.data.db.entry.ClassEntry
import com.example.universalyogalondon.data.db.entry.CourseEntry
import com.example.universalyogalondon.databinding.ItemViewSavedClassBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


import com.example.universalyogalondon.model.DataVO
import com.example.universalyogalondon.model.YogaClassVO


class SaveListAdapter(
    var itemList: MutableList<CourseEntry>,
    val delegate: (CourseEntry, String, Int) -> Unit
) : RecyclerView.Adapter<SaveListAdapter.SavedViewHolder>() {

    private val firestore: FirebaseFirestore = Firebase.firestore // Initialize Firestore

    fun updateData(filteredList : MutableList<CourseEntry>){
        itemList.clear()
        itemList.addAll(filteredList)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedViewHolder {
        return SavedViewHolder(ItemViewSavedClassBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: SavedViewHolder, position: Int) {
        val item = itemList[position]
        holder.bindData(item, delegate)
    }

    inner class SavedViewHolder(private val binding: ItemViewSavedClassBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: CourseEntry, delegate: (CourseEntry, String, Int) -> Unit) {
            binding.tvTitle.text = item.courseName
            binding.tvDate.text = item.from_to_date
            binding.tvTime.text = item.timestamp.toString()

            binding.rlEdit.setOnClickListener { delegate.invoke(item, "edit", adapterPosition) }
            binding.rlDelete.setOnClickListener { delegate.invoke(item, "delete", adapterPosition) }
            binding.btnPublish.setOnClickListener {
                publishDataToFirebase(item) // Publish data to Firebase on click
                delegate.invoke(item, "publish", adapterPosition)
            }

            item.itemList.let { setSavedList(it.toMutableList()) }
        }

        fun setSavedList(dataList: MutableList<ClassEntry>) {
            val classListAdapter = ClassListAdapter(mutableListOf())
            classListAdapter.updateData(dataList)
            binding.rvClassList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = classListAdapter
            }
        }

        private fun publishDataToFirebase(item: CourseEntry) {
            val documentRef = firestore.collection("courses").document()

            // Convert each ClassEntry to a map and store it in a List<Map<String, Any?>>
            val itemListAsMaps: List<Map<String, Any?>> = item.itemList.map { it.toMap() }

            // Create the Firestore data structure
            val courseData = hashMapOf(
                "courseName" to item.courseName,
                "from_to_date" to item.from_to_date,
                "timestamp" to item.timestamp,
                "itemList" to itemListAsMaps
            )

            // Save to Firestore
            documentRef.set(courseData)
                .addOnSuccessListener {
                    println("Course published to Firebase successfully")
                }
                .addOnFailureListener { e ->
                    println("Failed to publish course: ${e.message}")
                }
        }
        }



    }



