package com.kevin.scopingproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.widget.TextView
import android.widget.Toast
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class OverallRankingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overall_rankings)

        val lblScores = findViewById<TextView>(R.id.lblScores)
        val lblTeams = findViewById<TextView>(R.id.lblTeams)

        var scoresString : String = ""
        var teamsString : String = ""

        val client = OkHttpClient()

        val url = "http://10.0.2.2:3000/overall_scores"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                println("${e?.message}")
            }

            override fun onResponse(call: Call?, response: Response?) {
                if (response?.code() == 200) {

                    val body = response.body()!!.string()

                    try {
                        val jsonResponseData = JSONArray(body)

                        var jsonList = ArrayList<JSONObject>()

                        if (jsonResponseData.length() > 0) {
                            for (i in 0 until jsonResponseData.length()) {
                                jsonList.add(jsonResponseData.getJSONObject(i))
                            }

                            Collections.sort(jsonList, object : Comparator<JSONObject> {

                                override fun compare(a: JSONObject, b: JSONObject): Int {
                                    var valA = String()
                                    var valB = String()

                                    try {
                                        valA = a.getDouble("score").toString()
                                        valB = b.getDouble("score").toString()
                                    } catch (e: JSONException) {

                                    }

                                    return valA.compareTo(valB)
                                }
                            })

                            jsonList.reverse()

                            for (i in 0 until jsonList.size) {
                                teamsString += (i + 1).toString() + ". "
                                teamsString += jsonList.get(i).getString("team")
                                teamsString += ":\n"

                                scoresString += jsonList.get(i).getDouble("score")
                                scoresString += "\n"
                            }
                        } else {
                            teamsString = "No scores have been added"
                        }
                    } catch (e: JSONException) {
                        println(e.localizedMessage)
                    }

                    lblTeams.text = teamsString
                    lblScores.text = scoresString


                } else if (response?.code() == 500) {
                    Handler(Looper.getMainLooper()).post(Runnable {
                        Toast.makeText(
                            applicationContext,
                            "There was an error while gathering scores",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                }
            }
        })

    }
}
