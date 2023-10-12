plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") // KSP support
    //id("com.google.protobuf") version "0.9.4" // Added for Proto DataStore
}

val protobufVersion = "3.24.0"
val roomVersion = "2.5.2"
val workVersion = "2.8.1"

android {
    namespace = "com.blockyheadman.arcoscompanion"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.blockyheadman.arcoscompanion"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        //kotlinCompilerExtensionVersion = "1.4.3"
        kotlinCompilerExtensionVersion = "1.5.3" // Updated Kotlin Compiler
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    // Protobuf setup for Proto Datastore
    /*sourceSets {
        protobuf {
            protoc {
                artifact = "com.google.protobuf:protoc:$protobufVersion"
            }
            generateProtoTasks {
                all().forEach { task ->
                    task.builtins {
                        register("java") {
                            option("lite")
                        }
                        register("kotlin") {
                            option("lite")
                        }
                    }
                }
            }
        }
    }*/
}

dependencies {

    //implementation("androidx.core:core-ktx:1.9.0") // OG version in case things go wrong
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2023.09.02")) // og ver: 2023.03.00
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.2.0-alpha09") // Remove version if unstable

    implementation("androidx.core:core-splashscreen:1.0.1") // Added for Splash Screen
    implementation("androidx.datastore:datastore-preferences:1.0.0") // Added for Preferences DataStore
    //implementation("androidx.datastore:datastore:1.0.0") // Added for Proto DataStore
    //implementation("com.google.protobuf:protobuf-kotlin-lite:$protobufVersion") // Added for Proto DataStore
    //implementation("com.google.protobuf:protoc:$protobufVersion") // Added for Proto DataStore
    implementation("androidx.activity:activity-ktx:1.8.0") // Added for permissions
    implementation("androidx.fragment:fragment-ktx:1.6.1") // Added for permissions
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Added for REST API
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Added for JSON conversion
    implementation("androidx.room:room-runtime:$roomVersion") // Added for Room
    implementation("androidx.core:core-ktx:1.12.0") // Added for Room
    implementation("androidx.room:room-ktx:$roomVersion") // Added for Room
    ksp("androidx.room:room-compiler:$roomVersion") // Added for Room
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.27.0") // Added for SystemUI Controller
    implementation("androidx.work:work-runtime-ktx:$workVersion")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.09.02")) // og ver: 2023.03.00
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}