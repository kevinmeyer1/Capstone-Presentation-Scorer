package com.kevin.scopingproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.widget.*
import okhttp3.*
import java.io.IOException

class GroupActivity : AppCompatActivity() {
    val questions = arrayOf(
        "Poster content is of professional quality and indicates a mastery of the project subject material",
        "The presentation is organized, engaging, and includes a thorough description of the design and implementation of the design",
        "All team members are suitably attired, are polite, demonstrate full knowledge of material, and can answer all relevant questions",
        "The work product (model, prototype, documentation set or computer simulation) is of professional quality in all respects",
        "The team implemented novel approaches and/or solutions in the development of the project",
        "The project has the potential to enhance the reputation of the Innovative Computing Project and/or CCI/DSI",
        "The team successfully explained the scope and result of their project in no more than 5 minutes"
    )

    val scores: IntArray = intArrayOf(-1, -1, -1, -1, -1, -1, -1)
    var index = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        val btnNext = findViewById<Button>(R.id.btnNext)
        val btnPrevious = findViewById<Button>(R.id.btnPrevious)
        val rgRatings = findViewById<RadioGroup>(R.id.rgRating)


        checkButtons(index)
        setQuestion(index)

        btnNext.setOnClickListener() {
            val rbId = rgRatings.checkedRadioButtonId

            if (rbId != -1) {
                val rb = findViewById<RadioButton>(rbId)
                val strCheckRB = rb.text.toString()
                val checkedRB = strCheckRB.toInt()

                scores[index] = checkedRB

                if (index == 6) {
                    submitResults()
                } else {
                    index++
                    rgRatings.clearCheck()

                    checkButtons(index)
                    setQuestion(index)
                    checkRadioGroup(index)
                }
            } else {
                Handler(Looper.getMainLooper()).post(Runnable {
                    Toast.makeText(
                        applicationContext,
                        "Please select a score",
                        Toast.LENGTH_SHORT
                    ).show()
                })
            }
        }

        btnPrevious.setOnClickListener() {
            val rbId = rgRatings.checkedRadioButtonId

            if (rbId != -1) {
                val rb = findViewById<RadioButton>(rbId)
                val strCheckRB = rb.text.toString()
                val checkedRB = strCheckRB.toInt()

                scores[index] = checkedRB
                index--
                rgRatings.clearCheck()

                checkButtons(index)
                setQuestion(index)
                checkRadioGroup(index)
            } else {
                index--
                checkButtons(index)
                setQuestion(index)
                checkRadioGroup(index)
            }
        }
    }

    fun checkButtons(index: Int) {
        val btnNext = findViewById<Button>(R.id.btnNext)
        val btnPrevious = findViewById<Button>(R.id.btnPrevious)

        if (index == 0) {
            btnPrevious.isEnabled = false
            btnNext.isEnabled = true
        } else if (index == 6) {
            btnNext.text = "Submit"
            btnPrevious.isEnabled = true
        } else {
            btnNext.text = "Next"
            btnNext.isEnabled = true
            btnPrevious.isEnabled = true
        }
    }

    fun setQuestion(index: Int) {
        val lblQuestion = findViewById<TextView>(R.id.lblQuestion)
        val questionIndex = index + 1

        val question = questions[index]
        lblQuestion.setText("$questionIndex. $question")
    }

    fun checkRadioGroup(index: Int) {
        if (scores[index] != -1) {
            when (scores[index]) {
                0 -> findViewById<RadioButton>(R.id.rb0).isChecked = true
                1 -> findViewById<RadioButton>(R.id.rb1).isChecked = true
                2 -> findViewById<RadioButton>(R.id.rb2).isChecked = true
                3 -> findViewById<RadioButton>(R.id.rb3).isChecked = true
                4 -> findViewById<RadioButton>(R.id.rb4).isChecked = true
            }
        }
    }

    fun submitResults() {
        var totalScore = 0
        var averageScore: Double

        for (i in 0..6) {
            totalScore += scores[i]
        }

        averageScore = totalScore.toDouble() / 7
        averageScore = "%.2f".format(averageScore).toDouble()

        val txtTeam = findViewById<TextView>(R.id.txtTeam)
        val team = txtTeam.text.toString()

        if (team == "") {
            Handler(Looper.getMainLooper()).post(Runnable {
                Toast.makeText(
                    applicationContext,
                    "Please enter a team name",
                    Toast.LENGTH_SHORT
                ).show()
            })

            return
        }

        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val email = prefs.getString("email", null).toString()

        val client = OkHttpClient()

        val submitJson = """
            {
                "team": "${team}",
                "score": ${averageScore},
                "email": "${email}"
            }
            """.trimIndent()

        val url = "https://scopingproject.herokuapp.com/submit_score"
        val chargeReqBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), submitJson)
        val request = Request.Builder()
            .url(url)
            .post(chargeReqBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                println("${e?.message}")
            }

            override fun onResponse(call: Call?, response: Response?) {
                if (response?.code() == 406) {
                    Handler(Looper.getMainLooper()).post(Runnable {
                        Toast.makeText(
                            applicationContext,
                            "Please enter a valid team name",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                } else if (response?.code() == 200) {
                    Handler(Looper.getMainLooper()).post(Runnable {
                        Toast.makeText(
                            applicationContext,
                            "Score successfully submitted",
                            Toast.LENGTH_SHORT
                        ).show()
                    })

                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                } else if (response?.code() == 500) {
                    Handler(Looper.getMainLooper()).post(Runnable {
                        Toast.makeText(
                            applicationContext,
                            "There was an error while submitting your score.",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                }
            }
        })
    }
}
