package pro.dotslash.quicklaunchassistant

import android.app.assist.AssistContent
import android.app.assist.AssistStructure
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.service.voice.VoiceInteractionService
import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionSessionService

class AssistantService : VoiceInteractionService() {
    override fun onReady() {
        super.onReady()
    }
}

class AssistantSessionService : VoiceInteractionSessionService() {
    override fun onNewSession(args: Bundle?): VoiceInteractionSession {
        return AssistantSession(this)
    }
}

class AssistantSession(private val context: AssistantSessionService) : VoiceInteractionSession(context) {

    override fun onHandleAssist(data: Bundle?, structure: AssistStructure?, content: AssistContent?) {
        super.onHandleAssist(data, structure, content)

        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val packageName = prefs.getString("selected_app", null)

        if (packageName != null) {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
        finish()
    }
}
