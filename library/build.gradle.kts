plugins {
    id("com.android.library")
    id("maven-publish")
}

android {
    namespace = "dev.vendicated.xclasspath"
    compileSdk = 32

    defaultConfig {
        minSdk = 21
        targetSdk = 32
    }

    buildTypes {
        release {
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    compileOnly("de.robv.android.xposed:api:82")
}

afterEvaluate {
    publishing {
        publications {
            register(project.name, MavenPublication::class.java) {
                group = "dev.vendicated.xclasspath"
                artifactId = "XClassPath"

                from(components["release"])
            }

            repositories {
                val username = System.getenv("MAVEN_USERNAME")
                val password = System.getenv("MAVEN_PASSWORD")

                if (username != null && password != null) {
                    maven {
                        credentials {
                            this.username = username
                            this.password = password
                        }
                        setUrl("https://maven.aliucord.com/snapshots")
                    }
                } else {
                    mavenLocal()
                }
            }
        }
    }
}
