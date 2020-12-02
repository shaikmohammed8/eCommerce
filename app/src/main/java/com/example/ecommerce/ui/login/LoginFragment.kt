package com.example.ecommerce.ui.login

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.AfterLoginActivity
import com.example.ecommerce.Constant.RC_SIGN_IN
import com.example.ecommerce.R
import com.example.ecommerce.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class LoginFragment : Fragment() {
private lateinit var  binding:FragmentLoginBinding
private lateinit var viewModel: LoginViewModel
private lateinit var googleSignInClient: GoogleSignInClient
private lateinit var auth: FirebaseAuth
private lateinit var bitmap:Bitmap
private lateinit var drawable:Drawable
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_login, container, false)
        viewModel=ViewModelProvider(this).get(LoginViewModel::class.java)
        drawable=AppCompatResources.getDrawable(requireContext(),R.drawable.round_button_white)!!
        bitmap=AppCompatResources.getDrawable(requireContext(),R.drawable.ic_baseline_check_24)?.toBitmap()!!
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        auth = FirebaseAuth.getInstance()

        viewModel.btAnimation.observe(viewLifecycleOwner, Observer {
            if (it){binding.loginSingInButton.startAnimation()}
            else{binding.loginSingInButton.revertAnimation{}
            binding.loginSingInButton.background=drawable}
        })

        viewModel.isSuccess.observe(viewLifecycleOwner, Observer {
            if (it){ binding.loginSingInButton.doneLoadingAnimation(Color.WHITE,bitmap!!)
                GlobalScope.launch{
                    delay(500)
                    val intent=Intent(context,AfterLoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish() } } })


        binding.loginSingInButton.setOnClickListener {
            if (isValid()) viewModel.login(binding.emailForLogin.text.toString(),binding.passwordForLogin.text.toString(),context)
        }
        binding.googleButton.setOnClickListener {  binding.googleButton.startAnimation{binding.loginSingInButton.background=drawable}
            signIn() }
        binding.signUpNavigation.setOnClickListener { findNavController().navigate(R.id.action_loginFragment_to_signUpFragment) }

   return binding.root
    }


    private fun signIn() {
        googleSignInClient.signOut()
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
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
            binding.googleButton.revertAnimation{binding.loginSingInButton.background=drawable}
            binding.googleButton.background=AppCompatResources.getDrawable(requireContext(),R.drawable.round_social_button)
            Toast.makeText(context, "signInResult:failed code=${e.message}" ,Toast.LENGTH_LONG).show()

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        GlobalScope.launch(Dispatchers.IO) {
            val auth = auth.signInWithCredential(credential)
            withContext(Dispatchers.Main){ binding.googleButton.doneLoadingAnimation(Color.WHITE,bitmap!!)}
            //delay(500)
            val intent=Intent(context,AfterLoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

    }


private fun isValid():Boolean{
    if(!Patterns.EMAIL_ADDRESS.matcher(binding.emailForLogin.text.toString()).matches()||binding.emailForLogin.text.toString().trim().isEmpty()){
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