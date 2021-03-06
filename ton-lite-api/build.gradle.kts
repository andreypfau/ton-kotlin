kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.tonTl)
                api(projects.tonTlb)
                api(projects.tonBlock)
                api(projects.tonCrypto)
                api(projects.tonCell)
                api(projects.tonBoc)
                api(projects.tonApi)
                api(projects.tonLogger)
                implementation(libs.serialization.json)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
