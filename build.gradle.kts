import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

repositories {
    mavenCentral()
}


tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    testImplementation(kotlin("test"))
    implementation(compose.desktop.currentOs)
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")
    implementation("org.mongodb:mongodb-driver-kotlin-sync:4.11.0")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "go"
            packageVersion = "1.0.0"
        }
    }
}
