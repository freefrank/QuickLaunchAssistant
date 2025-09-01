package pro.dotslash.quicklaunchassistant

import android.graphics.drawable.Drawable

data class ActivityInfo(
    val name: String,
    val className: String,
    val packageName: String,
    val icon: Drawable? = null,
    val isDefault: Boolean = false
)