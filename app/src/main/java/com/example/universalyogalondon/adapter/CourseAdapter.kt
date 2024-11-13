package com.example.universalyogalondon.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.universalyogalondon.R
import com.example.universalyogalondon.model.YogaClass

class CourseAdapter(private var courses: List<YogaClass>) : RecyclerView.Adapter<CourseAdapter.ViewHolder>() {

    // ViewHolder to hold view references
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val courseName: TextView = view.findViewById(R.id.courseNameTextView)
        val courseInstructor: TextView = view.findViewById(R.id.courseInstructorTextView)
        val courseDuration: TextView = view.findViewById(R.id.courseDurationTextView)
        val editPrice: TextView = view.findViewById(R.id.editPrice)
        val classTypeChipGroup: TextView = view.findViewById(R.id.classTypeChipGroup)
        val dateRangePicker: TextView = view.findViewById(R.id.datePickerButton)
        val editMaximumCapacity: TextView = view.findViewById(R.id.editMaximumCapacity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val course = courses[position]
        val context = holder.itemView.context

        // Check each required field for missing data
        when {
            course.name.isBlank() -> {
                Toast.makeText(context, "Course name is required", Toast.LENGTH_SHORT).show()
                return
            }
            course.instructor.isBlank() -> {
                Toast.makeText(context, "Instructor name is required", Toast.LENGTH_SHORT).show()
                return
            }
            course.duration == null -> {
                Toast.makeText(context, "Duration is required", Toast.LENGTH_SHORT).show()
                return
            }
            course.price == null -> {
                Toast.makeText(context, "Price is required", Toast.LENGTH_SHORT).show()
                return
            }
            course.chipGroup == null -> {
                Toast.makeText(context, "Type of class is required", Toast.LENGTH_SHORT).show()
                return
            }
            course.dayOfWeek.isBlank() -> {
                Toast.makeText(context, "Day of the class is required", Toast.LENGTH_SHORT).show()
                return
            }
            course.maximumCapacity == null -> {
                Toast.makeText(context, "Maximum Capacity is required", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Display fields if all are valid
        holder.courseName.text = course.name
        holder.courseInstructor.text = "Instructor: ${course.instructor}"
        holder.courseDuration.text = "Duration: ${course.duration} minutes"
        holder.editPrice.text = "Price: ${course.price}"
        holder.classTypeChipGroup.text = "Type: ${course.chipGroup}"
        holder.dateRangePicker.text = "Day: ${course.dayOfWeek}"
        holder.editMaximumCapacity.text = "Maximum Capacity: ${course.maximumCapacity}"
    }

    // Returns the number of courses in the list
    override fun getItemCount(): Int = courses.size

    // Updates the list of courses and refreshes the RecyclerView
    fun updateCourses(newCourses: List<YogaClass>) {
        courses = newCourses
        notifyDataSetChanged()
    }
}
