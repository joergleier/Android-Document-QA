package com.ml.shubham0204.docqa.ui.screens.edit_settings

import androidx.lifecycle.ViewModel
import com.ml.shubham0204.docqa.data.GeminiAPIKey
import com.ml.shubham0204.docqa.data.ModelSettings
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class EditSettingsViewModel(
    private val geminiAPIKey: GeminiAPIKey,
    val modelSettings: ModelSettings
) : ViewModel() {


//    private var _llmIsLocal by mutableStateOf(true)
//    val llmIsLocal:Boolean
//        get() = _llmIsLocal

    fun getAPIKey(): String? = geminiAPIKey.getAPIKey()


    fun saveAPIKey(apiKey: String) {
        geminiAPIKey.saveAPIKey(apiKey)
    }

    fun toggleLLMBackend(){
        //_llmIsLocal = !_llmIsLocal
        //localModel.isEnabled= !localModel.isEnabled
        modelSettings.switchBackend()
    }
}
