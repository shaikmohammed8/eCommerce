package com.example.ecommerce.ui.result

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.MainActivity
import com.example.ecommerce.R
import com.example.ecommerce.databinding.FragmentResultBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class ResultFragment : Fragment() {
private lateinit var viewModel: ResultViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding=FragmentResultBinding.inflate(inflater, container, false)
        viewModel=ViewModelProvider(this).get(ResultViewModel::class.java)
        binding.lifecycleOwner=this

        binding.button3.setOnClickListener {
findNavController().navigate(R.id.action_resultFragment2_to_loginFragment2)

        }
binding.button4.setOnClickListener {
    findNavController().navigate(R.id.action_resultFragment2_to_signUpFragment2)
}
        binding.button5.setOnClickListener {
        viewModel.logOut()
            val intent=Intent(context,MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
   return binding.root
    }


}