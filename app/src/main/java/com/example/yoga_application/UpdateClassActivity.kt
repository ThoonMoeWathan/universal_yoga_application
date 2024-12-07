package com.example.yoga_application

import android.R
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.yoga_application.databinding.ActivityUpdateClassBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class UpdateClassActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateClassBinding
    private lateinit var db: YogaDatabaseHelper
    private var courseList: List<Course> = listOf()
    private var selectedCourseId: Int = -1

    private var selectedCourseDay: String = ""
    private var classId: Int = -1  // ID of the class being edited

    private val dateFormat = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault())
    private val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateClassBinding.inflate(layoutInflater)
        setContentView(binding.root)


        db = YogaDatabaseHelper(this)

        // Get the class ID from the intent
        classId = intent.getIntExtra("class_id", -1)
        if (classId == -1) {
            Toast.makeText(this, "Class ID not found!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Load course list for spinner
        courseList = db.getAllCourses()
        val courseNames = courseList.map { it.courseName }
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, courseNames)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.updateSpinnerCourse.adapter = adapter

        // Set up spinner item selection
        binding.updateSpinnerCourse.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCourseId = courseList[position].id
                selectedCourseDay = courseList[position].day // Update selected course day of week
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCourseId = -1
                selectedCourseDay = "" // Reset selected day if nothing is selected
            }
        }

        // Load class details
        loadClassDetails()

        binding.updateSaveClassBtn

        // Set up date picker
        binding.updateBtnDaySelection.setOnClickListener {
            showDatePicker()
        }

        // Set up save button for updates
        binding.updateSaveClassBtn.setOnClickListener {
            updateClassDetails()
        }
    }

    private fun loadClassDetails() {
        // Retrieve the class details from the database
        val classModel = db.getClassByID(classId)
        if (classModel != null) {
            binding.updateTxtDay.text = classModel.date
            binding.updateEditTeacherName.setText(classModel.teacher)
            binding.updateEditComment.setText(classModel.comment)
            selectedCourseId = classModel.courseId

            // Set the spinner to the selected course
            val coursePosition = courseList.indexOfFirst { it.id == selectedCourseId }
            if (coursePosition != -1) {
                binding.updateSpinnerCourse.setSelection(coursePosition)
                selectedCourseDay = courseList[coursePosition].day // Set the day of week for existing course
            }
        } else {
            Toast.makeText(this, "Class not found!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updateClassDetails() {
        val selectedDate = binding.updateTxtDay.text.toString()
        val teacherName = binding.updateEditTeacherName.text.toString()
        val comment = binding.updateEditComment.text.toString()

        if (selectedCourseId == -1 || selectedDate.isBlank() || teacherName.isBlank()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Update the class object and save it to the database
        val updatedClass = Class(
            id = classId,
            date = selectedDate,
            teacher = teacherName,
            comment = comment,
            courseId = selectedCourseId.toString()
        )

        db.updateClass(updatedClass)
        Toast.makeText(this, "Class updated successfully!", Toast.LENGTH_LONG).show()
        finish()
    }

    private fun showDatePicker() {
        val calendar = android.icu.util.Calendar.getInstance()
        calendar.timeZone = android.icu.util.TimeZone.getTimeZone("Asia/Yangon")

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(Calendar.YEAR, selectedYear)
            selectedCalendar.set(Calendar.MONTH, selectedMonth)
            selectedCalendar.set(Calendar.DAY_OF_MONTH, selectedDay)

            val selectedDate = dateFormat.format(selectedCalendar.time)
            val selectedDayOfWeek = dayFormat.format(selectedCalendar.time)

            // Validate if selected date matches course's day of the week
            if (selectedDayOfWeek.equals(selectedCourseDay, ignoreCase = true)) {
                binding.updateTxtDay.text = selectedDate
            } else {
                Toast.makeText(
                    this,
                    "Selected date does not match the course day ($selectedCourseDay). Please select a $selectedCourseDay.",
                    Toast.LENGTH_SHORT
                ).show()
                binding.updateTxtDay.text = "" // Clear date selection
            }
        }

        DatePickerDialog(
            this, dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}