plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

fun getGitTag(): String {
    return try {
        val process = ProcessBuilder("git", "describe", "--tags", "--always", "--dirty")
            .directory(rootDir)
            .start()
        val result = process.inputStream.bufferedReader().readText().trim()
        if (result.isEmpty()) "dev" else result
    } catch (e: Exception) {
        "dev"
    }
}

fun getVersionCode(): Int {
    return try {
        val process = ProcessBuilder("git", "rev-list", "--count", "HEAD")
            .directory(rootDir)
            .start()
        val result = process.inputStream.bufferedReader().readText().trim()
        if (result.isEmpty()) 1 else result.toInt()
    } catch (e: Exception) {
        1
    }
}

android {
    namespace = "pro.dotslash.quicklaunchassistant"
    compileSdk = 34

    defaultConfig {
        applicationId = "pro.dotslash.quicklaunchassistant"
        minSdk = 24
        targetSdk = 34
        versionCode = getVersionCode()
        versionName = getGitTag()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
