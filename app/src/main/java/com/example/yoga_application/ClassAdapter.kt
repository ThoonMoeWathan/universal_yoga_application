package com.example.yoga_application

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ClassAdapter (private var classes:List<Class>, context: Context):

    RecyclerView.Adapter<ClassAdapter.classViewHolder>() {
    private val db:YogaDatabaseHelper= YogaDatabaseHelper(context)
    private var originalClassesList: List<Class> = classes.toList() // Initial list for reference
    private var filteredClassesList: List<Class> = classes

    class classViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val TeacherName: TextView =itemView.findViewById(R.id.teacher)
        val dateOfClass:TextView=itemView.findViewById(R.id.dateOfClass)
        val updateBtn: ImageView =itemView.findViewById(R.id.updateClassBtn)
        val deleteBtn: ImageView =itemView.findViewById(R.id.deleteClassBtn)
        val detailbtn: ImageView =itemView.findViewById(R.id.detailClassBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): classViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.class_item,parent,false)
        return classViewHolder(view)
    }
    // Update getItemCount and onBindViewHolder to use filteredClassesList
    override fun getItemCount(): Int {
        return filteredClassesList.size
    }
    override fun onBindViewHolder(holder: classViewHolder, position: Int) {
        val classItem = filteredClassesList[position] // Bind classItem to the holder
        //search end
        holder.TeacherName.text = classItem.teacher
        holder.dateOfClass.text = classItem.date

        holder.updateBtn.setOnClickListener{
            val intent= Intent(holder.itemView.context, UpdateClassActivity::class.java).apply {
                putExtra("class_id",classItem.id)
            }
            holder.itemView.context.startActivity(intent)
        }
        holder.deleteBtn.setOnClickListener{
            db.deleteClass(classItem.id)
//            deleteClassAPICall(classItem.id.toString())
            refreshData(db.getAllClasses())
            Toast.makeText(holder.itemView.context,"Class Deleted!", Toast.LENGTH_LONG).show()
        }
        holder.detailbtn.setOnClickListener{
            val intent= Intent(holder.itemView.context, ClassDetailActivity::class.java).apply {
                putExtra("class_id",classItem.id)
            }
            holder.itemView.context.startActivity(intent)
        }
    }

//    private val baseURL = "http://10.0.2.2/UniversalYogaSite/"
//
//    private fun deleteClassAPICall(id: String?) {
//        val url = "$baseURL/deleteClass.php"
//
//        val jsonBody = JSONObject().apply {
//            put("id", id)
//        }
//
//        val jsonObjectRequest = JsonObjectRequest(
//            Request.Method.POST, url, jsonBody,
//            { response ->
//                try {
//                    val status = response.getString("status")
//                    if (status == "success") {
//                        showToast("Class deleted from server successfully!")
//                    } else {
//                        showToast("Failed to delete class from server")
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    showToast("Error parsing server response")
//                }
//            },
//            { error ->
//                Log.e("DBApp", "Delete Error: ${error.message}")
//                showToast("Error syncing with server")
//            }
//        )
//        Volley.newRequestQueue(context).add(jsonObjectRequest)
//    }
//
//    private fun showToast(message: String) {
//        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
//    }


        // search
    fun filter(query: String) {
        filteredClassesList = if (query.isEmpty()) {
            originalClassesList
        } else {
            originalClassesList.filter {
                it.teacher.contains(query, ignoreCase = true) || // Adjust for your model's field name
                it.date.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    // searchEnd
    // Refresh data in adapter
    fun refreshData(newClassesList: List<Class>) {
        originalClassesList = newClassesList.toList()
        filteredClassesList = newClassesList
        notifyDataSetChanged()
    }
}