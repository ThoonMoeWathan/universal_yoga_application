package com.example.yoga_application

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.yoga_application.databinding.ActivityAddClassBinding
import java.lang.Class
import java.text.SimpleDateFormat
import java.util.Locale

class AddClassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddClassBinding
    private lateinit var db: YogaDatabaseHelper
    private var courseList: List<Course> = listOf()
    private var selectedCourseId: Int = -1
    private var selectedCourseDay: String = ""  // Day of the week for selected course

    private val dateFormat = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault())
    private val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())  // To get day of the week

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddClassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db= YogaDatabaseHelper(this)

        binding.btnDaySelection.setOnClickListener{
            showDatePicker()
        }

        // Fetch courses from the database and populate the spinner
        courseList = db.getAllCourses()
        val courseNames = courseList.map { it.courseName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courseNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCourse.adapter = adapter

        // Set a listener to get the selected course ID and day of the week
        binding.spinnerCourse.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCourse = courseList[position]
                selectedCourseId = selectedCourse.id
                selectedCourseDay = selectedCourse.day  // Assign day of the week for validation
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCourseId = -1
                selectedCourseDay = ""
            }
        }
        binding.saveClassBtn.setOnClickListener{
            if (selectedCourseId != -1) {
                val selectedDate = binding.txtDay.text.toString()
                val teacherName = binding.editTeacherName.text.toString()
                val comment = binding.editComment.text.toString()

                if (selectedDate.isNotEmpty() && teacherName.isNotEmpty()) {
                    val classModel = com.example.yoga_application.Class(
                        id = 0,
                        date = selectedDate,
                        teacher = teacherName,
                        comment = comment,
                        courseId = selectedCourseId.toString()
                    )
                    db.insertClass(classModel)
                    Toast.makeText(this, "Class Saved!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please select a Course", Toast.LENGTH_SHORT).show()
            }

        }
    }
private fun showDatePicker() {
    val calendar = Calendar.getInstance()
    calendar.timeZone = TimeZone.getTimeZone("Asia/Yangon")

    val dateSetListener = DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.set(Calendar.YEAR, selectedYear)
        selectedCalendar.set(Calendar.MONTH, selectedMonth)
        selectedCalendar.set(Calendar.DAY_OF_MONTH, selectedDay)

        val selectedDate = dateFormat.format(selectedCalendar.time)
        val selectedDayOfWeek = dayFormat.format(selectedCalendar.time)

        // Validate if selected date matches course's day of the week
        if (selectedDayOfWeek.equals(selectedCourseDay, ignoreCase = true)) {
            binding.txtDay.text = selectedDate
        } else {
            Toast.makeText(
                this,
                "Selected date does not match the course day ($selectedCourseDay). Please select a $selectedCourseDay.",
                Toast.LENGTH_SHORT
            ).show()
            binding.txtDay.text = ""  // Clear date selection
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