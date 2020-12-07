package com.example.ecommerce.repository


import com.google.firebase.auth.FirebaseAuth

class FirebaseRepository{

    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }



    fun user()=auth.currentUser
    fun signOut()=auth.signOut()
}