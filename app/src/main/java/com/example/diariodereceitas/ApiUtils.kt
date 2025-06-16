package com.example.diariodereceitas

import com.google.firebase.auth.FirebaseAuth

fun getFirebaseIdToken(onResult: (String?) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    user?.getIdToken(true)
        ?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result?.token
                onResult(idToken)
            } else {
                onResult(null)
            }
        }
}
