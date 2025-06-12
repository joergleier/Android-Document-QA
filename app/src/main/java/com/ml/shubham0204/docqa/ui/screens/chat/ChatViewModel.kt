package com.ml.shubham0204.docqa.ui.screens.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ml.shubham0204.docqa.data.ChunksDB
import com.ml.shubham0204.docqa.data.DocumentsDB
import com.ml.shubham0204.docqa.data.GeminiAPIKey
import com.ml.shubham0204.docqa.data.ModelSettings
import com.ml.shubham0204.docqa.data.RetrievedContext
import com.ml.shubham0204.docqa.domain.embeddings.SentenceEmbeddingProvider
import com.ml.shubham0204.docqa.domain.llm.GeminiRemoteAPI
import com.ml.shubham0204.docqa.domain.llm.LocalModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ChatViewModel(
    private val documentsDB: DocumentsDB,
    private val chunksDB: ChunksDB,
    private val geminiAPIKey: GeminiAPIKey,
    private val sentenceEncoder: SentenceEmbeddingProvider,
    private val localModelProvider: LocalModelProvider,
    private val modelSettings: ModelSettings

) : ViewModel() {
    private val _questionState = MutableStateFlow("")
    val questionState: StateFlow<String> = _questionState

    private val _responseState = MutableStateFlow("")
    val responseState: StateFlow<String> = _responseState

    private val _isGeneratingResponseState = MutableStateFlow(false)
    val isGeneratingResponseState: StateFlow<Boolean> = _isGeneratingResponseState

    private val _retrievedContextListState = MutableStateFlow(emptyList<RetrievedContext>())
    val retrievedContextListState: StateFlow<List<RetrievedContext>> = _retrievedContextListState

    init{
        viewModelScope.launch(Dispatchers.IO){
            localModelProvider.initialize()
        }
    }

    fun getAnswer(
        query: String,
        prompt: String,
    ) {
            _isGeneratingResponseState.value = true
            _questionState.value = query
            try {
                var jointContext = ""
                val retrievedContextList = ArrayList<RetrievedContext>()
                val queryEmbedding = sentenceEncoder.encodeText(query)
                chunksDB.getSimilarChunks(queryEmbedding, n = 1).forEach {
                    jointContext += " " + it.second.chunkData
                    retrievedContextList.add(
                        RetrievedContext(
                            it.second.docFileName,
                            it.second.chunkData
                        )
                    )
                }
                val inputPrompt =
                    prompt.replace("\$CONTEXT", jointContext).replace("\$QUERY", query)
                CoroutineScope(Dispatchers.IO).launch {


                    if (modelSettings.llmIsLocal){
                        // local inference
                        localModelProvider.generateResponse(inputPrompt).let { llmResponse ->
                            _responseState.value = llmResponse
                            _isGeneratingResponseState.value = false
                            _retrievedContextListState.value = retrievedContextList
                            Log.d("chatviewmodelexception", "Model Result: $llmResponse")
                        }
                        Log.d("chatviewmodelexception", "Model Prompt: $inputPrompt")
                        //val res = localModelProvider.generateResponse(inputPrompt)

                        //Log.d("chatviewmodelexception", "Model Result: $res")

                    }else {
                        // cloud inference
                        val apiKey = geminiAPIKey.getAPIKey() ?: throw Exception("Gemini API key is null")
                        val geminiRemoteAPI = GeminiRemoteAPI(apiKey)

                        geminiRemoteAPI.getResponse(inputPrompt)?.let { llmResponse ->
                            _responseState.value = llmResponse
                            _isGeneratingResponseState.value = false
                            _retrievedContextListState.value = retrievedContextList
                        }
                    }

                }
            } catch (e: Exception) {
                _isGeneratingResponseState.value = false
                _questionState.value = ""
                Log.d("chatviewmodelexception", "getAnswer: ${e.message}")
                throw e
            }
       // }
    }


    fun checkNumDocuments(): Boolean = documentsDB.getDocsCount() > 0

    fun checkValidAPIKey(): Boolean = geminiAPIKey.getAPIKey() != null || modelSettings.llmIsLocal

}
