package com.example.universalyogalondon.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
//import androidx.compose.ui.semantics.text
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.universalyogalondon.R
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.model.Document

class UsersAdapter(val userList: MutableList<DocumentSnapshot>) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {
//    private val users = mutableListOf<Map<String, String>>() // List of users (each user as a map)

    // Set users and notify RecyclerView
    fun setUsers(newUsers: MutableList<DocumentSnapshot>) {
        userList.clear() // Clear the old data
        userList.addAll(newUsers) // Add the new data
        notifyDataSetChanged() // Notify the adapter to refresh the list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        // Inflate item_user layout for each item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_users, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]

//        val userName = user["name"] ?: "Unknown"
//        val userPhone = user["phone"] ?: "No Phone"
//        val userEmail = user["email"] ?: "No Email"

        val courseName = user["courseName"] ?: "Unknown"
        val userList = user["users"]
//        val tempList = userList[0].get("users")
        println("temp ${user.data?.contains("users")}")
        println("userList $userList")

//        for (document in documents) {
//
//                    if (document.contains("users")) {
//                        val courseId = document.id
//                        val courseData = document.data
//                        val user = document.get("users") // Retrieves the "users" list
//                        val courseName = document.get("courseName")
//
//                        println("courseData $courseData")
//                         usersAdapter?.setUsers(courseData as MutableList<Map<String, String>>)
//                    }
//                }
//
//        // Bind data to the view
        holder.tvUserName.text = courseName.toString()
        val userListAdapter = UserListAdapter(mutableListOf())
        holder.rvUserList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userListAdapter
        }
        userList?.let { userListAdapter.setUsers(it as MutableList<Map<String, String>>) }
    }

//    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
//        val user = userList[position]
//        val courseName = user["courseName"] ?: "Unknown"
//
//        // Extract user data from the DocumentSnapshot
//        val userDataList = mutableListOf<Map<String, String>>()
//        val users = user.get("users") as? List<Map<String, Any>>
//        if (users != null) {
//            for (userMap in users) {
//                val name = userMap["name"] as? String ?: "Unknown"
//                val phone = userMap["phone"] as? String ?: "No Phone"
//                val email = userMap["email"] as? String ?: "No Email"
//                userDataList.add(mapOf("name" to name, "phone" to phone, "email" to email))
//            }
//        }
//
//        // Bind data to the view
//        holder.tvUserName.text = courseName.toString()
//        val userListAdapter = UserListAdapter(mutableListOf())
//        holder.rvUserList.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = userListAdapter
//        }
//        userListAdapter.setUsers(userDataList) // Pass the extracted user data
//    }

    override fun getItemCount(): Int = userList.size

    // ViewHolder class
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserName: TextView = itemView.findViewById(R.id.tvCourseName)
        val rvUserList: RecyclerView = itemView.findViewById(R.id.rvUserList)
    }
}
