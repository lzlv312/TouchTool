import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.application)
    alias(libs.plugins.nav.safeargs)
}

android {
    namespace = "top.bogey.touch_tool"
    compileSdk = 34
    ndkVersion = "21.4.7075529"
    buildToolsVersion = "35.0.0"

    val pattern = DateTimeFormatter.ofPattern("yyMMdd_HHmm")
    val now = LocalDateTime.now().format(pattern)

    defaultConfig {
        applicationId = "top.bogey.touch_tool"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = now

        externalNativeBuild {
            cmake {
                cppFlags.add("-std=c++11")
                cppFlags.add("-frtti")
                cppFlags.add("-fexceptions")
                cppFlags.add("-Wno-format")

                arguments.add("-DANDROID_PLATFORM=android-23")
                arguments.add("-DANDROID_STL=c++_shared")
                arguments.add("-DANDROID_ARM_NEON=TRUE")
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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            resValue("string", "app_name", "点击助手")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    applicationVariants.all {
        outputs.all {
            if (buildType.name == "release") {
                val impl = this as BaseVariantOutputImpl
                impl.outputFileName = "点击助手_$now.APK"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
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

    implementation(libs.treeview)
    implementation(libs.flexbox)

    implementation(libs.mmkv)
    implementation(libs.gson)
    implementation(libs.shizuku.api)
    implementation(libs.shizuku.provider)

    implementation(libs.exp4j)
}