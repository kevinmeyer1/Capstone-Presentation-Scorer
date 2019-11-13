package com.example.scopingproject.models

data class Score(var scores: ArrayList<Int>){
    fun getCurrentScore(): String{
        return if(scores.size == 0){
            "0.00"
        } else {
            var totalScore = 0.00
            for(score in scores){
                totalScore += score.toDouble()
            }
            String.format("%.2f", totalScore/scores.size.toDouble())
        }
    }
}