package pro.dotslash.quicklaunchassistant

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val priorityPackageNames = listOf(
        "com.openai.chatgpt",
        "com.anthropic.claude",
        "com.deepseek.chat",
        "com.larus.nova"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

            val apps = withContext(Dispatchers.IO) {
                getInstalledApps()
            }

            val adapter = AppAdapter(apps) { appInfo ->
                val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
                prefs.edit().putString("selected_app", appInfo.packageName).apply()
                finish()
            }
            recyclerView.adapter = adapter

            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun getInstalledApps(): List<AppInfo> {
        val pm: PackageManager = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val allApps = pm.queryIntentActivities(intent, 0).map {
            AppInfo(
                it.loadLabel(pm).toString(),
                it.loadIcon(pm),
                it.activityInfo.packageName
            )
        }

        val (priorityApps, otherApps) = allApps.partition { priorityPackageNames.contains(it.packageName) }

        return priorityApps.sortedBy { it.name } + otherApps.sortedBy { it.name }
    }
}
