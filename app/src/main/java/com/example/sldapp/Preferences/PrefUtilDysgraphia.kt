package com.example.sldapp.Preferences

import android.content.Context
import androidx.preference.PreferenceManager

class PrefUtilDysgraphia {
    companion object{

        private const val FIRST_CLICKED_ID = "com.example.sideapp_first_clicked"

        fun getFirstClicked(context: Context) : String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(FIRST_CLICKED_ID, "")!!
        }

        fun setFirstClicked(text : String, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit().putString(
                FIRST_CLICKED_ID, text)
            editor.apply()
        }

        private const val SECOND_CLICKED_ID = "com.example.sideapp_second_clicked"

        fun getSecondClicked(context: Context) : String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(SECOND_CLICKED_ID, "")!!
        }

        fun setSecondClicked(text : String, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit().putString(
                SECOND_CLICKED_ID, text)
            editor.apply()
        }

        private const val HIDE_LETTER_ID = "com.example.sideapp_hide_letter"

        fun getHideLetter(context: Context) : Boolean {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getBoolean(HIDE_LETTER_ID, false)!!
        }

        fun setHideLetter(text : Boolean, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(
                HIDE_LETTER_ID, text)
            editor.apply()
        }
    }
}