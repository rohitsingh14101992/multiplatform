import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.native.cocoapods")
    id("kotlinx-serialization")
}

val coroutinesVersion = "1.3.5"
val serializer_version = "0.20.0"
val ktor_version = "1.3.2"
val kodeinVersion = "6.5.3"
val kotlin_version = "1.3.72"

version = "1.0.0"
kotlin {

    cocoapods {
        summary = "Shared module for Android and iOS"
        homepage = "Link to a Kotlin/Native module homepage"
        frameworkName = "SharedCode"
    }

    val iOSTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
            ::iosArm64
        else
            ::iosX64

    iOSTarget("ios") {
        compilations {
            val main by getting {
                kotlinOptions.freeCompilerArgs = listOf("-Xobjc-generics")
            }
        }
    }
    android()


    sourceSets["commonMain"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-common")

        // COROUTINES
        implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:$coroutinesVersion")



        // SERIALIZATION
        implementation ("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$serializer_version")

        // KODE IN
        implementation ("org.kodein.di:kodein-di-core:$kodeinVersion")
        implementation ("org.kodein.di:kodein-di-erased:$kodeinVersion")

        // KTOR
        implementation ("io.ktor:ktor-client-core:$ktor_version")
        implementation ("io.ktor:ktor-client-serialization:$ktor_version")
        implementation("io.ktor:ktor-client-logging:$ktor_version")
    }

    sourceSets["iosMain"].dependencies {
        implementation ("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")
        implementation ("org.jetbrains.kotlin:kotlin-stdlib-common:$kotlin_version")

        // COROUTINES
        implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:$coroutinesVersion")

        // SERIALIZATION
        implementation ("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializer_version")

        // KTOR

        implementation("io.ktor:ktor-client-ios:$ktor_version")
        implementation("io.ktor:ktor-client-serialization-native:$ktor_version")
        implementation("io.ktor:ktor-client-logging-native:$ktor_version")
        implementation("io.ktor:ktor-client-json-native:$ktor_version")
    }

    sourceSets["commonTest"].dependencies {
        implementation ("io.ktor:ktor-client-mock:$ktor_version")
        implementation ("io.ktor:ktor-client-mock-jvm:$ktor_version")
        implementation ("io.ktor:ktor-client-mock-native:$ktor_version")
        implementation ( "org.jetbrains.kotlin:kotlin-test-common:$kotlin_version")
        implementation ( "org.jetbrains.kotlin:kotlin-test-annotations-common:$kotlin_version")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$coroutinesVersion")
    }

    sourceSets["androidTest"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")
        implementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializer_version")
        }
}

dependencies {
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-common:$kotlin_version")

    // COROUTINES
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:$coroutinesVersion")

    // SERIALIZATION
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializer_version")

    // KTOR
    implementation ("io.ktor:ktor-client-android:$ktor_version")
    implementation("io.ktor:ktor-client-serialization-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-logging-jvm:$ktor_version")
}

android {

    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    sourceSets {
        getByName("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            java.srcDirs("src/androidMain/kotlin")
            res.srcDirs("src/androidMain/res")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            consumerProguardFiles("consumer-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
    }

    val packForXcode by tasks.creating(Sync::class) {
        val targetDir = File(buildDir, "xcode-frameworks")

        /// selecting the right configuration for the iOS
        /// framework depending on the environment
        /// variables set by Xcode build
        val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
        val framework = kotlin.targets
            .getByName<KotlinNativeTarget>("ios")
            .binaries.getFramework(mode)
        inputs.property("mode", mode)
        dependsOn(framework.linkTask)

        from({ framework.outputDirectory })
        into(targetDir)

        /// generate a helpful ./gradlew wrapper with embedded Java path
        doLast {
            val gradlew = File(targetDir, "gradlew")
            gradlew.writeText("#!/bin/bash\n"
                    + "export 'JAVA_HOME=${System.getProperty("java.home")}'\n"
                    + "cd '${rootProject.rootDir}'\n"
                    + "./gradlew \$@\n")
            gradlew.setExecutable(true)
        }
    }

    tasks.getByName("build").dependsOn(packForXcode)

}