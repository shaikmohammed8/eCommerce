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
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.*

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: SignUpViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var bitmap:Bitmap
    private lateinit var drawable:Drawable
    private lateinit var callbackManager: CallbackManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        viewModel=ViewModelProvider(this).get(SignUpViewModel::class.java)
        binding.lifecycleOwner=this

        drawable=AppCompatResources.getDrawable(requireContext(),R.drawable.round_button_white)!!
        bitmap=AppCompatResources.getDrawable(requireContext(),R.drawable.ic_done_white_48dp)?.toBitmap()!!
        callbackManager= CallbackManager.Factory.create()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        auth = FirebaseAuth.getInstance()

        viewModel.btAnimation.observe(viewLifecycleOwner, Observer {
            if (it){binding.SignUpButton.startAnimation()}
            else{binding.SignUpButton.revertAnimation()
                binding.SignUpButton.background=drawable}
        })
        viewModel.isSuccess.observe(viewLifecycleOwner, Observer {
             if (it) {binding.SignUpButton.doneLoadingAnimation(Color.WHITE,bitmap)
                 GlobalScope.launch{
                     delay(500)
                     val intent=Intent(context,AfterLoginActivity::class.java)
                     startActivity(intent)
                     activity?.finish() } } })

        binding.loginNavigation.setOnClickListener { findNavController().navigate(R.id.action_signUpFragment_to_loginFragment) }
        binding.SignUpButton.setOnClickListener { if (isValid()){ viewModel.onSignUP(binding.emailForSignUp.text.toString(),binding.passwordForSignUp.text.toString(),context) } }
        binding.googleButtonSignup.setOnClickListener {binding.SignUpButton.startAnimation()
            signIn() }
        binding.facebookButtonSignup.setOnClickListener { facebookLogin() }

        return binding.root
    }


    private fun facebookLogin(){
        binding.SignUpButton.startAnimation()
       LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager,object :FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                handleFacebookSignIt(result!!.accessToken)
            }

            override fun onCancel() {
              binding.SignUpButton.revertAnimation()
            }

            override fun onError(error: FacebookException?) {
                binding.SignUpButton.revertAnimation()
            }

        })
    }
    private fun handleFacebookSignIt(token: AccessToken) {
        val credential=FacebookAuthProvider.getCredential(token.token)
        GlobalScope.launch(Dispatchers.IO) {
    auth.signInWithCredential(credential).addOnCompleteListener {
        if (it.isSuccessful){
            binding.SignUpButton.doneLoadingAnimation(R.color.RedColor,bitmap)
            GlobalScope.launch(Dispatchers.Main) {
            delay(500)
            val intent=Intent(context,AfterLoginActivity::class.java)
            startActivity(intent)
            activity?.finish()}
        }
        else{
            binding.SignUpButton.revertAnimation()
            Toast.makeText(context,"something went wrong please check your detail and connection",Toast.LENGTH_SHORT).show()
        }
    }}
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
        else {callbackManager.onActivityResult(requestCode,resultCode,data)}
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
           binding.SignUpButton.revertAnimation()
            Toast.makeText(context, "signInResult:failed code=${e.message}" , Toast.LENGTH_LONG).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        GlobalScope.launch(Dispatchers.IO) {
            val auth = auth.signInWithCredential(credential)
           withContext(Dispatchers.Main){ binding.SignUpButton.doneLoadingAnimation(R.color.RedColor,bitmap)
           delay(500)
            val intent=Intent(context,AfterLoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }}}


    private fun isValid():Boolean{
        val email:String=binding.emailForSignUp.text.toString().trim()
        if(binding.nameForSignUp.text.toString().length<2||binding.nameForSignUp.text.toString().trim().isEmpty()){
            binding.nameForSignUp.error = "Invalid name"
            binding.nameForSignUp.requestFocus()
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()||email.isEmpty()){
            binding.emailForSignUp.error = "Invalid Email"
            binding.emailForSignUp.requestFocus()

            return false
        }
        if(binding.phoneNoForSignUp.text.toString().length!=10||binding.phoneNoForSignUp.text.toString().trim().isEmpty()){
            binding.phoneNoForSignUp.error = "Invalid Phone no."
            binding.phoneNoForSignUp.requestFocus()
            return false
        }
        if(binding.passwordForSignUp.text.toString().length<=5||binding.passwordForSignUp.text.toString().trim().isEmpty()){
            binding.passwordForSignUp.error = "Password should be greater then 6"
            binding.passwordForSignUp.requestFocus()
            return false
        }
        return true
    }





}
