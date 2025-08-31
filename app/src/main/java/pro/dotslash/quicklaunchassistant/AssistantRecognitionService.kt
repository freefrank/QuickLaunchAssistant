package pro.dotslash.quicklaunchassistant

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionService

// This is a stub RecognitionService. The main logic is in the AssistantSession.
// A RecognitionService is required by the voice-interaction-service declaration,
// but we don't need to implement any recognition features for this app's purpose.
class AssistantRecognitionService : RecognitionService() {
    override fun onStartListening(recognizerIntent: Intent?, listener: Callback?) {}
    override fun onCancel(listener: Callback?) {}
    override fun onStopListening(listener: Callback?) {}
}
