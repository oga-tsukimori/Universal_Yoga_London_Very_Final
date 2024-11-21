package com.example.universalyogalondon.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.universalyogalondon.databinding.FragmentClassCalendarBinding
import android.util.Log
import androidx.appcompat.app.AlertDialog
import java.util.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.universalyogalondon.YogaClassStorage
import com.example.universalyogalondon.adapter.SaveListAdapter
import com.example.universalyogalondon.data.db.entry.CourseEntry
import com.example.universalyogalondon.helper.ViewState
import com.example.universalyogalondon.model.YogaClass
import com.example.universalyogalondon.model.viewmodel.DatabaseViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class ClassCalendarFragment : Fragment() {

    private var _binding: FragmentClassCalendarBinding? = null
    private val databaseViewModel: DatabaseViewModel by viewModels()
    private val binding get() = _binding!!
    private var savedListAdapter: SaveListAdapter? = null
    private val courseList: MutableList<CourseEntry> = mutableListOf()
    private var currentCourseId: String? = null

    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenToDB()
        setSavedList()
        setUpSearchAction()
    }

    // Function to create a new course
    private fun createNewCourse(courseName: String, duration: Int, capacity: Int, description: String) {
        val newCourse = CourseEntry(
            courseId = UUID.randomUUID().toString().hashCode(),
            courseName = courseName,
            duration = duration.toString(),
            capacity = capacity,
            classType = "General", // Replace with appropriate value
            description = description,
            time = "10:00 AM", // Replace with appropriate value
            timestamp = System.currentTimeMillis(),
            itemList = emptyList(), // Replace with appropriate value
            pricing = 10.0, // Replace with appropriate value
            dayOfWeek = "Monday", // Replace with appropriate value
            timeOfDay = "" // Replace with appropriate value
        )
        currentCourseId = newCourse.courseId.toString()

        // Save the course to Firestore
        db.collection("courses")
            .add(newCourse)
            .addOnSuccessListener { documentReference ->
                currentCourseId = documentReference.id
                Toast.makeText(requireContext(), "Course created: $courseName", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Error", "Failed to create course", e)
            }
    }

    // Function to add a class to the current course
    private fun addClassToCurrentCourse(className: String, dayOfWeek: String, time: String, duration: Int) {
        if (currentCourseId == null) {
            Toast.makeText(requireContext(), "No course selected!", Toast.LENGTH_SHORT).show()
            return
        }

        val newClass = YogaClass(
            classID = UUID.randomUUID().toString().hashCode(),
            courseID = currentCourseId!!.toInt(),
            name = className,
            dayOfWeek = dayOfWeek,
            time = time,
            duration = duration,
            id = TODO(),
            instructor = TODO(),
            startDate = TODO(),
            endDate = TODO(),
            level = TODO(),
            description = TODO(),
            price = TODO(),
            chipGroup = TODO(),
            maximumCapacity = TODO(),
            timeOfDay = TODO(),
        )

        // Add to local storage
        YogaClassStorage.addClassToCourse(currentCourseId!!, newClass)

        // Save to Firestore
        db.collection("courses/${currentCourseId}/classes")
            .add(newClass)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Class added: $className", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Error", "Failed to add class", e)
            }
    }

    // Listen for updates to the course list
    private fun listenToDB() {
        databaseViewModel.getSaveCourseList()
        databaseViewModel.courseList.observe(viewLifecycleOwner) { viewState ->
            if (viewState is ViewState.Success) {
                viewState.value?.let { courses ->
                    courseList.clear()
                    courseList.addAll(courses)
                    savedListAdapter?.updateData(courseList)
                }
            }
        }
    }

    // Set up the saved course list
    private fun setSavedList() {
        savedListAdapter = SaveListAdapter(mutableListOf()) { data, root, pos ->
            when (root) {
                "edit" -> {
                    Toast.makeText(requireContext(), "Edit Course: ${data.courseName}", Toast.LENGTH_SHORT).show()
                }
                "delete" -> {
                    confirmDelete(data.courseId)
                }
                "publish" -> {
                    publishCourse(data)
                }
            }
        }

        binding.rvSaved.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = savedListAdapter
        }
    }

    // Publish a course to Firestore
    private fun publishCourse(course: CourseEntry) {
        db.collection("courses")
            .add(course)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Course published: ${course.courseName}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Publish failed!", Toast.LENGTH_SHORT).show()
                Log.e("Error", "Failed to publish course", e)
            }
    }

    // Confirm deletion of a course
    private fun confirmDelete(courseId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Course")
            .setMessage("Are you sure you want to delete this course?")
            .setPositiveButton("Delete") { _, _ ->
                databaseViewModel.deleteByCourseId(courseId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Set up search functionality
    private fun setUpSearchAction() {
        binding.edtSearch.doAfterTextChanged { searchText ->
            val filteredList = courseList.filter {
                val query = searchText.toString()
                (it.courseName?.contains(query, ignoreCase = true) ?: false) ||
                        (it.teacherName?.contains(query, ignoreCase = true) ?: false)
            }
            savedListAdapter?.updateData(filteredList.toMutableList())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
