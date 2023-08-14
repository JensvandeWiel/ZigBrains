import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask
import org.jetbrains.intellij.tasks.RunPluginVerifierTask

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
    id("java") // Java support
    alias(libs.plugins.gradleIntelliJPlugin) // Gradle IntelliJ Plugin
    alias(libs.plugins.changelog) // Gradle Changelog Plugin
    alias(libs.plugins.grammarkit)
}

// Keep these in sync with whatever the oldest IDE version we're targeting in gradle.properties needs
val javaLangVersion: JavaLanguageVersion? = JavaLanguageVersion.of(17)
val javaVersion = JavaVersion.VERSION_17

java {
    toolchain {
        languageVersion.set(javaLangVersion)
    }
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

// Configure project's dependencies
repositories {
    mavenCentral()
    maven {
        // Personal jitpack mirror with builtin caching, so it's a bit faster than jitpack
        // If it fails to pull deps, comment it out and uncomment the line below it
        setUrl("https://mvn.falsepattern.com/jitpack/")
        // setUrl("https://jitpack.io/")
    }
}

dependencies {
    compileOnly(libs.annotations)
    implementation("com.github.FalsePattern:lsp4intellij-zigbrains:c6b5059039")
}

intellij {
    pluginName = properties("pluginName")
    version = properties("platformVersion")
    type = properties("platformType")

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins = properties("platformPlugins").map { it.split(',').map(String::trim).filter(String::isNotEmpty) }
}

// Include the generated files in the source set
sourceSets {
    main {
        java {
            srcDirs(
                "build/generated/sources/grammarkit/java/lexer",
                "build/generated/sources/grammarkit/java/parser"
            )
        }
    }
}

changelog {
    groups.empty()
    repositoryUrl = properties("pluginRepositoryUrl")
}

tasks {
    wrapper {
        gradleVersion = properties("gradleVersion").get()
    }

    generateLexer {
        enabled = false
    }

    generateParser {
        enabled = false
    }

    register<GenerateLexerTask>("generateZonLexer") {
        group = "build setup"
        sourceFile = file("src/main/java/com/falsepattern/zigbrains/zon/lexer/Zon.flex")
        targetDir = "build/generated/sources/grammarkit/java/lexer/com/falsepattern/zigbrains/zon/lexer"
        targetClass = "ZonFlexLexer"
        purgeOldFiles = true
    }

    register<GenerateParserTask>("generateZonParser") {
        group = "build setup"
        sourceFile = file("src/main/java/com/falsepattern/zigbrains/zon/parser/Zon.bnf")
        targetRoot = "build/generated/sources/grammarkit/java/parser"
        pathToParser = "com/falsepattern/zigbrains/zon/psi/ZonParser.java"
        pathToPsiRoot = "com/falsepattern/zigbrains/zon/psi"
        purgeOldFiles = true
    }

    register<DefaultTask>("generateSources") {
        description = "Generate source code from parser/lexer definitions"
        group = "build setup"
        dependsOn("generateZonLexer")
        dependsOn("generateZonParser")
    }

    compileJava {
        dependsOn("generateSources")
    }

    patchPluginXml {
        version = properties("pluginVersion")
        sinceBuild = properties("pluginSinceBuild")
        untilBuild = properties("pluginUntilBuild")

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with (it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }

        val changelog = project.changelog // local variable for configuration cache compatibility
        // Get the latest available change notes from the changelog file
        changeNotes = properties("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        }
    }

    task<Exec>("nixos_jbr") {
        description = "Create a symlink to package jetbrains.jdk"
        group = "build setup"
        commandLine("nix-build", "<nixpkgs>", "-A", "jetbrains.jdk", "-o", "jbr")
    }

    withType<org.jetbrains.intellij.tasks.RunIdeBase> {
        project.file("jbr/bin/java")
            .takeIf { it.exists() }
            ?.let { projectExecutable.set(it.toString()) }
    }

    withType<org.jetbrains.intellij.tasks.RunPluginVerifierTask> {
        project.file("jbr")
            .takeIf { it.exists() }
            ?.let { runtimeDir.set(it.toString()) }
    }

    signPlugin {
        certificateChainFile = file("secrets/chain.crt")
        privateKeyFile = file("secrets/private.pem")
        password = environment("PRIVATE_KEY_PASSWORD")
    }

    verifyPluginSignature {
        certificateChainFile = file("secrets/chain.crt")
    }

//    publishPlugin {
//        dependsOn("patchChangelog")
//        token = environment("PUBLISH_TOKEN")
//        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
//        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
//        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
//        channels = properties("pluginVersion").map { listOf(it.split('-').getOrElse(1) { "default" }.split('.').first()) }
//    }
}