package com.example.roomexample.utilities

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase




//Singleton objects for using firebase services
object FirebaseService {
    val collectionRef = Firebase.firestore.collection("scenes")
    val firebaseAuth = FirebaseAuth.getInstance()
}

fun showSnackMessage(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
}

fun hideKeyboard(context: Context?, rootView:View) {
    val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(rootView.windowToken, 0)
}

