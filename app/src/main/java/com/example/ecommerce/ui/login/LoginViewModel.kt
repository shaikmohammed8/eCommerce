package com.example.ecommerce.ui.login

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce.repository.FirebaseRepository

class LoginViewModel: ViewModel() {
private val repository=FirebaseRepository()
    private val _btAnimation= MutableLiveData<Boolean>()
    val btAnimation: LiveData<Boolean> get() = _btAnimation

    private val _isSuccess= MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> get() = _isSuccess

    fun login(email:String,password:String,context:Context?) {
        _btAnimation.value=true

        repository.auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful) {_isSuccess.value=true }
                else { Toast.makeText(context,"${it.exception}",Toast.LENGTH_SHORT).show()
                    _btAnimation.value=false }



            }
    }
}