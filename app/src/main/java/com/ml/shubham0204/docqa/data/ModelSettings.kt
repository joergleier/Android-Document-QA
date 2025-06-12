package com.ml.shubham0204.docqa.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.koin.core.annotation.Single

@Single
class ModelSettings {
    //public var isEnabled = true
    // if true then local LLM (gemma) is used as Backend, else cloud LLM (gemini)
    private var _llmIsLocal by mutableStateOf(true)
    val llmIsLocal:Boolean
        get() = _llmIsLocal

    fun switchBackend(){
        _llmIsLocal = !_llmIsLocal
    }
}