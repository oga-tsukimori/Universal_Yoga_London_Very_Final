package com.example.universalyogalondon.adapter

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.universalyogalondon.R
import com.example.universalyogalondon.adapter.UsersAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FetchDataActivity : AppCompatActivity() {
    private lateinit var tvCourseName: TextView
    private lateinit var rvUsers: RecyclerView
    private lateinit var usersAdapter: UsersAdapter

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_user_profile)

        // Initialize UI components
        tvCourseName = findViewById(R.id.tvCourseName)
        rvUsers = findViewById(R.id.rvUsers)

        // Initialize RecyclerView
//        usersAdapter = UsersAdapter()
        rvUsers.layoutManager = LinearLayoutManager(this)
        rvUsers.adapter = usersAdapter

        // Fetch course data from Firestore
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val ref = db.collection("courses").document(userId)
            ref.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Get the course name
                        val courseName = document.getString("coursename")
                        tvCourseName.text = courseName ?: "No Course Name Found"

                        // Get the list of users
                        val users = document.get("users") as? List<Map<String, String>>
                        if (users != null) {
//                            usersAdapter.setUsers(users)
                        } else {
                            Toast.makeText(this, "No users found!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Document not found!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to fetch data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
        }
    }
}
