package com.example.universalyogalondon.activity

import android.app.Activity
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.universalyogalondon.data.db.entry.ClassEntry
import com.example.universalyogalondon.databinding.ActivityAddCourseBinding
import com.example.universalyogalondon.model.viewmodel.DatabaseViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class AddCourseActivity : AppCompatActivity() {

    lateinit var binding : ActivityAddCourseBinding
    private val viewModel : DatabaseViewModel by viewModels()
    var selectedDate: Long = 0
    var mProfileUri : Uri? = null

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data!!


                mProfileUri = fileUri

                updateProfile(fileUri)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Log.d("TAG", "Image Picker Error: ")
            } else {
                Log.d("TAG", "Cancel Image Pick: ")
            }
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSelectDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Class Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                selectedDate = selection
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                binding.btnSelectDate.text = dateFormat.format(Date(selectedDate))
            }

            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        binding.btnSelectTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                binding.btnSelectTime.text = SimpleDateFormat("HH:mm a").format(cal.time)
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(
                Calendar.MINUTE), true).show()
        }
        binding.btnAddPhoto.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)
                .cropSquare()
                .maxResultSize(70,70)
                .maxResultSize(1080, 1080)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        saveClassToDB()
    }

    private fun updateProfile(fileUri: Uri) {
        binding.btnAddPhoto.visibility = View.GONE
        binding.cvPhoto.visibility = View.VISIBLE
        binding.imvPhoto.setImageURI(fileUri)

    }

    private fun saveClassToDB() {
        binding.layoutAdd.setOnClickListener {
            val classEntry = ClassEntry(
                className = binding.classNameEditText.text.trim().toString(),
                teacherName = binding.teacherNameEditText.text.trim().toString(),
                date = binding.btnSelectDate.text.trim().toString(),
                time = binding.btnSelectTime.text.trim().toString(),
                image = mProfileUri.toString()
            )
            viewModel.insertClass(classEntry)
            finish()
        }

    }
}