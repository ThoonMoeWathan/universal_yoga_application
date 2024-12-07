package com.example.yoga_application

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.yoga_application.databinding.ActivityCourseDetailBinding

class CourseDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCourseDetailBinding
    private lateinit var db: YogaDatabaseHelper
    private var courseId: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCourseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db=YogaDatabaseHelper(this)


        // Retrieve course ID from intent
        courseId=intent.getIntExtra("course_id",-1)
        if(courseId== -1){
            finish()
            return
        }

        // Get course details by ID and populate fields
        val course=db.getCourseByID(courseId)
        binding.courseName.setText("Course Name: "+course.courseName)
        binding.courseDay.setText("Day Of Week: "+course.day)
        binding.courseTime.setText("Course Time: "+course.time)
        binding.courseCapacity.setText("Member Limit: "+course.capacity)
        binding.coursePrice.setText("Fee: "+course.price+" $")
        binding.courseTypetxt.setText("Yoga Type: "+course.type)
        binding.courseDescrption.setText("Description: "+course.description)
    }
}