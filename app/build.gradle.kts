import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.detekt)
    alias(libs.plugins.manes)
    alias(libs.plugins.dokka)
    alias(libs.plugins.test.logger)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "pl.rk.gac"
    compileSdk = 36

    defaultConfig {
        applicationId = "pl.rk.gac"
        minSdk = 28
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildToolsVersion = "36.0.0"
}

dependencies {
    detektPlugins(libs.detekt)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.coroutines.core)
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.mockk.agent)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.mockk.agent)
    testImplementation(libs.mockk.android)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.ui.tooling)
    testImplementation(libs.coroutines.test)
}

detekt {
    config.setFrom("$projectDir/detekt.yml")
    autoCorrect = true
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates") {
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = JvmTarget.JVM_21.target
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = JvmTarget.JVM_21.target
}

dokka {
    dokkaSourceSets.main {
        jdkVersion.set(java.targetCompatibility.toString().toInt()) // Used for linking to JDK documentation
        skipDeprecated.set(false)
    }

    pluginsConfiguration.html {
        dokkaSourceSets {
            configureEach {
                documentedVisibilities.set(
                    setOf(
                        VisibilityModifier.Public,
                        VisibilityModifier.Private,
                        VisibilityModifier.Protected,
                        VisibilityModifier.Internal,
                        VisibilityModifier.Package,
                    )
                )
            }
        }
    }
}

hilt {
    enableAggregatingTask = false
}

testlogger {
    showStackTraces = false
    showFullStackTraces = false
    showCauses = false
    slowThreshold = 10000
    showSimpleNames = true
}

private fun isNonStable(version: String): Boolean {
    return listOf("alpha", "beta", "rc", "cr", "m", "preview", "snapshot", "dev")
        .any { version.lowercase().contains(it) }
}
