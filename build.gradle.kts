
group = "com.quizappbackend"
version = "0.0.1"

plugins {
    application

    val kotlinVersion = "1.6.0"
    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

repositories {
    mavenCentral()
}

dependencies {
    val kotlinVersion: String by project
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")

    val logbackVersion: String by project
    implementation("ch.qos.logback:logback-classic:$logbackVersion")


    //K-Mongo
    val kMongoVersion: String by project
    implementation("org.litote.kmongo:kmongo:$kMongoVersion")
    implementation("org.litote.kmongo:kmongo-coroutine:$kMongoVersion")
    implementation("org.litote.kmongo:kmongo-async:$kMongoVersion")
    implementation("org.litote.kmongo:kmongo-annotation-processor:$kMongoVersion")


    //Server
    val ktorVersion: String by project
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")
    implementation("io.ktor:ktor-network-tls:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-network-tls-certificates:$ktorVersion")

    implementation("io.ktor:ktor-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-freemarker:$ktorVersion")


    //Coroutine
    val coroutineCoreVersion: String by project
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineCoreVersion")


    //BCrypt
    val bCryptVersion: String by project
    implementation("org.mindrot:jbcrypt:$bCryptVersion")


    //KOIN
    val koinVersion: String by project
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")


    //Test
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
}