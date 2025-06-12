plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("com.google.devtools.ksp")
    id("de.undercouch.download") version "5.5.0"
}

android {
    namespace = "com.ml.shubham0204.docqa"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ml.shubham0204.docqa"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    signingConfigs {
        create("release") {
            storeFile = file("../keystore.jks")
            storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("RELEASE_KEYSTORE_ALIAS")
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
        }
    }
    applicationVariants.configureEach {
        kotlin.sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.material3.icons.extended)
    implementation(libs.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Apache POI
    implementation(libs.apache.poi)
    implementation(libs.apache.poi.ooxml)

    // Sentence Embeddings
    // https://github.com/shubham0204/Sentence-Embeddings-Android
    implementation(files("libs/sentence_embeddings.aar"))
    implementation(libs.onnxruntime.android)

    // iTextPDF - for parsing PDFs
    implementation(libs.itextpdf)

    // ObjectBox - vector database
    debugImplementation(libs.objectbox.android.objectbrowser)
    releaseImplementation(libs.objectbox.android)

    // Gemini SDK - LLM
    implementation(libs.generativeai)

    // MediaPipe - local LLM on device
    implementation(libs.tasks.genai)

    // compose-markdown
    // https://github.com/jeziellago/compose-markdown
    implementation(libs.compose.markdown)

    // Koin dependency injection
    implementation(libs.koin.android)
    implementation(libs.koin.annotations)
    implementation(libs.koin.androidx.compose)
    ksp(libs.koin.ksp.compiler)

    // For secured/encrypted shared preferences
    implementation(libs.androidx.security.crypto)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

apply(plugin = "io.objectbox")

// download local model
val rawDir = file("src/main/res/raw")

tasks.register("ensureAssetDirExists") {
    doLast {
        if (!rawDir.exists()) {
            rawDir.mkdirs()
        }
    }
}

tasks.register<de.undercouch.gradle.tasks.download.Download>("downloadMobileBertModel") {
    description = "Downloads the MobileBERT TFLite model."
    group = "build setup" // Optional: for task organization in ./gradlew tasks

    src("https://storage.googleapis.com/mediapipe-models/text_embedder/bert_embedder/float32/1/bert_embedder.tflite")
    dest(File(rawDir, "mobile_bert.tflite"))
    overwrite(false)
    onlyIfModified(true) // Good practice: only download if remote changed or local is missing

    // Make sure the directory exists before attempting to download
    dependsOn(tasks.named("ensureAssetDirExists"))
}

tasks.named("preBuild").configure {
    //dependsOn(tasks.named("downloadMobileBertModel"))
}

