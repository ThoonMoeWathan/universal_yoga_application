package com.example.yoga_application

data class Course(
    val id:Int,
    val courseName:String,
    val day:String,
    val time:String,
    val capacity: String,
    val price: String,
    val type:String,
    val description:String?=null
)
