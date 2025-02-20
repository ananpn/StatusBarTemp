plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.statusbartemp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.statusbartemp"
        minSdk = 27
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    val room_version = "2.6.0"
    val hilt_version = "2.48.1"

    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.core:core-ktx:1.12.0")
    //implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    //Compose
    implementation("androidx.activity:activity-compose:1.7.0")
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    //lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    //Test

    //Hilt 1.1.0-alpha01
    annotationProcessor("androidx.hilt:hilt-compiler:1.1.0-alpha01")
    ksp("androidx.hilt:hilt-compiler:1.1.0-alpha01")
    implementation("androidx.hilt:hilt-common:1.1.0-alpha01")
    implementation("androidx.hilt:hilt-work:1.1.0-alpha01")
    implementation("com.google.dagger:hilt-android:$hilt_version")
    ksp("com.google.dagger:hilt-android-compiler:$hilt_version")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    //implementation("com.squareup.retrofit2:converter-moshi:2.4.0")
    //implementation("com.squareup.retrofit2:converter-simplexml:2.9.0")
    //implementation("com.squareup.retrofit2:converter-jaxb:2.9.0")



    //implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
    //ksp("com.squareup.moshi:moshi-kotlin-codegen:1.12.0")

}