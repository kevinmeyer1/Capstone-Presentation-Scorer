package com.example.scopingproject.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.scopingproject.R
import kotlinx.android.synthetic.main.activity_magic_link_sent.*

class MagicLinkHoldingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_magic_link_sent)
        if (intent != null && intent.hasExtra(EMAIL)) {
            textViewMagicLink.text = getString(R.string.magic_link_message, intent?.extras?.getString(EMAIL))
        }

        buttonOpenEmailApp.setOnClickListener {openEmailApp()}
    }

    private fun openEmailApp(){
        var emailIntent = Intent(Intent.ACTION_MAIN)
        emailIntent.addCategory(Intent.CATEGORY_APP_EMAIL)
        startActivity(emailIntent)
    }

    companion object {
        const val EMAIL = "email"
    }
}