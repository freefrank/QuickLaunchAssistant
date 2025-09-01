package pro.dotslash.quicklaunchassistant

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {
    private const val PREFS_NAME = "prefs"
    private const val KEY_SELECTED_APP = "selected_app"
    private const val KEY_SELECTED_ACTIVITY = "selected_activity"
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun saveSelectedApp(context: Context, packageName: String, className: String? = null) {
        val prefs = getPrefs(context)
        with(prefs.edit()) {
            putString(KEY_SELECTED_APP, packageName)
            if (className != null) {
                putString(KEY_SELECTED_ACTIVITY, className)
            } else {
                remove(KEY_SELECTED_ACTIVITY)
            }
            apply()
        }
    }
    
    fun getSelectedApp(context: Context): String? {
        return getPrefs(context).getString(KEY_SELECTED_APP, null)
    }
    
    fun getSelectedActivity(context: Context): String? {
        return getPrefs(context).getString(KEY_SELECTED_ACTIVITY, null)
    }
    
    fun getSelectedAppInfo(context: Context): Pair<String?, String?> {
        val prefs = getPrefs(context)
        return Pair(
            prefs.getString(KEY_SELECTED_APP, null),
            prefs.getString(KEY_SELECTED_ACTIVITY, null)
        )
    }
    
    fun clearSelection(context: Context) {
        val prefs = getPrefs(context)
        with(prefs.edit()) {
            remove(KEY_SELECTED_APP)
            remove(KEY_SELECTED_ACTIVITY)
            apply()
        }
    }
}