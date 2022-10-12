package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity


/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding
    private val viewModel by viewModels<AuthenticationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_authentication)
//         TODO: Implement the create account and sign in using FirebaseUI, use sign in using email and sign in using Google

//          TODO: If the user was authenticated, send him to RemindersActivity

//          TODO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout
        // Check if Registration before
        AuthState()
            // Else
        binding.loginbtn.setOnClickListener {
            SignIn()
        }
    }

    // Test if Login Successful Or not
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == SIGN_IN_RESULT_CODE){
            val res = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK){
                Log.i("AuthenticationActivity","Success SignIn")
            }else{
                Log.i("AuthenticationActivity","SignIn Unsuccessful ${res?.error?.errorCode}")
            }
        }
    }

    // Start Reminder if Successful Login
    private fun startReminderActivity(){
            startActivity(Intent(this, RemindersActivity::class.java))
            finish()
    }


    private fun SignIn() {
        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build())
        // start sign in - intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),SIGN_IN_RESULT_CODE
        )
    }

    // Return auth state
    private fun AuthState(){
        viewModel.authenticationState.observe(this, Observer { authenticationState ->
            when(authenticationState){
                AuthenticationViewModel.AuthenticationState.AUTHENTICATED -> {
                    startReminderActivity()
                }
                else ->{
                    binding.loginbtn.setOnClickListener { SignIn() }
                }
            }
        })
    }
    companion object {
        const val SIGN_IN_RESULT_CODE = 1001
    }
}
