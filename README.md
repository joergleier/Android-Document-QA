# Document Question-Answering with Local RAG in Android (Fork)

This is a fork from [this project](https://github.com/shubham0204/Android-Document-QA).
Please check it out for further reference.

Many thanks to the [original author](https://github.com/shubham0204) for sharing his work. :pray: :blush:

I added the following features to this fork:
- LLM On-Device with the help of [MediaPipe's LLM Inference API](https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference/android).


| Feature            | On-Device | Remote |
|--------------------|-----------|--------|
| Sentence Embedding | ✅         |        |
| Text Splitter      | ✅         |        |
| Vector Database    | ✅         |        |
| LLM                | ✅         | ✅      |


# Setup
- Clone this repo and open it in Android Studio.
- Download Gemma3 from [Kaggle](https://www.kaggle.com/models/google/gemma-3/tfLite?select=gemma3-1B-it-int4.task)  or Huggingface.
- Push the gemma3-1B-it-int4.task model file to your device with adb.
```bash
$> adb push gemma3-1B-it-int4.task /data/local/tmp/llm
```
- Run the app.
- (optional) Go to Settings (gear in the top right corner) and choose backend (cloud or local).
- Add document(s).
- Ask questions about the documents.

# Limitations
Be aware that local LLM inference on Android devices has some limitations.
Due to hardware limitations only models with up to 1B parameters can be used so far.
Those models are very limited by their capabilities compared to their cloud counterparts.


