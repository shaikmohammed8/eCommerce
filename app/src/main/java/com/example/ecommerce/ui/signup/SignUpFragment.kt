package com.example.ecommerce.ui.signup

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
import com.example.ecommerce.databinding.FragmentSignUpBinding
import com.example.ecommerce.repository.FirebaseRepository
import java.lang.Exception

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: SignUpViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        val repository=FirebaseRepository()
        viewModel=ViewModelProvider(this).get(SignUpViewModel::class.java)
        binding.lifecycleOwner=this
        viewModel.isSuccess.observe(viewLifecycleOwner, Observer {
             if (it){
                val intent=Intent(context,AfterLoginActivity::class.java)
                Log.d("SingUP","message$it")
                 startActivity(intent)
                //viewModel.onDoneNavigation()
            }
        })

        binding.registerSingUpButton.setOnClickListener {
            if (isValid()){
                viewModel.onSignUP(binding.emailForRegister.text.toString(),binding.passwordForRegister.text.toString())
            }

        }
        return binding.root
    }



    private fun isValid():Boolean{
        val email:String=binding.emailForRegister.text.toString().trim()
        if(binding.nameForRegister.text.toString().length<2||binding.nameForRegister.text.toString().trim().isEmpty()){
            binding.nameForRegister.error = "Invalid name"
            binding.nameForRegister.requestFocus()
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()||email.isEmpty()){
            binding.emailForRegister.error = "Invalid Email"
            binding.emailForRegister.requestFocus()

            return false
        }
        if(binding.PhoneNoForRegister.text.toString().length<=9||binding.PhoneNoForRegister.text.toString().trim().isEmpty()){
            binding.PhoneNoForRegister.error = "Invalid Phone no."
            binding.PhoneNoForRegister.requestFocus()
            return false
        }
        if(binding.passwordForRegister.text.toString().length<=5||binding.passwordForRegister.text.toString().trim().isEmpty()){
            binding.passwordForRegister.error = "Password should be greater then 6"
            binding.passwordForRegister.requestFocus()
            return false
        }
        return true
    }





}
