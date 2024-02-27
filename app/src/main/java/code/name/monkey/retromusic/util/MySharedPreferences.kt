package code.name.monkey.retromusic.util

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences private constructor(context: Context) {
    private val PREF_NAME = "MyPrefs"
    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    companion object {
        private var instance: MySharedPreferences? = null

        @Synchronized
        fun getInstance(context: Context): MySharedPreferences {
            if (instance == null) {
                instance = MySharedPreferences(context)
            }
            return instance!!
        }
    }

    fun putString(key: String, value: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String, defaultValue: String): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    fun putInt(key: String, value: Int) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }
}