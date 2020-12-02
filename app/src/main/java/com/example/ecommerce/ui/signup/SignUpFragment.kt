package com.example.ecommerce.ui.signup

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.AfterLoginActivity
import com.example.ecommerce.Constant
import com.example.ecommerce.R
import com.example.ecommerce.databinding.FragmentSignUpBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: SignUpViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var bitmap:Bitmap
    private lateinit var drawable:Drawable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        viewModel=ViewModelProvider(this).get(SignUpViewModel::class.java)
        binding.lifecycleOwner=this
        drawable=AppCompatResources.getDrawable(requireContext(),R.drawable.round_button_white)!!
        bitmap=AppCompatResources.getDrawable(requireContext(),R.drawable.ic_baseline_check_24)?.toBitmap()!!
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        auth = FirebaseAuth.getInstance()

        viewModel.btAnimation.observe(viewLifecycleOwner, Observer {
            if (it){binding.registerSingUpButton.startAnimation()}
            else{binding.registerSingUpButton.revertAnimation()
                binding.registerSingUpButton.background=drawable}
        })
        viewModel.isSuccess.observe(viewLifecycleOwner, Observer {
             if (it) {binding.registerSingUpButton.doneLoadingAnimation(Color.WHITE,bitmap!!)
                 GlobalScope.launch{
                     delay(500)
                     val intent=Intent(context,AfterLoginActivity::class.java)
                     startActivity(intent)
                     activity?.finish() } } })

        binding.loginNavigation.setOnClickListener { findNavController().navigate(R.id.action_signUpFragment_to_loginFragment) }
        binding.registerSingUpButton.setOnClickListener { if (isValid()){ viewModel.onSignUP(binding.emailForRegister.text.toString(),binding.passwordForRegister.text.toString(),context) } }
        binding.googleButton.setOnClickListener { signIn() }
        return binding.root
    }
    private fun signIn() {
        googleSignInClient.signOut()
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, Constant.RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constant.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
           binding.googleButton.revertAnimation()
           binding.googleButton.background=AppCompatResources.getDrawable(requireContext(),R.drawable.round_social_button)
            Toast.makeText(context, "signInResult:failed code=${e.message}" , Toast.LENGTH_LONG).show()

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        binding.googleButton.startAnimation()
        GlobalScope.launch(Dispatchers.IO) {
            val auth = auth.signInWithCredential(credential)
            binding.googleButton.doneLoadingAnimation(Color.WHITE,bitmap)
            val intent=Intent(context,AfterLoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

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
