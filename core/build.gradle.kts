import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

val appMode: Boolean = gradleLocalProperties(rootDir, providers).getProperty("APP_MODE") == "dev"
val productionMode: Boolean = gradleLocalProperties(rootDir, providers).getProperty("PRODUCTION_MODE") == "true"
val apiUrl: String = gradleLocalProperties(rootDir, providers).getProperty("API_URL")
val apiUrlProd: String = gradleLocalProperties(rootDir, providers).getProperty("API_URL_PROD")

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-parcelize")
}

apply(from = "../shared_dependencies.gradle")

android {
    namespace = "com.singa.core"
    compileSdk = 34

    defaultConfig {
        minSdk = 27



        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "BASE_URL", "\"$apiUrl\"")
        buildConfigField("String", "BASE_URL_PROD", "\"$apiUrlProd\"")
        buildConfigField("Boolean", "PRODUCTION_MODE", "$productionMode")
        buildConfigField("Boolean", "APP_MODE", "$appMode")
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
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}