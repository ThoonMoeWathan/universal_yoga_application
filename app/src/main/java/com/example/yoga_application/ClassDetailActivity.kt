package com.example.yoga_application

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.yoga_application.databinding.ActivityClassDetailBinding
import com.example.yoga_application.databinding.ActivityCourseDetailBinding

class ClassDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClassDetailBinding
    private lateinit var db: YogaDatabaseHelper
    private var classId: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityClassDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db=YogaDatabaseHelper(this)


        // Retrieve class ID from intent
        classId=intent.getIntExtra("class_id",-1)
        if(classId== -1){
            finish()
            return
        }

        // Get class details by ID and populate fields
        val classModel=db.getClassByID(classId)
        binding.txtTeacherName.setText("Teacher Name: "+classModel?.teacher)
        binding.txtCourseName.setText("Course Name: "+classModel?.courseName)
        binding.txtClassDate.setText("Class Date: "+classModel?.date)
        binding.txtClassDay.setText("Day Of Week: "+classModel?.day)
        binding.txtComment.setText("Comment: "+classModel?.comment)
        binding.txtTime.setText("Class Time: "+classModel?.time)
        binding.txtCapacity.setText("Member Limit: "+classModel?.capacity)
        binding.txtPrice.setText("Class Fee: "+classModel?.price+" $")
        binding.txtType.setText("Yoga Type: "+classModel?.type)
        binding.txtDescription.setText("Description: "+classModel?.description)
    }
}