package com.example.ecommerce.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce.repository.FirebaseRepository

class LoginViewModel: ViewModel() {
private val repository=FirebaseRepository()

    private val _isSuccess= MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> get() = _isSuccess

    fun login(email:String,password:String) {
        repository.auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful) _isSuccess.value=true
            }
    }
}