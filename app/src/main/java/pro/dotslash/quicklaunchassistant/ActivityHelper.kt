package pro.dotslash.quicklaunchassistant

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo

object ActivityHelper {
    
    fun getAppActivities(context: Context, packageName: String): List<ActivityInfo> {
        val pm = context.packageManager
        val activities = mutableListOf<ActivityInfo>()
        
        try {
            // Get all activities for the package
            val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            val appInfo = pm.getApplicationInfo(packageName, 0)
            
            // Get main launcher activity
            val launcherIntent = pm.getLaunchIntentForPackage(packageName)
            val defaultActivityName = launcherIntent?.component?.className
            
            // Query all activities that can be launched
            val intent = Intent(Intent.ACTION_MAIN)
            intent.setPackage(packageName)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            
            val resolveInfos = pm.queryIntentActivities(intent, 0)
            val launcherActivities = resolveInfos.map { it.activityInfo.name }.toSet()
            
            // Also check for other common entry points
            val alternativeIntents = listOf(
                Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_DEFAULT) },
                Intent(Intent.ACTION_VIEW),
                Intent(Intent.ACTION_SEND).apply { type = "text/plain" }
            )
            
            val additionalActivities = mutableSetOf<String>()
            alternativeIntents.forEach { alternativeIntent ->
                alternativeIntent.setPackage(packageName)
                val altResolveInfos = pm.queryIntentActivities(alternativeIntent, 0)
                additionalActivities.addAll(altResolveInfos.map { it.activityInfo.name })
            }
            
            // Combine all discoverable activities
            val allDiscoverableActivities = launcherActivities + additionalActivities
            
            packageInfo.activities?.forEach { activityInfo ->
                if (activityInfo.exported || allDiscoverableActivities.contains(activityInfo.name)) {
                    try {
                        val activityLabel = activityInfo.loadLabel(pm).toString()
                        val isDefault = activityInfo.name == defaultActivityName
                        
                        // Try to get activity icon, fallback to app icon
                        val icon = try {
                            activityInfo.loadIcon(pm)
                        } catch (e: Exception) {
                            appInfo.loadIcon(pm)
                        }
                        
                        activities.add(
                            ActivityInfo(
                                name = activityLabel.ifEmpty { activityInfo.name.substringAfterLast('.') },
                                className = activityInfo.name,
                                packageName = packageName,
                                icon = icon,
                                isDefault = isDefault
                            )
                        )
                    } catch (e: Exception) {
                        // Skip this activity if we can't load its info
                    }
                }
            }
            
            // Sort activities: default first, then alphabetically
            activities.sortWith(compareBy<ActivityInfo> { !it.isDefault }.thenBy { it.name })
            
        } catch (e: Exception) {
            // If we can't get detailed activities, at least try to get the default launcher
            val launcherIntent = pm.getLaunchIntentForPackage(packageName)
            if (launcherIntent?.component != null) {
                val appInfo = pm.getApplicationInfo(packageName, 0)
                activities.add(
                    ActivityInfo(
                        name = "Default",
                        className = launcherIntent.component!!.className,
                        packageName = packageName,
                        icon = appInfo.loadIcon(pm),
                        isDefault = true
                    )
                )
            }
        }
        
        return activities.distinctBy { it.className }
    }
    
    fun createActivityIntent(packageName: String, className: String): Intent {
        val intent = Intent()
        intent.setClassName(packageName, className)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return intent
    }
}