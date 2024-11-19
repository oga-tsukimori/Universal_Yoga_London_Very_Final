package com.example.universalyogalondon.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.universalyogalondon.R
import com.example.universalyogalondon.adapter.UserListAdapter.UserListViewHolder
import com.google.firebase.firestore.DocumentSnapshot

class UserListAdapter (val userList: MutableList<Map<String, String>>) : RecyclerView.Adapter<UserListViewHolder>() {
//    private val users = mutableListOf<Map<String, String>>() // List of users (each user as a map)

    // Set users and notify RecyclerView
    fun setUsers(newUsers: MutableList<Map<String,String>>) {
        userList.clear() // Clear the old data
        userList.addAll(newUsers) // Add the new data
        notifyDataSetChanged() // Notify the adapter to refresh the list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        // Inflate item_user layout for each item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_item_user, parent, false)
        return UserListViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        val user = userList[position]
        val userName = user["name"] ?: "Unknown"
        val userPhone = user["phone"] ?: "No Phone"
        val userEmail = user["email"] ?: "No Email"
//
//        // Bind data to the view
        holder.tvName.text = userName.toString()
        holder.tvPhone.text = userPhone
        holder.tvEmail.text = userEmail
        println("### $user")
    }

    override fun getItemCount(): Int = userList.size

    // ViewHolder class
    class UserListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
    }
}
