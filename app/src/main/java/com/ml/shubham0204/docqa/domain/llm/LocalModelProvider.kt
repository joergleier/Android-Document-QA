package com.ml.shubham0204.docqa.domain.llm

import android.content.Context
import com.google.common.util.concurrent.ListenableFuture
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import org.koin.core.annotation.Single

@Single
class LocalModelProvider(private val context: Context) {
    // Set the configuration options for the LLM Inference task
    private val taskOptions: LlmInference.LlmInferenceOptions =
        LlmInference.LlmInferenceOptions.builder()
            .setModelPath("/data/local/tmp/llm/gemma3-1b-it-int4.task")
            .setMaxTopK(64)
            .setPreferredBackend(LlmInference.Backend.GPU)
            .build()

    private var llmInference: LlmInference? = null

    // Create an instance of the LLM Inference task
    //private val llmInference: LlmInference = LlmInference.createFromOptions(context, taskOptions)

    suspend fun initialize(){
        llmInference = LlmInference.createFromOptions(context, taskOptions)
    }

    suspend fun generateResponse(input: String): String {
        return llmInference?.generateResponse(input) ?: ""
    }

    suspend fun generateResponseAsync(input: String): ListenableFuture<String>? {
        return llmInference?.generateResponseAsync(input)
    }
}
