package com.example.ecommerce.ui.result

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.R
import com.example.ecommerce.databinding.FragmentResultBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class ResultFragment : Fragment() {
private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding=FragmentResultBinding.inflate(inflater, container, false)
        auth= FirebaseAuth.getInstance()
        val user:FirebaseUser?=auth.currentUser
        binding.button3.setOnClickListener {
findNavController().navigate(R.id.action_resultFragment2_to_loginFragment2)

        }
binding.button4.setOnClickListener {
    findNavController().navigate(R.id.action_resultFragment2_to_signUpFragment2)
}
        binding.button5.setOnClickListener {
            auth.signOut()
            Toast.makeText(context,"...${user?.uid} ${user?.email}", Toast.LENGTH_SHORT).show()
        }
   return binding.root
    }


}