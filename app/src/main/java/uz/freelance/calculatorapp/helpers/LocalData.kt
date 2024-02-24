package uz.freelance.calculatorapp.helpers

import android.content.Context
import android.content.SharedPreferences

class LocalData(val context: Context) {

    private val APP_SETTINGS = "APP_SETTINGS"

    private fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE)
    }

    fun history(): String? {
        return getSharedPreferences().getString("history", null)
    }

    fun history(newValue: String?) {
        val editor = getSharedPreferences().edit()
        editor.putString("history", newValue)
        editor.apply()
    }

}