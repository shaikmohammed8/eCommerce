package com.example.ecommerce.ui.mainscreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseUser

class MainScreenViewModel:ViewModel() {
    private val repository=FirebaseRepository()

    private val _cUser=MutableLiveData<FirebaseUser?>()
    val cUser:LiveData<FirebaseUser?> get() = _cUser


    fun getUser(){
       _cUser.value=repository.user()

   }

}