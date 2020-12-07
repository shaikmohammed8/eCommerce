package com.example.ecommerce.ui.login

import android.content.Intent
import android.graphics.Bitmap
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
import com.example.ecommerce.Constant.RC_SIGN_IN
import com.example.ecommerce.R
import com.example.ecommerce.databinding.FragmentLoginBinding
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

class LoginFragment : Fragment() {
private lateinit var  binding:FragmentLoginBinding
private lateinit var viewModel: LoginViewModel
private lateinit var googleSignInClient: GoogleSignInClient
private lateinit var auth: FirebaseAuth
private lateinit var bitmap:Bitmap
    private lateinit var callbackManager: CallbackManager
private lateinit var drawable:Drawable
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_login, container, false)
        viewModel=ViewModelProvider(this).get(LoginViewModel::class.java)
        drawable=AppCompatResources.getDrawable(requireContext(),R.drawable.login_button)!!
        bitmap=AppCompatResources.getDrawable(requireContext(),R.drawable.ic_done_white_48dp)?.toBitmap()!!
        callbackManager= CallbackManager.Factory.create()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        auth = FirebaseAuth.getInstance()

        viewModel.btAnimation.observe(viewLifecycleOwner, Observer {
            if (it){binding.LoginButton.startAnimation()}
            else{binding.LoginButton.revertAnimation()}
        })

        viewModel.isSuccess.observe(viewLifecycleOwner, Observer {
            if (it){ binding.LoginButton.doneLoadingAnimation(R.color.RedColor,bitmap)
                GlobalScope.launch{
                    delay(500)
                    val intent=Intent(context,AfterLoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish() } } })


        binding.LoginButton.setOnClickListener {
            if (isValid()) viewModel.login(binding.emailForLogin.text.toString(),binding.passwordForLogin.text.toString(),context)
        }
        binding.googleButtonLogin.setOnClickListener {
            binding.LoginButton.startAnimation()
            signIn() }
        binding.signUpNavigation.setOnClickListener { findNavController().navigate(R.id.action_loginFragment_to_signUpFragment) }
        binding.facebookButtonLogin.setOnClickListener { facebookSignIn() }
   return binding.root
    }

    private fun facebookSignIn() {
        binding.LoginButton.startAnimation()
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager,object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                handleFacebookSignUp(result!!.accessToken)
            }

            override fun onCancel() {
                binding.LoginButton.revertAnimation()
            }

            override fun onError(error: FacebookException?) {
                binding.LoginButton.revertAnimation()
            }

        })
    }

    private fun handleFacebookSignUp(token: AccessToken) {
        val credential= FacebookAuthProvider.getCredential(token.token)
        GlobalScope.launch(Dispatchers.IO) {
            auth.signInWithCredential(credential).addOnCompleteListener {
                if (it.isSuccessful){
                    binding.LoginButton.doneLoadingAnimation(R.color.RedColor,bitmap)
                    GlobalScope.launch(Dispatchers.Main) {
                        delay(500)
                        val intent=Intent(context,AfterLoginActivity::class.java)
                        startActivity(intent)
                        activity?.finish()}
                }
                else{
                    binding.LoginButton.revertAnimation()
                    Toast.makeText(context,"something went wrong please check your detail and connection",Toast.LENGTH_SHORT).show()
                }
            }}
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
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            binding.LoginButton.revertAnimation()
            Toast.makeText(context, "signInResult:failed code=${e.message}" ,Toast.LENGTH_LONG).show()

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        GlobalScope.launch(Dispatchers.IO) {
            val auth = auth.signInWithCredential(credential)
            withContext(Dispatchers.Main){ binding.LoginButton.doneLoadingAnimation(R.color.RedColor,bitmap)
                 }
            delay(500)
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