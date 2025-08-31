package pro.dotslash.quicklaunchassistant

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val priorityPackageNames = listOf(
        "com.openai.chatgpt",
        "com.anthropic.claude",
        "com.deepseek.chat",
        "com.larus.nova"
    )

    private lateinit var allApps: List<AppInfo>
    private lateinit var adapter: AppAdapter
    private lateinit var searchEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val loadingLayout: LinearLayout = findViewById(R.id.loadingLayout)
        val circularProgress: CircularProgressIndicator = findViewById(R.id.circularProgress)
        val loadingText: TextView = findViewById(R.id.loadingText)
        val progressText: TextView = findViewById(R.id.progressText)
        searchEditText = findViewById(R.id.searchEditText)

        recyclerView.layoutManager = LinearLayoutManager(this)
        
        // Setup search functionality
        setupSearch()

        // Load apps with progress tracking
        lifecycleScope.launch {
            loadingLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

            allApps = withContext(Dispatchers.IO) {
                getInstalledAppsWithProgress(circularProgress, progressText)
            }

            adapter = AppAdapter(allApps) { appInfo ->
                val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
                prefs.edit().putString("selected_app", appInfo.packageName).apply()
                finish()
            }
            recyclerView.adapter = adapter

            // Smooth transition
            loadingLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterApps(s.toString())
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    private fun filterApps(query: String) {
        if (!::allApps.isInitialized || !::adapter.isInitialized) return
        
        val filteredApps = if (query.isBlank()) {
            allApps
        } else {
            allApps.filter { app ->
                app.name.contains(query, ignoreCase = true) ||
                app.packageName.contains(query, ignoreCase = true)
            }
        }
        
        adapter.updateApps(filteredApps)
    }

    private suspend fun getInstalledAppsWithProgress(
        progressIndicator: CircularProgressIndicator,
        progressText: TextView
    ): List<AppInfo> {
        val pm: PackageManager = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfos = pm.queryIntentActivities(intent, 0)
        
        val totalApps = resolveInfos.size
        val apps = mutableListOf<AppInfo>()
        
        withContext(Dispatchers.Main) {
            progressIndicator.max = totalApps
            progressText.text = "0 / $totalApps apps loaded"
        }
        
        resolveInfos.forEachIndexed { index, resolveInfo ->
            val appInfo = AppInfo(
                resolveInfo.loadLabel(pm).toString(),
                resolveInfo.loadIcon(pm),
                resolveInfo.activityInfo.packageName
            )
            apps.add(appInfo)
            
            // Update progress on main thread
            withContext(Dispatchers.Main) {
                progressIndicator.setProgressCompat(index + 1, true)
                progressText.text = "${index + 1} / $totalApps apps loaded"
            }
            
            // Small delay for smooth animation (only for first few to avoid too long loading)
            if (index < 10) {
                delay(50)
            }
        }

        val (priorityApps, otherApps) = apps.partition { priorityPackageNames.contains(it.packageName) }
        return priorityApps.sortedBy { it.name } + otherApps.sortedBy { it.name }
    }
}
