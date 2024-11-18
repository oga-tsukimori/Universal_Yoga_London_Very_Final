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

//        setupDateRangePicker()
        setupClassTypeChips()
        //setupSaveButton()
        setupInputValidation()
        setupAddClassButton()
        setupClassesRecyclerView()

        saveCourseToDB()

        listenToDB()
    }


    private fun listenToDB() {
        databaseViewModel.getSaveClassList()
        databaseViewModel.classList.observe(viewLifecycleOwner) {
            if (it is ViewState.Success) {
                it.value?.let { classes.addAll(it) }
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

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            val className = binding.edtCourseName.text.toString()
            if (className.isBlank()) {
                Toast.makeText(context, "Please enter a course name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Remove the instructor variable
            val duration = binding.edtDuration.text.toString().toIntOrNull() ?: 0
            val selectedTypes = binding.classTypeChipGroup.checkedChipIds.map { chipId ->
                (binding.classTypeChipGroup.findViewById<Chip>(chipId)).text.toString()
            }

            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
            val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(startDate))

            val yogaClass = YogaClass(
                id = UUID.randomUUID().toString(),
                name = className,
                instructor = "", // Pass an empty string for now
                time = time,
                startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                    Date(
                        startDate
                    )
                ),
                endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(endDate)),
                dayOfWeek = dayOfWeek,
                duration = duration,
                level = selectedTypes.joinToString(", "),
                description = binding.edtDesc.text.toString(),
                price = TODO(),
                chipGroup = TODO(),
                maximumCapacity = TODO(),
                timeOfDay = binding.timeOfDay.text.toString().toInt(),


            )

            saveCourseToDatabase(yogaClass)
//            YogaClassStorage.addClass(yogaClass)
//            viewModel.addCourse(yogaClass) // Add this line
//            Toast.makeText(context, "Course saved successfully", Toast.LENGTH_SHORT).show()
            //databaseViewModel.insertClass()
            clearInputs()
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

    private fun showAddClassDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_class, null)
        val classNameEditText = dialogView.findViewById<EditText>(R.id.classNameEditText)
        val teacherNameEditText = dialogView.findViewById<EditText>(R.id.teacherNameEditText)
        val datePickerButton = dialogView.findViewById<TextView>(R.id.datePickerButton)
        val btnSelectTime = dialogView.findViewById<TextView>(R.id.btnSelectTime)
        val btnAddPhoto = dialogView.findViewById<TextView>(R.id.btnAddPhoto)
        val cardView = dialogView.findViewById<CardView>(R.id.cvPhoto)
        val image = dialogView.findViewById<ImageView>(R.id.imvPhoto)

        var selectedDate: Long = 0

        btnSelectTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                btnSelectTime.text = SimpleDateFormat("HH:mm a").format(cal.time)
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        btnAddPhoto.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)
                .cropSquare()
                .maxResultSize(70,70)
                .maxResultSize(1080, 1080)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        if (mProfileUri == null) {
            btnAddPhoto.visibility = View.VISIBLE
            cardView.visibility = View.GONE
        } else {
            btnAddPhoto.visibility = View.GONE
            cardView.visibility = View.VISIBLE
            image.setImageURI(mProfileUri)
        }

        datePickerButton.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Class Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                selectedDate = selection
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                datePickerButton.text = dateFormat.format(Date(selectedDate))
            }

            datePicker.show(childFragmentManager, "DATE_PICKER")
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Add New Class")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val className = classNameEditText.text.toString()
                val teacherName = teacherNameEditText.text.toString()
                if (className.isNotBlank() && teacherName.isNotBlank() && selectedDate != 0L) {
                    val newClass = ClassInfo(className, teacherName, selectedDate)
                    //classes.add(newClass)
                    classAdapter.notifyItemInserted(classes.size - 1)
                    Toast.makeText(context, "New class added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
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

    private fun saveCourseToDatabase(yogaClass: YogaClass) {

        val result = dbHelper.insertCourse(yogaClass.name
            , yogaClass.duration, yogaClass.description, yogaClass.level)

        if (result > 0) {
            Toast.makeText(context, "Course saved successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed to save course", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveCourseToDB() {

        binding.saveButton.setOnClickListener {
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
                   timeOfDay = binding.timeOfDay.text.trim().toString()

                )
                databaseViewModel.insertCourse(course)
                clearInputs()
                Toast.makeText(requireContext(), "Successfully saved...", Toast.LENGTH_SHORT).show()
            }

        }

    }
}
