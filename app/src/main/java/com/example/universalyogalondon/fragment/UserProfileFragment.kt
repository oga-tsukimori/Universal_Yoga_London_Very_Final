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
    private lateinit var courseAdapter: CourseAdapter
    private var courseListAdapter : CourseListAdapter? = null
    var courseList : MutableList<CourseEntry>? = null

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
        getCourses()

       /* // Initialize RecyclerView
        val recyclerView: RecyclerView = binding.classesRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        courseAdapter = CourseAdapter(emptyList())
        recyclerView.adapter = courseAdapter

        // Fetch courses from database
        val dbHelper = DatabaseHelper(requireContext())
        val courseList = dbHelper.getAllCourses() // Assume this returns a List<Course>
        courseAdapter.updateCourses(courseList)*/

        setSavedList()
    }
    private fun getCourses(){
        val db = Firebase.firestore
        db.collection("courses")
            .get()
            .addOnSuccessListener { result ->
                println("### $result")
                for (document in result) {
                    Log.d("#####", "${document.id} => ${document.data}")
//                    document.data.let { courseListAdapter.updateData(it) }
//                    courseList?.addAll(document.data)
                }


            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }
    }

    private fun setSavedList() {
        courseListAdapter = CourseListAdapter(mutableListOf())
//        courseListAdapter?.updateData(dataList)
        binding.rvCourseList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = courseListAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
