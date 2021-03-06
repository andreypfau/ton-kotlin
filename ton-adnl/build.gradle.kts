kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.tonTl)
                api(projects.tonTlb)
                api(projects.tonCrypto)
                api(projects.tonApi)
                api(libs.ktor.client.core)
                api(libs.ktor.client.cio)
                api(libs.ktor.server.cio)
                api(libs.ktor.network)
                implementation(libs.serialization.json)
                implementation(projects.tonLogger)
                implementation(libs.atomicfu)
                implementation(libs.datetime)
                implementation(libs.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
