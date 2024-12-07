package com.example.yoga_application

import android.app.AlertDialog
import android.app.TimePickerDialog
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
import com.example.yoga_application.databinding.ActivityAddCourseBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AddCourseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddCourseBinding
    private lateinit var db: YogaDatabaseHelper
    private val dayList = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    private var selectedDay: String = ""

    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = YogaDatabaseHelper(this)

        // Set up Spinner for days
        val sortedDayList = dayList
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortedDayList)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        binding.spinnerDay.adapter = dayAdapter

        binding.spinnerDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedDay = sortedDayList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedDay = ""
            }
        }
        //End Spinner

        // Set up Time Picker Button
        binding.btnTimeSelection.setOnClickListener {
            showTimePicker()
        }

        // Save Button for adding course
        binding.saveCourseBtn.setOnClickListener {
            saveCourse()
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
    private fun saveCourse() {
        val courseName = binding.editCourse.text.toString()
        val day = selectedDay
        val time = binding.txtTime.text.toString()
        val capacity = binding.editCapacity.text.toString()
        val price = binding.editPrice.text.toString()
        val type = binding.editType.text.toString()
        val description = binding.editDescription.text.toString()

        if (courseName.isBlank() || day.isBlank() || time.isBlank() || capacity.isBlank() || price.isBlank() || type.isBlank()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Prepare the confirmation message
        val confirmationMessage = """
        Course Name: $courseName
        Day: $day
        Time: $time
        Capacity: $capacity
        Price: $price
        Type: $type
        Description: $description
    """.trimIndent()

        // Show confirmation dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Course Details")
        builder.setMessage(confirmationMessage)

        // Positive Button: Save the data
        builder.setPositiveButton("Save") { _, _ ->
            val course = Course(0, courseName, day, time, capacity, price, type, description)
            db.insertCourse(course)
            Toast.makeText(this, "Course Saved!", Toast.LENGTH_LONG).show()
            finish()
        }

        // Negative Button: Cancel
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        // Show the dialog
        builder.create().show()

//        val course = Course(0, courseName, day, time, capacity, price, type, description)
//        db.insertCourse(course)
//        finish()
//        Toast.makeText(this, "Course Saved!", Toast.LENGTH_LONG).show()
    }
}