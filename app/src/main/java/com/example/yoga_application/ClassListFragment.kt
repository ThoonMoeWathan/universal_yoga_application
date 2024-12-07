package com.example.yoga_application

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yoga_application.databinding.FragmentClassListBinding

class ClassListFragment : Fragment() {
    private var _binding: FragmentClassListBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: YogaDatabaseHelper
    private lateinit var classAdapter: ClassAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClassListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = YogaDatabaseHelper(requireContext())
        classAdapter = ClassAdapter(db.getAllClasses(), requireContext())

        binding.classesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.classesRecyclerView.adapter = classAdapter
        binding.addClassBtn.setOnClickListener{
            val intent = Intent(
                activity,
                AddClassActivity::class.java
            )
            startActivity(intent)
        }
        binding.searchClassBtn.setOnClickListener{
            if (binding.editSearch.visibility == View.GONE) {
                binding.editSearch.visibility = View.VISIBLE
            } else {
                binding.editSearch.visibility = View.GONE
                binding.editSearch.text.clear() // Clear search when hiding
                val teacherQuery = binding.editSearch.text.toString()
                val dateQuery = binding.editSearch.text.toString()  // New field for date
                val dayQuery = binding.editSearch.text.toString()    // New field for day

                val filteredClasses = db.getFilteredClasses(teacherQuery, dateQuery, dayQuery)
                classAdapter.refreshData(filteredClasses)
            }
        }
        // Add Text Change Listener for Search Functionality
        binding.editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    classAdapter.filter(s.toString())
                } catch (e: Exception) {
                    Log.e("ClassListFragment", "Error in search filter: ${e.message}")
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        super.onResume()
        classAdapter.refreshData(db.getAllClasses())
    }
}