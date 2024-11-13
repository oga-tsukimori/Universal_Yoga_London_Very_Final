package com.example.universalyogalondon.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.universalyogalondon.databinding.FragmentClassCalendarBinding
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import androidx.appcompat.app.AlertDialog
import java.util.*
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.universalyogalondon.R
import com.example.universalyogalondon.YogaClassStorage
import com.example.universalyogalondon.adapter.SaveListAdapter
import com.example.universalyogalondon.data.db.entry.ClassEntry
import com.example.universalyogalondon.data.db.entry.CourseEntry
import com.example.universalyogalondon.databinding.DefaultDialogViewBinding
import com.example.universalyogalondon.helper.ViewState
import com.example.universalyogalondon.model.viewmodel.DatabaseViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class ClassCalendarFragment : Fragment() {

    private var _binding: FragmentClassCalendarBinding? = null
    private val databaseViewModel : DatabaseViewModel by viewModels()
    private val binding get() = _binding!!
    private val calendar = Calendar.getInstance()
    private var savedListAdapter : SaveListAdapter? = null
    private var dialogBinding: DefaultDialogViewBinding? = null
    private var builder: AlertDialog? = null
    private var courseList : MutableList<CourseEntry> = mutableListOf()
    val nameList = arrayListOf("name1","name2","name3")

    private lateinit var databaseReference: DatabaseReference

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
        /*setupMonthNavigation()
        updateCalendarTitle()
        populateCalendar()*/


        listenToDB()
        setSavedList()

        setUpSearchAction()
    }

    private fun setUpSearchAction() {
        binding.edtSearch.doAfterTextChanged { searchText ->
            val filteredList = courseList.filter {
                it.courseName?.contains(searchText.toString(), ignoreCase = true) ?: false
            }
            savedListAdapter?.updateData(filteredList as MutableList<CourseEntry>)
        }
    }

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


    private fun setSavedList() {
        savedListAdapter = SaveListAdapter(mutableListOf()) {data , root, pos ->
            when(root) {
                "edit" -> {
                    Toast.makeText(requireContext(), "Edit", Toast.LENGTH_SHORT).show()
                }
                "delete" -> {
                    confirmDelete(data.courseId)
                }
                "publish" -> {
                  println("CoursesVO $data")
                    db.collection("testing")
                        .add(data)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(requireContext(), "Success!", Toast.LENGTH_SHORT).show()
                            Log.d("Potato", "DocumentSnapshot added with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Fail!", Toast.LENGTH_SHORT).show()
                            Log.w("Banana", "Error adding document", e)
                        }
//                    uploadToFirebaseDatabase(data)
                    Toast.makeText(requireContext(), "Published!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.rvSaved.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = savedListAdapter
        }
    }

    private fun uploadToFirebaseDatabase (dataEntry: CourseEntry){
        db.collection("courses")
            .add(dataEntry)
            .addOnSuccessListener { documentReference ->
                Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(requireContext(), "Successfully added to Firebase", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
    }
    @SuppressLint("DefaultLocale")
    private fun confirmDelete(courseId : Int) {
        dialogBinding = DefaultDialogViewBinding.inflate(LayoutInflater.from(requireActivity()))
        builder = AlertDialog.Builder(requireContext()).create().apply {
            setView(dialogBinding?.root)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)

            dialogBinding?.tvCancel?.setOnClickListener {
                dismiss()
            }
            dialogBinding?.tvDelete?.setOnClickListener {
                databaseViewModel.deleteByCourseId(courseId)
                dismiss()
            }
            show()
        }
    }
    /*private fun setupMonthNavigation() {
        binding.buttonPreviousMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateCalendarTitle()
            populateCalendar()
        }

        binding.buttonNextMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateCalendarTitle()
            populateCalendar()
        }
    }

    private fun updateCalendarTitle() {
        val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        binding.textViewMonthYear.text = "$monthName ${calendar.get(Calendar.YEAR)}"
    }

    private fun populateCalendar() {
        binding.calendarGrid.removeAllViews()
        addHeaderRow()

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfMonth = getFirstDayOfMonth()

        var dayOfMonth = 1
        for (i in 0 until 6) {
            val row = TableRow(context)
            row.layoutParams = TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)
            for (j in 0 until 7) {
                if (i == 0 && j < firstDayOfMonth || dayOfMonth > daysInMonth) {
                    row.addView(createEmptyCell())
                } else {
                    row.addView(createDayCell(dayOfMonth))
                    dayOfMonth++
                }
            }
            binding.calendarGrid.addView(row)
            if (dayOfMonth > daysInMonth) break
        }
    }

    private fun addHeaderRow() {
        val headerRow = TableRow(context)
        val daysOfWeek = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        for (day in daysOfWeek) {
            headerRow.addView(createTextView(day, isHeader = true))
        }
        binding.calendarGrid.addView(headerRow)
    }*/

    private fun getFirstDayOfMonth(): Int {
        val temp = calendar.clone() as Calendar
        temp.set(Calendar.DAY_OF_MONTH, 1)
        var firstDay = temp.get(Calendar.DAY_OF_WEEK) - 2
        if (firstDay < 0) firstDay = 6
        return firstDay
    }

    private fun createDayCell(day: Int): FrameLayout {
        val cell = FrameLayout(requireContext())
        cell.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
            setMargins(2, 2, 2, 2)
        }
        cell.setPadding(8, 8, 8, 8)

        val dayView = TextView(context).apply {
            text = day.toString()
            setTextColor(ContextCompat.getColor(context, R.color.black))
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.TOP or Gravity.START
            layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        cell.addView(dayView)

        val temp = calendar.clone() as Calendar
        temp.set(Calendar.DAY_OF_MONTH, day)
        val dayOfWeek = temp.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
        val classes = YogaClassStorage.getClassesForDay(dayOfWeek)

        if (classes.isNotEmpty()) {
            // Add pink background for days with classes
            cell.setBackgroundResource(R.drawable.calendar_cell_background)
            
            // Add a small indicator dot
            val indicatorDot = View(context).apply {
                layoutParams = FrameLayout.LayoutParams(8, 8).apply {
                    gravity = Gravity.TOP or Gravity.END
                    setMargins(0, 4, 4, 0)
                }
                background = ContextCompat.getDrawable(context, R.drawable.indicator_dot)
            }
            cell.addView(indicatorDot)
        }

        cell.setOnClickListener {
            showClassesForDay(day)
        }

        return cell
    }

    private fun createEmptyCell(): View {
        return View(context).apply {
            layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
        }
    }

    private fun showClassesForDay(day: Int) {
        val temp = calendar.clone() as Calendar
        temp.set(Calendar.DAY_OF_MONTH, day)
        val dayOfWeek = temp.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        val classes = YogaClassStorage.getClassesForDay(dayOfWeek)

        val message = if (classes.isNotEmpty()) {
            classes.joinToString("\n") { yogaClass ->
                "${yogaClass.time} - ${yogaClass.name} (${yogaClass.duration} min)"
            }
        } else {
            "No classes scheduled for this day"
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Classes for $dayOfWeek, ${temp.get(Calendar.DAY_OF_MONTH)} ${temp.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())}")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun createTextView(text: String, isHeader: Boolean = false): TextView {
        return TextView(context).apply {
            this.text = text
            setPadding(8, 8, 8, 8)
            gravity = Gravity.CENTER
            layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            if (isHeader) {
                setTypeface(null, Typeface.BOLD)
                setTextColor(ContextCompat.getColor(context, R.color.pink_500))
                textSize = 14f
            } else {
                setTextColor(ContextCompat.getColor(context, R.color.black))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
