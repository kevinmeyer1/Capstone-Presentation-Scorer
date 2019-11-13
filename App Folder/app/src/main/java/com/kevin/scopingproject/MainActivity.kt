package com.kevin.scopingproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnNewRating = findViewById<Button>(R.id.btnNewRating)
        val btnOverallRankings = findViewById<Button>(R.id.btnOverallRankings)
        val btnPersonalRatings = findViewById<Button>(R.id.btnPersonalRatings)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnNewRating.setOnClickListener {
            val intent = Intent(applicationContext, GroupActivity::class.java)
            startActivity(intent)
        }

        btnOverallRankings.setOnClickListener {
            val intent = Intent(applicationContext, OverallRankingsActivity::class.java)
            startActivity(intent)
        }

        btnPersonalRatings.setOnClickListener {
            val intent = Intent(applicationContext, PersonalRankingsActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val editor = prefs.edit()
            editor.remove("email")
            editor.commit()

            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}
