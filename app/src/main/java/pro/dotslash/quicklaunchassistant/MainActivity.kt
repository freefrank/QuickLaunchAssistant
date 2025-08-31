package pro.dotslash.quicklaunchassistant

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val apps = getInstalledApps()
        val adapter = AppAdapter(apps) { appInfo ->
            val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("selected_app", appInfo.packageName).apply()
            finish()
        }
        recyclerView.adapter = adapter
    }

    private fun getInstalledApps(): List<AppInfo> {
        val pm: PackageManager = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val allApps = pm.queryIntentActivities(intent, 0)
        return allApps.map {
            AppInfo(
                it.loadLabel(pm).toString(),
                it.loadIcon(pm),
                it.activityInfo.packageName
            )
        }
    }
}
