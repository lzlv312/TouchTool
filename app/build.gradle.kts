import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.tasks.PackageAndroidArtifact
import java.io.FileInputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties

plugins {
    alias(libs.plugins.application)
    alias(libs.plugins.nav.safeargs)
}

val pattern: DateTimeFormatter? = DateTimeFormatter.ofPattern("yyMMdd_HHmm")
val now: String? = LocalDateTime.now().format(pattern)

// 读取签名配置
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

configure<ApplicationExtension> {
    namespace = "top.bogey.touch_tool"
    compileSdk = common.versions.targetSdk.get().toInt()
    ndkVersion = common.versions.ndkVersion.get()
    buildToolsVersion = common.versions.buildToolsVersion.get()

    // 签名配置
    signingConfigs {
        create("release") {
            if (keystoreProperties.containsKey("storeFile") && 
                keystoreProperties.containsKey("storePassword") && 
                keystoreProperties.containsKey("keyAlias") && 
                keystoreProperties.containsKey("keyPassword")) {
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }
        }
    }

    defaultConfig {
        applicationId = "com.one_step.app"
        minSdk = common.versions.minSdk.get().toInt()
        targetSdk = common.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = now

        @Suppress("UnstableApiUsage")
        externalNativeBuild {
            cmake {
                cppFlags += listOf("-std=c++20", "-Wno-format")
                arguments += listOf("-DANDROID_STL=c++_shared")
            }
        }

        ndk {
            abiFilters.add("arm64-v8a")
        }
    }

    buildTypes {

        debug {
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "一步一步Debug")
        }

        release {
            isMinifyEnabled = false
            isShrinkResources = false
            resValue("string", "app_name", "一步一步")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = common.versions.cmakeVersion.get()
        }
    }

    buildFeatures {
        viewBinding = true
        aidl = true
        resValues = true
    }
}

tasks.withType<PackageAndroidArtifact>().configureEach {
    if (name.contains("release", true)) {
        doLast {
            val dir = outputDirectory.get().asFile
            val apk = dir.listFiles()?.firstOrNull { it.extension == "apk" } ?: return@doLast

            val target = File(dir, "App_${now}.APK")
            apk.copyTo(target, true)
        }
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

    implementation(libs.litert)
    implementation(libs.litert.support.api) {
        exclude(group = "com.google.ai.edge.litert", module = "litert-api")
    }
}