package com.example.ecommerce.ui.signup

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.repository.FirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SignUpViewModel: ViewModel(){
private val repository=FirebaseRepository()
private val _btAnimation=MutableLiveData<Boolean>()
  val btAnimation:LiveData<Boolean> get() = _btAnimation
  private val _isSuccess=MutableLiveData<Boolean>()
  val isSuccess:LiveData<Boolean> get() = _isSuccess



  fun onSignUP(email:String,password:String,context:Context?){
    viewModelScope.launch {
      _btAnimation.value=true
    withContext(Dispatchers.IO){
      repository.auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
      if (it.isSuccessful)
        _isSuccess.value=true
      else{_btAnimation.value=false
      Toast.makeText(context,"${it.exception}",Toast.LENGTH_SHORT).show()}
    }
  }}}

//  fun onDoneNavigation(){
//    _isSuccess.value==false
//  }

}