package com.example.scopingproject.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.scopingproject.R

class SplashScreenActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if(intent != null && intent.data != null){
            magicLinkScreen(intent?.data?.path.toString())
        } else {
            loginScreen()
        }
    }

    private fun loginScreen(){
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun magicLinkScreen(path: String){
        val magicLinkIntent = Intent(this, MagicLinkLogin::class.java)
        magicLinkIntent.putExtra(MagicLinkLogin.PATH, path)
        startActivity(magicLinkIntent)
        finish()
    }
}