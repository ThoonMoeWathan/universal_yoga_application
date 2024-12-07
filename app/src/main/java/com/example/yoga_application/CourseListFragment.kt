package com.example.yoga_application

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yoga_application.databinding.FragmentCourseListBinding


class CourseListFragment : Fragment() {
    private var _binding: FragmentCourseListBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: YogaDatabaseHelper
    private lateinit var courseAdapter: CourseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCourseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = YogaDatabaseHelper(requireContext())
        courseAdapter = CourseAdapter(db.getAllCourses(), requireContext())

        binding.coursesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.coursesRecyclerView.adapter = courseAdapter
        binding.addCourseBtn.setOnClickListener{
            val intent = Intent(
                activity,
                AddCourseActivity::class.java
            )
            startActivity(intent)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        super.onResume()
        courseAdapter.refreshData(db.getAllCourses())
    }
}