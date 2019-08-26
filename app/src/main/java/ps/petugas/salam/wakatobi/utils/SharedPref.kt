package ps.petugas.salam.wakatobi.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * ----------------------------------------------
 * Created by ukieTux on 12/23/16 with ♥ .
 * @email : ukie.tux@gmail.com
 * @github : https://www.github.com/tuxkids
 * * ----------------------------------------------
 * * © 2016 | All Rights Reserved
 */

object SharedPref {

    private val TAG = "SharedPref"

    val pref: SharedPreferences
        get() {
            val context: Context? = AppController.context
            return PreferenceManager.getDefaultSharedPreferences(context)
        }

    fun saveString(key: String, value: String) {
        //        Logger.d(TAG+ "saveString: " + value);
        pref.edit()
                .putString(key, value)
                .apply()
    }

    fun getString(key: String): String {
        //        Logger.d(TAG+ "getString: " + getPref().getString(key, null));
        return pref.getString(key, null)
    }


    fun deleteString(key: String) {
        pref.edit()
                .remove(key)
                .apply()
    }

    fun saveInt(key: String, value: Int) {
        //        Logger.d(TAG + "saveInt: " + value);
        pref.edit()
                .putInt(key, value)
                .apply()
    }

    fun getInt(key: String): Int {
        return pref.getInt(key, 0)
    }

    fun deleteInt(key: String) {
        pref.edit()
                .remove(key)
                .apply()
    }

    fun saveBol(key: String, value: Boolean) {
        pref.edit()
                .putBoolean(key, value)
                .apply()
    }

    fun getBol(key: String): Boolean {
        return pref.getBoolean(key, false)
    }

    fun deleteBol(key: String) {
        pref.edit()
                .remove(key)
                .apply()
    }

    fun clearAll() {
        pref.edit().clear().apply()
    }

}
