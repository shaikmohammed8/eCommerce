package com.example.ecommerce.ui.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.repository.FirebaseRepository
import kotlinx.coroutines.launch


class SignUpViewModel: ViewModel(){
private val repository=FirebaseRepository()

  private val _isSuccess=MutableLiveData<Boolean>()
  val isSuccess:LiveData<Boolean> get() = _isSuccess



  fun onSignUP(email:String,password:String){
    repository.auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
      if (it.isSuccessful)
        _isSuccess.value=true

    }
  }

//  fun onDoneNavigation(){
//    _isSuccess.value==false
//  }

}