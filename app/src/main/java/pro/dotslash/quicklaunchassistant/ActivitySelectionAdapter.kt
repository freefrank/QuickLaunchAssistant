package pro.dotslash.quicklaunchassistant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ActivitySelectionAdapter(
    private val activities: List<ActivityInfo>,
    private val onActivityClick: (ActivityInfo) -> Unit
) : RecyclerView.Adapter<ActivitySelectionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val activityIcon: ImageView = view.findViewById(R.id.activity_icon)
        val activityName: TextView = view.findViewById(R.id.activity_name)
        val activityClassName: TextView = view.findViewById(R.id.activity_class_name)
        val defaultBadge: TextView = view.findViewById(R.id.default_badge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = activities[position]
        
        activity.icon?.let { holder.activityIcon.setImageDrawable(it) }
        holder.activityName.text = activity.name
        holder.activityClassName.text = activity.className.substringAfterLast('.')
        
        holder.defaultBadge.visibility = if (activity.isDefault) View.VISIBLE else View.GONE
        
        holder.itemView.setOnClickListener {
            onActivityClick(activity)
        }
    }

    override fun getItemCount() = activities.size
}