package com.weatherdemo.util

import java.text.SimpleDateFormat
import java.util.regex.Pattern

class Util {

    companion object {
        fun isEmailValid(email: String): Boolean {
            val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
            val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(email)
            return matcher.matches()
        }

        fun isPrimeNumber(number: Int): Boolean {
            for (i in 2..number / 2) {
                if (number % i == 0) {
                    return false
                }
            }
            return true
        }
        fun getDay(input: String): String? {
            val inFormat = SimpleDateFormat("yyyy-MM-dd")
            val date = inFormat.parse(input)
            val outFormat: SimpleDateFormat = SimpleDateFormat("EEEE")
            val goal: String = outFormat.format(date)

            return goal
        }
    }


}