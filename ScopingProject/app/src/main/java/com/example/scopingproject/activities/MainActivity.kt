package com.example.scopingproject.activities

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.view.MenuItem

import com.example.scopingproject.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottom_navigation_bar.setOnNavigationItemSelectedListener { p0 ->
            when(p0.itemId){
                R.id.tabGlobalScores -> setFragment(ScoreboardFragment)
                R.id.tabYourScores -> setFragment(ScorebboardFragment)
            }
            true
        }
    }
}
