plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.0"
}

group = "org.qiuhua.troveserver"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()  //加载本地仓库
    mavenCentral()  //加载中央仓库
    maven("https://jitpack.io")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://mvn.everbuild.org/public/")
    maven("https://repo.hypera.dev/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")

}





dependencies {
    compileOnly("com.google.code.gson:gson:2.13.2")
    compileOnly ("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    implementation("net.minestom:minestom:2025.09.13-1.21.8")
    //implementation("net.minestom:minestom:2026.01.01-1.21.11")
    implementation("org.yaml:snakeyaml:2.2")
    implementation("de.articdive:jnoise-pipeline:4.1.0")
    implementation("com.dfsek.terra:minestom:6.6.5-BETA+1ef12fdec")
    implementation("org.tinylog:tinylog-impl:2.6.2")
    implementation("org.tinylog:tinylog-api:2.6.2")
    implementation("org.tinylog:slf4j-tinylog:2.6.2")
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("com.ezylang:EvalEx:3.6.0")
    implementation("org.openjdk.nashorn:nashorn-core:15.7")
    implementation("jakarta.json:jakarta.json-api:2.1.3")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("io.netty:netty-all:4.2.9.Final")
    
}



tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "org.qiuhua.troveserver.Main"
        }
    }


    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        // 指定输出目录
        destinationDirectory.set(file("D:\\Server-Minestom"))
        mergeServiceFiles()
        archiveClassifier.set("") // Prevent the -all suffix on the shadowjar file.
    }
}
