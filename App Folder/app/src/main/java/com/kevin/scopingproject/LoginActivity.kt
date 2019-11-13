package com.kevin.scopingproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import java.io.IOException
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val client = OkHttpClient()

        var intent = intent

        if (intent != null && intent.data != null) {
            var data : Uri = intent.data
            var token : String = data.getQueryParameter("token")

            val linkJson = """
            {
                "token": "${token}"
            }
            """.trimIndent()

            val url = "https://scopingproject.herokuapp.com/verifyToken"
            val chargeReqBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), linkJson)
            val request = Request.Builder()
                .url(url)
                .post(chargeReqBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    println("${e?.message}")
                }

                override fun onResponse(call: Call?, response: Response?) {
                    if (response?.code() == 401) {
                        Handler(Looper.getMainLooper()).post(Runnable {
                            Toast.makeText(
                                applicationContext,
                                "There was an error validating your token",
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                    } else if (response?.code() == 200) {
                        val jsonResponse = JSONObject(response?.body()!!.string())
                        val jsonEmail = jsonResponse.getString("email")

                        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                        val editor = prefs.edit()
                        editor.putString("email", jsonEmail)
                        editor.commit()

                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            })

        }



        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGetLink = findViewById<Button>(R.id.btnInfo)

        btnLogin.setOnClickListener {
            val email = txtUsername.text.toString()
            val password = txtPassword.text.toString()

            val loginJson = """
            {
                "email": "${email}",
                "password": "${password}"
            }
            """.trimIndent()

            val url = "https://scopingproject.herokuapp.com/login"
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
                    if (response?.code() == 401) {
                        Handler(Looper.getMainLooper()).post(Runnable {
                            Toast.makeText(
                                applicationContext,
                                "Unauthorized email/password",
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                    } else if (response?.code() == 200) {
                        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                        val editor = prefs.edit()
                        editor.putString("email", email)
                        editor.commit()

                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            })

        }

        btnGetLink.setOnClickListener() {
            val email = txtUsername.text.toString()

            if (email == "") {
                Handler(Looper.getMainLooper()).post(Runnable {
                    Toast.makeText(
                        applicationContext,
                        "Enter an email address in the field to get login information",
                        Toast.LENGTH_SHORT
                    ).show()
                })
            } else {
                val loginJson = """
                {
                    "email": "${email}"
                }
                """.trimIndent()

                val url = "https://scopingproject.herokuapp.com/request_login_information"
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
                        if (response?.code() == 401) {
                            Handler(Looper.getMainLooper()).post(Runnable {
                                Toast.makeText(
                                    applicationContext,
                                    "This email does not have the authorization to become a scorer",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                        } else if (response?.code() == 200) {
                            Handler(Looper.getMainLooper()).post(Runnable {
                                Toast.makeText(
                                    applicationContext,
                                    "An email with instructions has been sent to the provided email address",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                        }
                    }
                })
            }
        }
    }
}
