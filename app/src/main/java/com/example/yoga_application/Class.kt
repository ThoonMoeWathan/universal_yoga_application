package com.example.yoga_application

data class Class(
    val id:Int,
    val date: String,
    val teacher: String,
    val comment: String?=null,
    val courseId: String
)
