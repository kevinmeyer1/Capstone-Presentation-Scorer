package com.example.scopingproject.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.scopingproject.R
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import java.io.IOException

class LoginActivity : AppCompatActivity(){

    var isPasswordShown = true
    var client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        buttonLoginWithPassword.setOnClickListener {togglePasswordField()}
        buttonLogin.setOnClickListener {login()}

    }

    private fun togglePasswordField(){
        if(isPasswordShown){
            textInputLayoutPassword.visibility = View.GONE
            buttonLogin.text = getString(R.string.send_me_a_magic_link)
            buttonLoginWithPassword.text = getString(R.string.login_with_password)
        } else {
            textInputLayoutPassword.visibility = View.VISIBLE
            buttonLogin.text = getString(R.string.log_me_in)
            buttonLoginWithPassword.text = getString(R.string.login_with_magic_link)
        }
        isPasswordShown = !isPasswordShown
    }

    private fun login(){
        if(isPasswordShown){
            loginWithPassword(editTextEmail.text.toString(), editTextPassword.text.toString())
        } else {
            loginWithMagicLink(editTextEmail.text.toString())
        }
    }

    private fun loginWithMagicLink(email: String){
        //TODO send the email to the backend to send email
        if(verifyEmail()) {
            onMagicLinkSuccess(email)
        } else {
            onMagicLinkFailure()
        }
    }

    private fun loginWithPassword(email: String, password: String){
        if (verifyEmail() && verifyPassword()) {
            val loginJson = """
            {
                "email": "${email}",
                "password": "${password}"
            }
            """.trimIndent()

            val url = "http://10.0.2.2:3000/login"
            val chargeReqBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), loginJson)
            val request = Request.Builder()
                    .url(url)
                    .post(chargeReqBody)
                    .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    println("${e?.message}")
                }

                override fun onResponse(call: Call?, response: Response?) {
                    //charge has gone through
                    if (response?.code() == 401) {
                        onPasswordFailure()
                    } else if (response?.code() == 200) {
                        onPasswordSuccess()
                    }
                }
            })
        }
    }

    private fun onMagicLinkSuccess(email: String){
        clearErrors()
        val intent = Intent(this, MagicLinkHoldingActivity::class.java)
        intent.putExtra(MagicLinkHoldingActivity.EMAIL, email)
        startActivity(intent)
        finish()
    }

    private fun onPasswordSuccess(){
        clearErrors()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onMagicLinkFailure(){
        //TODO set error if there is a magic link error
    }

    private fun onPasswordFailure(){
        //TODO set error if there is password login error
    }

    private fun verifyEmail(): Boolean{
        return if(editTextEmail.text.isNullOrBlank()){
            textInputLayoutEmail.error = "Please enter in an email"
            false
        } else {
            textInputLayoutEmail.error = null
            true
        }
    }

    private fun verifyPassword(): Boolean{
        return if(editTextPassword.text.isNullOrBlank()){
            textInputLayoutPassword.error = "Please enter in a password"
            false
        } else {
            textInputLayoutPassword.error = null
            true
        }
    }

    private fun clearErrors(){
        textInputLayoutEmail.error = null
        textInputLayoutPassword.error = null
    }
}