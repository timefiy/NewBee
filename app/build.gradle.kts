plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "cn.zzuli.shopapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "cn.zzuli.shopapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(project(":CityPicker"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    //RecyclerView
    implementation ("androidx.recyclerview:recyclerview:1.1.0")
    //RecyclerView的好搭档

    //Gson解析
    implementation ("com.google.code.gson:gson:2.10.1")
    //图片加载器
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")




}