package pro.dotslash.quicklaunchassistant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AssistantActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val (packageName, className) = PreferenceHelper.getSelectedAppInfo(this)

        if (packageName != null) {
            val intent = if (className != null) {
                // Launch specific activity
                ActivityHelper.createActivityIntent(packageName, className)
            } else {
                // Launch default activity
                packageManager.getLaunchIntentForPackage(packageName)?.apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
            }
            
            if (intent != null) {
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    // If specific activity fails, try launching the default
                    if (className != null) {
                        val fallbackIntent = packageManager.getLaunchIntentForPackage(packageName)
                        if (fallbackIntent != null) {
                            fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(fallbackIntent)
                        }
                    }
                }
            }
        }
        finish()
    }
}
