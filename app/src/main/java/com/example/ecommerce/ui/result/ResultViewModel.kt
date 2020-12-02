package com.example.ecommerce.ui.result

import androidx.lifecycle.ViewModel
import com.example.ecommerce.repository.FirebaseRepository

class ResultViewModel:ViewModel(){
    private val repository=FirebaseRepository()

    fun logOut(){
        repository.signOut()
    }
}