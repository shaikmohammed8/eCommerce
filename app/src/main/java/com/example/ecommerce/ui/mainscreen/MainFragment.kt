package com.example.ecommerce.ui.mainscreen

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.AfterLoginActivity
import com.example.ecommerce.R
import com.example.ecommerce.databinding.FragmentMainBinding



class MainFragment : Fragment() {
 private lateinit var viewModel: MainScreenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding=FragmentMainBinding.inflate(inflater, container, false)
        viewModel=ViewModelProvider(this).get(MainScreenViewModel::class.java)
        binding.lifecycleOwner=this

        viewModel.cUser.observe(viewLifecycleOwner, Observer {
            if (it!=null){
                val intent=Intent(context,AfterLoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        })

        binding.mainLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
        }
        binding.mainSignUpbutton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_signUpFragment)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.getUser()
    }


    }
