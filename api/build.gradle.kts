plugins {
    id("io.github.goooler.shadow") version "8.1.8"
}

repositories {
}

dependencies {
    implementation(project(":common"))
    // Adventure
    implementation("net.kyori:adventure-api:${rootProject.properties["adventure_bundle_version"]}")
    compileOnly("net.kyori:adventure-text-minimessage:${rootProject.properties["adventure_bundle_version"]}")
    compileOnly("net.kyori:adventure-text-serializer-gson:${rootProject.properties["adventure_bundle_version"]}")
    // YAML
    implementation(files("libs/boosted-yaml-${rootProject.properties["boosted_yaml_version"]}.jar"))
    // Cache
    compileOnly("com.github.ben-manes.caffeine:caffeine:${rootProject.properties["caffeine_version"]}")
    // Netty
    compileOnly("io.netty:netty-all:4.1.113.Final")
    // FOP
    compileOnly("org.apache.pdfbox:fontbox:${rootProject.properties["fontbox_version"]}")
    // COMMONS IO
    compileOnly("commons-io:commons-io:${rootProject.properties["commons_io_version"]}")
    // GSON
    compileOnly("com.google.code.gson:gson:${rootProject.properties["gson_version"]}")
}

tasks {
    shadowJar {
        relocate ("net.kyori", "net.momirealms.customnameplates.libraries")
    }
}