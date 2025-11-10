import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.application)
    alias(libs.plugins.nav.safeargs)
}

android {
    namespace = "top.bogey.touch_tool"
    compileSdk = 36
    ndkVersion = "29.0.14206865"
    buildToolsVersion = "36.1.0"

    val pattern = DateTimeFormatter.ofPattern("yyMMdd_HHmm")
    val now = LocalDateTime.now().format(pattern)
    val code = 1

    defaultConfig {
        applicationId = "top.bogey.touch_tool"
        minSdk = 24
        targetSdk = 36
        versionCode = code
        versionName = now

        externalNativeBuild {
            cmake {
                cppFlags += listOf("-std=c++14", "-Wno-format")
                arguments += listOf("-DANDROID_STL=c++_shared")
            }
        }

        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters.add("arm64-v8a")
        }
    }

    buildTypes {

        debug {
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "点击助手Debug")
        }

        release {
            isMinifyEnabled = false
            isShrinkResources = false
            resValue("string", "app_name", "点击助手")
        }
    }

    applicationVariants.all {
        outputs.all {
            if (buildType.name == "release") {
                val impl = this as BaseVariantOutputImpl
                impl.outputFileName = "点击助手_$now($code).APK"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "4.1.2"
        }
    }

    buildFeatures {
        viewBinding = true
        aidl = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.nav.fragment)
    implementation(libs.nav.ui)

    implementation(libs.flexbox)

    implementation(libs.mmkv)
    implementation(libs.gson)
    implementation(libs.shizuku.api)
    implementation(libs.shizuku.provider)

    implementation(libs.exp4j)
    implementation(libs.zxinglite)
    implementation(libs.tinypinyin)
    implementation(libs.hiddenapibypass)
}