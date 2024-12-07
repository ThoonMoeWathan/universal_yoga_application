package com.example.yoga_application

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class CourseAdapter (private var courses:List<Course>, context: Context):
    RecyclerView.Adapter<CourseAdapter.courseViewHolder>() {

    private val baseURL = "http://10.0.2.2/UniversalYogaSite/"

    private val db:YogaDatabaseHelper= YogaDatabaseHelper(context)

    class courseViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val courseName: TextView =itemView.findViewById(R.id.courseName)
        val updateBtn: ImageView =itemView.findViewById(R.id.updateBtn)
        val deleteBtn: ImageView =itemView.findViewById(R.id.deleteBtn)
        val detailbtn: ImageView =itemView.findViewById(R.id.detailBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): courseViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.course_item,parent,false)
        return courseViewHolder(view)
    }

    override fun getItemCount(): Int =courses.size

    override fun onBindViewHolder(holder: courseViewHolder, position: Int) {
        val course=courses[position]
        holder.courseName.text=course.courseName

        holder.updateBtn.setOnClickListener{
            val intent= Intent(holder.itemView.context, UpdateCourseActivity::class.java).apply {
                putExtra("course_id",course.id)
            }
            holder.itemView.context.startActivity(intent)
        }
        holder.deleteBtn.setOnClickListener{
            db.deleteCourse(course.id)
            refreshData(db.getAllCourses())
            Toast.makeText(holder.itemView.context,"Course Deleted!", Toast.LENGTH_LONG).show()
        }
        holder.detailbtn.setOnClickListener{
            val intent= Intent(holder.itemView.context, CourseDetailActivity::class.java).apply {
                putExtra("course_id",course.id)
            }
            holder.itemView.context.startActivity(intent)
        }
    }



    fun refreshData(newTasks:List<Course>){
        courses=newTasks
        notifyDataSetChanged()
    }
}