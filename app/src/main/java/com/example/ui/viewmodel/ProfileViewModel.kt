package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.data.models.ParentUser
import com.example.data.models.Student
import com.example.data.repository.AsuliaRepository
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel(
    private val repository: AsuliaRepository
) : ViewModel() {

    val user: StateFlow<ParentUser?> = repository.currentUser
    val students: StateFlow<List<Student>> = repository.students

    fun updateProfile(name: String, email: String, mobile: String) {
        repository.updateParentProfile(name, email, mobile)
    }
}
