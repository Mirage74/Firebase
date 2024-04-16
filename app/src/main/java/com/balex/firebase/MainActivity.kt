package com.balex.firebase

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.balex.firebase.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    val auth = FirebaseAuth.getInstance()

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkUser()
        addListeners()
        //regUser("balexvicx@gmail.com", "pass123")
    }

    private fun addListeners() {
        binding.buttonLogin.setOnClickListener {
            loginUser(binding.login.text.toString(), binding.password.text.toString())
        }

        binding.buttonLogout.setOnClickListener {
            auth.signOut()
            updateUI(NOT_AUTHORIZED_USER)

        }
        binding.buttonResetPassword.setOnClickListener {
            resetPass()
        }

        auth.addAuthStateListener {
            Log.d(TAG, "addAuthStateListener: ${auth.currentUser}")
        }
    }

    private fun resetPass() {
        auth.sendPasswordResetEmail(binding.login.text.toString())
            .addOnCompleteListener  { task ->
            if (task.isSuccessful) {
                val result = task.result
                Log.d(TAG, "Successfully send")
            } else {
                Log.d(TAG, "Failure send")
            }
        }
            .addOnFailureListener {
                Log.d(TAG, "Error signing in with email link", it)
            }
    }

    fun emn () {

    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    //auth.currentUser?.isEmailVerified
                    auth.currentUser?.sendEmailVerification()
                    Log.d(TAG, "Successfully signed: ${auth.currentUser}")
                    Log.d(TAG, "${result.getAdditionalUserInfo()?.getProfile()}")
                    Log.d(TAG, "${result.getAdditionalUserInfo()?.isNewUser()}")
                    auth.currentUser?.let { updateUI(it.uid) }
                } else {
                    Log.d(TAG, "Error signing in with email link", task.exception)
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Error signing in with email link", it)
            }
    }

    private fun updateUI(user: String) {
        binding.userName.text = user
    }

    private fun checkUser(): Boolean {
        val user = auth.currentUser
        var userName = NOT_AUTHORIZED_USER
        return if (user == null) {
            Log.d(TAG, NOT_AUTHORIZED_USER)
            updateUI(userName)
            false
        } else {
            Log.d(TAG, "Authorized")
            userName = user.uid
            updateUI(userName)
            true
        }
    }


    private fun regUser(email: String, password: String) {
        var userName = NOT_AUTHORIZED_USER
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    userName = auth.currentUser.toString()
                } else {
                    Log.d(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        updateUI(userName)
    }

    companion object {
        val NOT_AUTHORIZED_USER = "Not authorized"
    }


    override fun onDestroy() {
        super.onDestroy()
        auth.signOut()
    }
}