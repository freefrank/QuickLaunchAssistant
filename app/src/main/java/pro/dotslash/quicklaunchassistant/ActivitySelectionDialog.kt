package pro.dotslash.quicklaunchassistant

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ActivitySelectionDialog(
    context: Context,
    private val appInfo: AppInfo,
    private val activities: List<ActivityInfo>,
    private val onActivitySelected: (ActivityInfo) -> Unit,
    private val onDefaultSelected: () -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_activity_selection, null)
        setContentView(view)
        
        setupViews(view)
        
        // Make dialog take up most of the screen width
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            -2 // WRAP_CONTENT
        )
    }
    
    private fun setupViews(view: View) {
        val appIconDialog: ImageView = view.findViewById(R.id.app_icon_dialog)
        val dialogTitle: TextView = view.findViewById(R.id.dialog_title)
        val dialogSubtitle: TextView = view.findViewById(R.id.dialog_subtitle)
        val activitiesRecyclerView: RecyclerView = view.findViewById(R.id.activities_recycler_view)
        val btnCancel: MaterialButton = view.findViewById(R.id.btn_cancel)
        val btnUseDefault: MaterialButton = view.findViewById(R.id.btn_use_default)
        
        // Setup header
        appIconDialog.setImageDrawable(appInfo.icon)
        dialogTitle.text = "Select Activity"
        dialogSubtitle.text = "${appInfo.name} • ${activities.size} activities"
        
        // Setup RecyclerView
        activitiesRecyclerView.layoutManager = LinearLayoutManager(context)
        activitiesRecyclerView.adapter = ActivitySelectionAdapter(activities) { activity ->
            onActivitySelected(activity)
            dismiss()
        }
        
        // Setup buttons
        btnCancel.setOnClickListener { dismiss() }
        btnUseDefault.setOnClickListener {
            onDefaultSelected()
            dismiss()
        }
    }
    
    companion object {
        fun show(
            context: Context,
            appInfo: AppInfo,
            activities: List<ActivityInfo>,
            onActivitySelected: (ActivityInfo) -> Unit,
            onDefaultSelected: () -> Unit
        ) {
            ActivitySelectionDialog(
                context,
                appInfo,
                activities,
                onActivitySelected,
                onDefaultSelected
            ).show()
        }
    }
}