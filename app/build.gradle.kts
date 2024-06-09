import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

val appMode: Boolean = gradleLocalProperties(rootDir, providers).getProperty("APP_MODE") == "dev"
val productionMode: Boolean = gradleLocalProperties(rootDir, providers).getProperty("PRODUCTION_MODE") == "true"
val apiUrl: String = gradleLocalProperties(rootDir, providers).getProperty("API_URL")
val apiUrlProd: String = gradleLocalProperties(rootDir, providers).getProperty("API_URL_PROD")
val articleUrl: String = gradleLocalProperties(rootDir, providers).getProperty("ARTICLE_URL")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

project.ext.set("ASSET_DIR", "$projectDir/src/main/assets")
apply(from = "../shared_dependencies.gradle")

android {
    namespace = "com.singa.asl"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.singa.asl"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "BASE_URL", "\"$apiUrl\"")
        buildConfigField("String", "BASE_URL_PROD", "\"$apiUrlProd\"")
        buildConfigField("Boolean", "PRODUCTION_MODE", "$productionMode")
        buildConfigField("Boolean", "APP_MODE", "$appMode")
        buildConfigField("String", "ARTICLE_URL", "\"$articleUrl\"")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
        mlModelBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":core"))

    implementation(libs.mobile.ffmpeg.full.gpl)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.tensorflow.lite.gpu)
    implementation(libs.androidx.media3.exoplayer.hls)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Camerax implementation
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
   
}