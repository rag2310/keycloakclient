apply(from = "${rootDir.absolutePath}\\versions.gradle.kts")

val coreKtxVersion: String by extra
val appCompatVersion: String by extra
val materialVersion: String by extra
val junitVersion: String by extra
val testJunitVersion: String by extra
val espressoCoreVersion: String by extra
val activityKtxVersion: String by extra
val hiltVersion: String by extra
val navVersion: String by extra
val constraintLayoutVersion: String by extra
val appAuthVersion: String by extra
val androidXHiltVersion: String by extra
val jwtDecodeVersion: String by extra
val workerVersion: String by extra
val roomVersion: String by extra
val retrofitVersion: String by extra
val okHTTP3LoggingVersion: String by extra

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "com.rago.keycloakclient"
        minSdk = 26
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["appAuthRedirectScheme"] = "com.rago.keycloakclient"
    }

    buildTypes {
        getByName("release") {
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
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    dataBinding {
        isEnabled = true
    }

    viewBinding {
        isEnabled = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:$coreKtxVersion")
    implementation("androidx.appcompat:appcompat:$appCompatVersion")
    implementation("com.google.android.material:material:$materialVersion")
    implementation("androidx.constraintlayout:constraintlayout:$constraintLayoutVersion")
    implementation("androidx.activity:activity-ktx:$activityKtxVersion")
    testImplementation("junit:junit:$junitVersion")
    androidTestImplementation("androidx.test.ext:junit:$testJunitVersion")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espressoCoreVersion")

    // APPAUTH --------------------------------------------------------------------
    implementation("net.openid:appauth:$appAuthVersion")
    implementation("com.auth0.android:jwtdecode:$jwtDecodeVersion")

    // Hilt -----------------------------------------------------------------------
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-work:$androidXHiltVersion")
    kapt("androidx.hilt:hilt-compiler:$androidXHiltVersion")

    // Navigation -----------------------------------------------------------------
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    // Worker ---------------------------------------------------------------------
    // Kotlin + coroutines
    implementation("androidx.work:work-runtime-ktx:$workerVersion")

    // ROOM ------------------------
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    //Retrofit----------------------------------------------------------------------
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okHTTP3LoggingVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    // Modules
    implementation(project(":feature:login"))
}