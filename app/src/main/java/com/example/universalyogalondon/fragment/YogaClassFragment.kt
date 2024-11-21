package com.example.universalyogalondon.fragment

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.universalyogalondon.databinding.FragmentYogaCourseBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.material.chip.Chip
import android.text.Editable
import android.text.TextWatcher
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.universalyogalondon.R
import com.example.universalyogalondon.SharedViewModel
import com.example.universalyogalondon.activity.AddCourseActivity
import com.example.universalyogalondon.adapter.ClassAdapter
import com.example.universalyogalondon.data.db.entry.ClassEntry
import com.example.universalyogalondon.data.db.entry.CourseEntry
import com.example.universalyogalondon.helper.DatabaseHelper
import com.example.universalyogalondon.helper.ViewState
import com.example.universalyogalondon.model.ClassInfo
import com.example.universalyogalondon.model.ClassItem
import com.example.universalyogalondon.model.YogaClass
import com.example.universalyogalondon.model.viewmodel.DatabaseViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class YogaClassFragment : Fragment() {

    private var _binding: FragmentYogaCourseBinding? = null
    private val databaseViewModel : DatabaseViewModel by viewModels()
    private val binding get() = _binding!!
    private val calendar = Calendar.getInstance()
    private var startDate: Long = 0
    private var endDate: Long = 0

    private val classes = mutableListOf<ClassEntry>()
    private val classItem = mutableListOf<ClassItem>()
    private lateinit var classAdapter: ClassAdapter
    private lateinit var viewModel: SharedViewModel
    private lateinit var dbHelper: DatabaseHelper
    var mProfileUri : Uri? = null
    var chipType : String = ""

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data!!


                mProfileUri = fileUri
                //updateProfile(fileUri)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Log.d("TAG", "Image Picker Error: ")
            } else {
                Log.d("TAG", "Cancel Image Pick: ")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        // Initialize SQLite Database Helper
        dbHelper = DatabaseHelper(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentYogaCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            // Add listener to save button
            binding.saveButton.setOnClickListener {
                val courseName = binding.edtCourseName.text.toString()
                val courseDuration = binding.edtDuration.text.toString().toIntOrNull()
                val courseCapacity = binding.editMaximumCapacity.text.toString().toIntOrNull()
                val courseDescription = binding.edtDesc.text.toString()

                if (!courseName.isBlank() && courseDuration != null && courseCapacity != null) {
                    createNewCourse(courseName, courseDuration, courseCapacity, courseDescription)
                } else {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }


        setupClassTypeChips()
        setupInputValidation()
        setupAddClassButton()
        setupClassesRecyclerView()

        saveCourseToDB()

        listenToDB()
    }

    private fun createNewCourse(courseName: String, duration: Int, capacity: Int, description: String) {
        val newCourse = CourseEntry(
            courseId = UUID.randomUUID().toString().hashCode(),
            courseName = courseName,
            duration = duration.toString(),
            capacity = capacity,
            classType = "General", // Default value or user input
            description = description,
            time = "10:00 AM", // Default value or user input
            timestamp = System.currentTimeMillis(),
            itemList = emptyList(), // Replace with actual items if applicable
            pricing = 0.0, // Default value or user input
            dayOfWeek = "Monday", // Default value or user input
            timeOfDay = "" // Default value or user input
        )
        var currentCourseId = newCourse.courseId.toString()

        // Save the course to Firestore
        val db = Firebase.firestore

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



    private fun listenToDB() {
        databaseViewModel.getSaveClassList()
        databaseViewModel.classList.observe(viewLifecycleOwner) {
            if (it is ViewState.Success) {
                classes.clear()

                it.value?.let { classes.addAll(it)
                println("*** ${classes.size}")
                }
                it.value?.let { classAdapter.update(it) }
            }
        }
    }


    private fun setupInputValidation() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateInputs()
            }
        }

        binding.edtCourseName.addTextChangedListener(textWatcher)
        binding.edtDuration.addTextChangedListener(textWatcher)
//        binding.dateRangePickerButton.addTextChangedListener(textWatcher)
    }

    private fun validateInputs() {
        val isValid = binding.edtCourseName.text.isNotBlank() &&
                binding.edtDuration.text.isNotBlank() &&
//                binding.dateRangePickerButton.text != "Select Course Duration" &&
                binding.classTypeChipGroup.checkedChipIds.isNotEmpty()

        binding.saveButton.isEnabled = isValid
    }

//    private fun setupDateRangePicker() {
//        binding.dateRangePickerButton.setOnClickListener {
//            val constraintsBuilder = CalendarConstraints.Builder()
//                .setValidator(DateValidatorPointForward.now())
//
//            val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
//                .setTitleText("Select Course Duration")
//                .setSelection(Pair(MaterialDatePicker.todayInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()))
//                .setCalendarConstraints(constraintsBuilder.build())
//                .setTheme(R.style.ThemeMaterialCalendar)
//                .build()

