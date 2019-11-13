package com.example.scopingproject.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.scopingproject.R

class MagicLinkLogin : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_magic_link_login)
        if(intent != null && intent.hasExtra(PATH)){
            validateToken(PATH)
        } else {
            onValidationError()
        }
    }

    private fun validateToken(path: String){
        //TODO send the path back to the backend to validate the user login
        if(path.isNotEmpty()){
            onValidationSuccess(true)
        } else {
            onValidationError()
        }
    }

    private fun onValidationSuccess(hasPassword: Boolean){
        //TODO if user has a password skip sign up and take user to main activity
        if(hasPassword){
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun onValidationError(){
        //Sends the user to the original login screen if there is an error.
        startActivity(Intent(this, LoginActivity::class.java))
    }

    companion object{
        const val PATH = "path"
    }

}