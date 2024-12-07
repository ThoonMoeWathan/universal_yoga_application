package com.example.yoga_application

data class ClassWithCourse(
    val classId: Int,
    val teacher: String,
    val date: String,
    val comment: String?=null,
    val courseId:Int,
    val courseName:String,
    val day:String,
    val time:String,
    val capacity: String,
    val price: String,
    val type:String,
    val description:String?=null
)
