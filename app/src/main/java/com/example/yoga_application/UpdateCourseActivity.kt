package com.example.yoga_application

import android.R
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.yoga_application.databinding.ActivityUpdateCourseBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class UpdateCourseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateCourseBinding
    private lateinit var db: YogaDatabaseHelper
    private val dayList = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    private var selectedDay: String = ""
    private var courseId: Int = -1

    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUpdateCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db=YogaDatabaseHelper(this)

        // Get the course ID from the intent
        courseId = intent.getIntExtra("course_id", -1)
        if (courseId == -1) {
            Toast.makeText(this, "Course ID not found!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Load course details into fields
        loadCourseDetails()

        // Set up Spinner for days
        val dayAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, dayList)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        binding.updateSpinnerDay.adapter = dayAdapter

        binding.updateSpinnerDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedDay = dayList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedDay = ""
            }
        }
        // Set up Time Picker
        binding.updateBtnTimeSelection.setOnClickListener {
            showTimePicker()
        }

        // Update Button for saving course details
        binding.updateCourseBtn.setOnClickListener {
            updateCourse()
        }
    }

    private fun loadCourseDetails() {
        val course = db.getCourseByID(courseId)
        if (course != null) {
            binding.updateEditCourse.setText(course.courseName)
            binding.txtTime.text = course.time
            binding.updateEditCapacity.setText(course.capacity)
            binding.updateEditPrice.setText(course.price)
            binding.updateEditType.setText(course.type)
            binding.updateEditDescription.setText(course.description)
            selectedDay = course.day // Retrieve day from the database

            // Set spinner to the selected day
            val dayPosition = dayList.indexOf(selectedDay)
            if (dayPosition != -1) {
                binding.updateSpinnerDay.setSelection(dayPosition)
            }
        } else {
            Toast.makeText(this, "Course not found!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }


    private fun showTimePicker() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Yangon"))
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, selectedHour)
                set(Calendar.MINUTE, selectedMinute)
            }
            binding.txtTime.text = timeFormat.format(selectedCalendar.time)
        }

        TimePickerDialog(
            this,
            timeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun updateCourse() {
        val courseName = binding.updateEditCourse.text.toString()
        val day = selectedDay
        val time = binding.txtTime.text.toString()
        val capacity = binding.updateEditCapacity.text.toString()
        val price = binding.updateEditPrice.text.toString()
        val type = binding.updateEditType.text.toString()
        val description = binding.updateEditDescription.text.toString()

        // Check for required fields
        if (courseName.isBlank() || day.isBlank() || time.isBlank() || capacity.isBlank() || price.isBlank() || type.isBlank()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Update the course object
        val updatedCourse = Course(
            id = courseId,
            courseName = courseName,
            day = day,
            time = time,
            capacity = capacity,
            price = price,
            type = type,
            description = description
        )

        db.updateCourse(updatedCourse)
        Toast.makeText(this, "Course updated successfully!", Toast.LENGTH_LONG).show()
        finish()
    }
}