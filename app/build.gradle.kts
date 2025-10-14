@file:Suppress("UnstableApiUsage")

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Locale

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
val exclusions = listOf(
    "**/R.class",
    "**/R$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    "android/**/*.*",
    "**/databinding/*",
    "**/android/databinding/*Binding.*",
    "**/BR.*",
    "**/Br.*",
    "**/*\$ViewInjector*.*",
    "**/*\$ViewBinder*.*",
    "**/Lambda$*.class",
    "**/Lambda.class",
    "**/*Lambda.class",
    "**/*Lambda*.class",
    "**/*_MembersInjector.class",
    "**/Dagger*Component*.*",
    "**/*Module_*Factory.class",
    "**/di/module/*",
    "**/*_Factory*.*",
    "**/*Module*.*",
    "**/*Dagger*.*",
    "**/*Hilt*.*",
    // Compose specifics
    "**/*ComposableSingletons*.*",
    "**/*_Impl*.*"
)

android {
    namespace = "pl.rk.gac"
    compileSdk = 36

    defaultConfig {
        applicationId = "pl.rk.gac"
        minSdk = 31
        targetSdk = 36
        versionCode = 21
        versionName = "1.4.7"
        buildToolsVersion = "36.0.0"
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
        jvmToolchain(21)
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

    dependenciesInfo {
        includeInApk = true
        includeInBundle = true
    }
}

jacoco {
    toolVersion = "0.8.12"
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

tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

androidComponents {
    onVariants { variant ->
        val variantName =
            variant.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        val testTaskName = "test${variantName}UnitTest"

        tasks.register<JacocoReport>("jacoco${variantName}Report") {
            dependsOn(testTaskName)

            group = "Reporting"
            description = "Generate Jacoco coverage report for the ${variant.name} build."

            reports {
                xml.required.set(true)
                html.required.set(true)
                csv.required.set(false)
            }

            // Source directories
            sourceDirectories.setFrom(
                files(
                    "${project.projectDir}/src/main/java",
                    "${project.projectDir}/src/main/kotlin",
                    "${project.projectDir}/src/${variant.name}/java",
                    "${project.projectDir}/src/${variant.name}/kotlin"
                )
            )

            // Class directories
            classDirectories.setFrom(
                files(
                fileTree("${layout.buildDirectory.get()}/intermediates/javac/${variant.name}") {
                    exclude(exclusions)
                },
                fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/${variant.name}") {
                    exclude(exclusions)
                }
            ))

            // Execution data
            executionData.setFrom(
                fileTree(layout.buildDirectory.get()) {
                    include("**/*.exec", "**/*.ec")
                }
            )
        }
    }
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    group = "Reporting"
    description = "Generate Jacoco coverage report for debug build."

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/databinding/*",
        "**/android/databinding/*Binding.*",
        "**/BR.*",
        "**/*\$ViewInjector*.*",
        "**/*\$ViewBinder*.*",
        "**/Lambda$*.class",
        "**/Lambda.class",
        "**/*Lambda.class",
        "**/*Lambda*.class",
        "**/*_MembersInjector.class",
        "**/Dagger*Component*.*",
        "**/*Module_*Factory.class",
        "**/di/module/*",
        "**/*_Factory*.*",
        "**/*Module*.*",
        "**/*Dagger*.*",
        "**/*Hilt*.*",
        "**/*ComposableSingletons*.*",
        "**/*_Impl*.*",
        "**/hilt_aggregated_deps/*"
    )

    sourceDirectories.setFrom(files(
        "${project.projectDir}/src/main/java",
        "${project.projectDir}/src/main/kotlin"
    ))

    classDirectories.setFrom(files(
        fileTree("${layout.buildDirectory.get()}/intermediates/javac/debug") {
            exclude(fileFilter)
        },
        fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
            exclude(fileFilter)
        }
    ))

    executionData.setFrom(files(
        "${layout.buildDirectory.get()}/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"
    ))
}

tasks.register<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn("jacocoTestReport")

    sourceDirectories.setFrom(files(
        "${project.projectDir}/src/main/java",
        "${project.projectDir}/src/main/kotlin"
    ))

    executionData.setFrom(files(
        "${layout.buildDirectory.get()}/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"
    ))

    violationRules {
        rule {
            limit {
                minimum = "0.75".toBigDecimal()
            }
        }
        rule {
            enabled = true
            element = "CLASS"
            includes = listOf("pl.rk.*")
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

tasks.register("coverage") {
    dependsOn("testDebugUnitTest", "jacocoTestReport", "jacocoTestCoverageVerification")
}

tasks.register("cleanReports") {
    doLast {
        delete("${layout.buildDirectory.get()}/reports")
    }
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