//            dateRangePicker.addOnPositiveButtonClickListener { selection ->
//                startDate = selection.first
//                endDate = selection.second
//                updateDateRangeButtonText()
//                validateInputs()
//            }

//            dateRangePicker.show(childFragmentManager, "DATE_RANGE_PICKER")
//        }
//    }
//
//    private fun updateDateRangeButtonText() {
//        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
//        val startDateStr = dateFormat.format(Date(startDate))
//        val endDateStr = dateFormat.format(Date(endDate))
//        binding.dateRangePickerButton.text = "$startDateStr - $endDateStr"
//    }

    private fun setupClassTypeChips() {
        val classTypes = resources.getStringArray(R.array.class_types)
        classTypes.forEach { type ->
            chipType = type
            val chip = Chip(context).apply {
                text = type
                isCheckable = true
                setChipBackgroundColorResource(R.color.chip_background_color)
                setTextColor(resources.getColorStateList(R.color.chip_text_color, null))
                setOnCheckedChangeListener { _, _ -> validateInputs() }
            }
            binding.classTypeChipGroup.addView(chip)
        }

        binding.addCustomTypeButton.setOnClickListener {
            val customType = binding.customTypeEditText.text.toString().trim()
            if (customType.isNotEmpty()) {
                val chip = Chip(context).apply {
                    text = customType
                    isCheckable = true
                    isChecked = true
                    setChipBackgroundColorResource(R.color.chip_background_color)
                    setTextColor(resources.getColorStateList(R.color.chip_text_color, null))
                    setOnCheckedChangeListener { _, _ -> validateInputs() }
                }
                binding.classTypeChipGroup.addView(chip)
                binding.customTypeEditText.text?.clear()
                validateInputs()
            }
        }
    }


    private fun clearInputs() {
        binding.edtCourseName.text?.clear()
        binding.edtDuration.text?.clear()
        binding.edtDesc.text?.clear()
        binding.classTypeChipGroup.clearCheck()
        binding.customTypeEditText.text?.clear()
        calendar.time = Date() // Reset to current date and time
        startDate = 0
        endDate = 0
//        binding.dateRangePickerButton.text = "Select Course Duration"
    }

    private fun setupAddClassButton() {
        binding.btnAddClass.setOnClickListener {
            //showAddClassDialog()
            //Toast.makeText(requireContext(), "click", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(),AddCourseActivity::class.java))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupClassesRecyclerView() {
        //.val classList : List<ClassEntry> = databaseViewModel.getSaveClassList()

        classAdapter = ClassAdapter(mutableListOf()) { data, root, pos ->
            when(root) {
                "edit" -> {
                    //Toast.makeText(requireContext(), "Edit", Toast.LENGTH_SHORT).show()
                }
                "delete" -> {
                    //Toast.makeText(requireContext(), "Delete", Toast.LENGTH_SHORT).show()
                    //classAdapter.no
                    databaseViewModel.deleteByClassId(data.classId)
                }
            }
        }
        binding.classesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = classAdapter
        }
    }

    private fun saveCourseToDB() {

        binding.saveButton.setOnClickListener {
            println("### ${classes.size}")
            if (binding.edtCourseName.text.trim().toString().isNullOrEmpty()){
                binding.edtCourseName.error = "Please fill course name!"

            } else if (binding.edtDuration.text.trim().toString().isNullOrEmpty()){
                binding.edtDuration.error = "Please add course duration!"
            }
            else {

                val course = CourseEntry(
                    courseName = binding.edtCourseName.text.trim().toString(),
//                    from_to_date = binding.dateRangePickerButton.text.trim().toString(),
                    description = binding.edtDesc.text.trim().toString(),
                    itemList = classes,
                    duration = binding.edtDuration.text.toString(),
                    classType = chipType,
                    pricing = binding.editPrice.text.trim().toString().toDouble(),
                    capacity = binding.editMaximumCapacity.text.toString().toInt(),
                    dayOfWeek = binding.dayOfWeek.text.trim().toString(),
                    timeOfDay = binding.timeOfDay.text.trim().toString(),
                    teacherName = classes.get(0).teacherName ?: classes.get(1).teacherName ?:""

                )
                databaseViewModel.insertCourse(course)
                clearInputs()
//                classes.clear()

                Toast.makeText(requireContext(), "Successfully saved...", Toast.LENGTH_SHORT).show()
            }


            databaseViewModel.deleteAllClass()
        }

    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Add listener to save button
//        binding.saveButton.setOnClickListener {
//            val courseName = binding.edtCourseName.text.toString()
//            val courseDuration = binding.edtCourseDuration.text.toString().toIntOrNull()
//            val courseCapacity = binding.edtCourseCapacity.text.toString().toIntOrNull()
//            val courseDescription = binding.edtCourseDescription.text.toString()
//
//            if (!courseName.isBlank() && courseDuration != null && courseCapacity != null) {
//                createNewCourse(courseName, courseDuration, courseCapacity, courseDescription)
//            } else {
//                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        listenToDB()
//        setSavedList()
//        setUpSearchAction()
//    }

}
