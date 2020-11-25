package com.example.ecommerce.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.ecommerce.AfterLoginActivity
import com.example.ecommerce.R
import com.example.ecommerce.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
private lateinit var  binding:FragmentLoginBinding
private lateinit var viewModel: LoginViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_login, container, false)
        viewModel=ViewModelProvider(this).get(LoginViewModel::class.java)

        viewModel.isSuccess.observe(viewLifecycleOwner, Observer {
            if (it){
                val intent=Intent(context,AfterLoginActivity::class.java)
                Log.d("SingUP","message$it")
                startActivity(intent)
                //viewModel.onDoneNavigation()
            }
        })
        binding.loginSingInButton.setOnClickListener {
          viewModel.login(binding.emailForLogin.text.toString(),binding.passwordForLogin.text.toString())
        }

   return binding.root
    }



private fun isValid():Boolean{
    if(!Patterns.EMAIL_ADDRESS.matcher(binding.emailForLogin.toString().trim()).matches()||binding.emailForLogin.toString().trim().isEmpty()){
        binding.emailForLogin.error = "Invalid Email"
        binding.emailForLogin.requestFocus()

        return false
    }
    if(binding.passwordForLogin.text.toString().length<=5||binding.passwordForLogin.text.toString().trim().isEmpty()){
        binding.passwordForLogin.error = "Password should be greater then 6"
        binding.passwordForLogin.requestFocus()
        return false
    }
    return true
}
}