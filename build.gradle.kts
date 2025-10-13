// Top-level build file where you can add configuration options common to all subprojects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// build.gradle.kts (root level)
abstract class InstallGitHooksTask : Exec() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val scriptsDir: DirectoryProperty

    @get:OutputDirectory
    abstract val hooksDir: DirectoryProperty

    @TaskAction
    fun installHooks() {
        val scriptsDirFile = scriptsDir.get().asFile
        val hooksDirFile = hooksDir.get().asFile

        if (!hooksDirFile.exists()) {
            println("⚠️  .git/hooks directory not found")
            return
        }

        if (!scriptsDirFile.exists()) {
            println("⚠️  .githooks directory not found")
            return
        }

        val hooks = listOf(
            "commit-msg",
            "pre-commit",
            "prepare-commit-msg",
            "post-commit",
            "pre-push"
        )

        hooks.forEach { hookName ->
            val source = File(scriptsDirFile, hookName)
            val target = File(hooksDirFile, hookName)

            if (source.exists()) {
                // Create relative symlink
                val relativePath = "../../.githooks/$hookName"
                target.delete()

                // Make source executable
                source.setExecutable(true)

                // Create symlink (Unix/Linux/Mac)
                try {
                    val process = ProcessBuilder("ln", "-sf", relativePath, target.absolutePath)
                        .redirectErrorStream(true)
                        .start()

                    process.waitFor()
                    println("✅ Installed $hookName")
                } catch (_: Exception) {
                    // Fallback: copy file on Windows
                    source.copyTo(target, overwrite = true)
                    target.setExecutable(true)
                    println("✅ Copied $hookName")
                }
            }
        }

        println("🎉 Git hooks installed successfully!")
    }
}

tasks.register<InstallGitHooksTask>("installGitHooks") {
    description = "Install Git hooks"
    group = "git hooks"

    scriptsDir.set(layout.projectDirectory.dir(".githooks"))
    hooksDir.set(layout.projectDirectory.dir(".git/hooks"))
}

abstract class UninstallGitHooksTask : Exec() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val hooksDir: DirectoryProperty

    @TaskAction
    fun uninstallHooks() {
        val hooksDirFile = hooksDir.get().asFile

        val hooks = listOf(
            "commit-msg",
            "pre-commit",
            "prepare-commit-msg",
            "post-commit",
            "pre-push"
        )

        hooks.forEach { hookName ->
            val target = File(hooksDirFile, hookName)
            if (target.exists()) {
                target.delete()
                println("🗑️  Removed $hookName")
            }
        }

        println("Git hooks uninstalled")
    }
}

tasks.register<UninstallGitHooksTask>("uninstallGitHooks") {
    description = "Uninstall Git hooks"
    group = "git hooks"

    hooksDir.set(layout.projectDirectory.dir(".git/hooks"))
}

// Auto-install hooks on first build (optional)
tasks.matching { it.name == "preBuild" }.configureEach {
    dependsOn("installGitHooks")
}
