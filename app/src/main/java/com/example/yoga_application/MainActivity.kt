package com.example.yoga_application

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.yoga_application.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: YogaDatabaseHelper
    private val baseURL = "http://10.0.2.2/UniversalYogaSite/"
    private lateinit var networkConnectionHelper: NetworkConnectionHelper
    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize helpers
        dbHelper = YogaDatabaseHelper(this)
        networkConnectionHelper = NetworkConnectionHelper(applicationContext)
        requestQueue = Volley.newRequestQueue(this)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_courses -> loadFragment(CourseListFragment())
                R.id.menu_instructors -> loadFragment(ClassListFragment())
                R.id.resetDbBtn -> {
                    showConfirmationDialog()
                    true
                }
                R.id.item_upload -> {
                    observeNetworkConnection()
                    true
                }
                else -> false
            }
        }

        // Load the first fragment by default
        loadFragment(CourseListFragment())
    }

    private fun observeNetworkConnection() {
        networkConnectionHelper.observe(this) { isConnected ->
            if (isConnected) {
                showToast("Connected to the internet!")
                uploadToServer()
            } else {
                showToast("No internet connection!")
            }
        }
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Reset")
            .setMessage("Are you sure you want to reset the database? This action cannot be undone.")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, _ ->
                dbHelper.deleteAll()
                deleteAllAPICall()
                showToast("Database reset successfully!")
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun uploadToServer() {
        val classJSONObj = dbHelper.getClassJsonObjectList()
        val courseJSONObj = dbHelper.getCourseJsonObjectList()

        if (classJSONObj.isEmpty() || courseJSONObj.isEmpty()) {
            showAlertDialog("Data Upload", "No data to upload.")
            return
        }

        // Upload courses
        val urlCourses = "$baseURL/saveCourse.php"
        val jsonArrayCourses = JSONArray(courseJSONObj)
        makeJsonArrayRequest(urlCourses, jsonArrayCourses, "Course Upload")

        // Upload classes
        val urlClasses = "$baseURL/saveClass.php"
        val jsonArrayClasses = JSONArray(classJSONObj)
        makeJsonArrayRequest(urlClasses, jsonArrayClasses, "Class Upload")
    }

    private fun makeJsonArrayRequest(url: String, jsonArray: JSONArray, type: String) {
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.POST,
            url,
            jsonArray,
            { response ->
                try {
                    if (response.length() > 0) {
                        val responseObj = response.getJSONObject(0)
                        val status = responseObj.optString("status", "Unknown")
                        val message = responseObj.optString("message", "No message")
                        showAlertDialog("$type Status", "$status: $message")
                    } else {
                        showAlertDialog("$type Response", "The server returned an empty response.")
                    }
                } catch (e: JSONException) {
                    Log.e("DBApp", "JSON Parsing Error: ${e.message}")
                    showAlertDialog("Error", "Error parsing server response.")
                }
            },
            { error ->
                Log.e("DBApp", "Volley Error: ${error.message}")
                showAlertDialog("Error", "Error uploading $type. Check your connection or logs.")
            }
        )
        requestQueue.add(jsonArrayRequest)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun deleteAllAPICall() {
        val url = "$baseURL/deleteAll.php"
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response -> showToast("Response: $response") },
            { error -> Log.e("DBApp", "Delete Error: ${error.message}") }
        )
        requestQueue.add(stringRequest)
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
        return true
    }
}
