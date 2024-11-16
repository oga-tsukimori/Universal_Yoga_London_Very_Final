package com.example.universalyogalondon.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.universalyogalondon.R

class UsersAdapter(val userList: MutableList<Map<String, String>>) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {
//    private val users = mutableListOf<Map<String, String>>() // List of users (each user as a map)

    // Set users and notify RecyclerView
    fun setUsers(newUsers: MutableList<Map<String, String>>) {
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
        val userName = user["name"] ?: "Unknown"
        val userPhone = user["phone"] ?: "No Phone"
        val userEmail = user["email"] ?: "No Email"

        // Bind data to the view
        holder.tvUserName.text = userName
        holder.tvPhone.text = userPhone
        holder.tvEmail.text = userEmail
    }

    override fun getItemCount(): Int = userList.size

    // ViewHolder class
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
    }
}
