@file:Suppress("UnstableApiUsage")

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
    alias(libs.plugins.dokka)
    alias(libs.plugins.test.logger)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.ksp)
    jacoco
}

val javaVersion: JavaVersion = JavaVersion.VERSION_21
val jvmVersion = 21

android {
    namespace = "pl.rk.gac"
    compileSdk = 36

    defaultConfig {
        applicationId = "pl.rk.gac"
        minSdk = 31
        targetSdk = 36
        versionCode = 20
        versionName = "1.4.6"
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

        debug {
            enableAndroidTestCoverage = true
            enableUnitTestCoverage = true
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    kotlin {
        jvmToolchain(jvmVersion.toInt())
    }

    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE.md,LICENSE-notice.md}"
        }
    }

    bundle {
        language {
            enableSplit = false
        }
    }

    buildToolsVersion = "36.0.0"

    dependenciesInfo {
        includeInApk = true
        includeInBundle = true
    }
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

tasks.withType<Detekt>().configureEach {
    jvmTarget = JvmTarget.JVM_21.target
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = JvmTarget.JVM_21.target
}

val jacocoTestReport by tasks.registering(JacocoReport::class) {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    val fileTree = fileTree("${project.layout.buildDirectory}/intermediates/javac/debug") {
        exclude("**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "**/*Test*.*")
    }

    classDirectories.setFrom(fileTree)
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(files("${project.layout.buildDirectory}/jacoco/testDebugUnitTest.exec"))
}

val jacocoTestCoverageVerification by tasks.registering(JacocoCoverageVerification::class) {
    dependsOn("testDebugUnitTest")

    violationRules {
        rule {
            limit {
                minimum = "0.75".toBigDecimal()
            }
        }
        rule {
            enabled = true
            element = "CLASS"
            includes = listOf("rk.*")
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.75".toBigDecimal()
            }
        }
    }

    classDirectories.setFrom(fileTree("${project.layout.buildDirectory}/intermediates/javac/debug"))
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(files("${project.layout.buildDirectory}/jacoco/testDebugUnitTest.exec"))
}

tasks.register("cleanReports") {
    doLast {
        delete("${layout.buildDirectory}/reports")
    }
}

tasks.register("coverage") {
    dependsOn("testDebugUnitTest", jacocoTestReport, jacocoTestCoverageVerification)
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
