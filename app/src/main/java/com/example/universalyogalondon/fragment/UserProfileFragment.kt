package com.example.universalyogalondon.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.universalyogalondon.adapter.CourseAdapter
import com.example.universalyogalondon.adapter.CourseListAdapter
import com.example.universalyogalondon.adapter.UsersAdapter
import com.example.universalyogalondon.data.db.entry.CourseEntry
import com.example.universalyogalondon.databinding.FragmentUserProfileBinding
import com.example.universalyogalondon.helper.DataHelper
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private var usersAdapter : UsersAdapter? = null
    var courseList : MutableList<CourseEntry>? = null
    private var userList: MutableList<Map<String, String>>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCoursesWithUsers()

        setUpUserList()
    }

    private fun setUpUserList(){
        usersAdapter = UsersAdapter(mutableListOf())
//        courseListAdapter?.updateData(dataList)
        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = usersAdapter
        }
    }

    fun getCoursesWithUsers() {
        // Initialize Firestore
        val db = Firebase.firestore

        // Reference the "courses" collection
        val coursesRef = db.collection("courses")


        // Query documents containing the "users" field
        coursesRef.get()
            .addOnSuccessListener { documents ->
                val courseData = documents.documents
                println("courseData ${courseData.size}")
                usersAdapter?.setUsers(courseData)

//                for (document in documents) {
//                    // Check if "users" field exists
//                    if (document.contains("users")) {
//                        val courseId = document.id
//                        val courseData = document.data
//                        val user = document.get("users") // Retrieves the "users" list
//                        val courseName = document.get("courseName")
//
//                        println("courseData $courseData")
////                        userList.add(user)
////
//                        usersAdapter?.setUsers(courseData as MutableList<Map<String, String>>)
//                    }
////                    userList.addAll()
//                }

            }
            .addOnFailureListener { exception ->
                println("Error fetching courses: ${exception.message}")
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
