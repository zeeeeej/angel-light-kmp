import de.jensklingenberg.ktorfit.gradle.ErrorCheckingMode
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.qr.kit)
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // Koin
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)

            implementation(libs.splashscreen)
            implementation(libs.bundles.mlkit)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
//            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.bundles.androidx.lifecycle)

            implementation(libs.bundles.navigation)
            implementation(libs.bundles.kotlinxExt)
            implementation(libs.bundles.androidx.datastore)
            implementation(libs.okio)
            // Koin
            api(libs.koin.core)
            implementation(libs.koin.compose)

            implementation(libs.bundles.yunext)
            implementation(libs.bundles.zeeeeej)

            implementation(libs.bundles.coil3)
//            implementation(libs.qr.kit)
            implementation(libs.napier)

            implementation(libs.bundles.ktor.client)
            implementation(libs.ktorfit)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.kable)

        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "com.yunext.angel.light"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.yunext.angel.light"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

// https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-resources-usage.html#resource-usage
compose.resources {
    // set to true makes the generated Res class public. By default, the generated class is internal.
    publicResClass = false
    // allows you to assign the generated Res class to a particular package (to access within the code,
    // as well as for isolation in a final artifact). By default, Compose Multiplatform assigns the
    // {group name}.{module name}.generated.resources package to the class.
    packageOfResClass = "com.yunext.angel.light.resources"
    // set to always makes the project unconditionally generate the Res class. This may be useful
    // when the resource library is only available transitively. By default, Compose Multiplatform
    // uses the auto value to generate the Res class only if the current project has an explicit
    // implementation or api dependency on the resource library.
    generateResClass = auto
}

// https://foso.github.io/Ktorfit/configuration/
ktorfit{
    errorCheckingMode = ErrorCheckingMode.NONE
    generateQualifiedTypeName = true
}

