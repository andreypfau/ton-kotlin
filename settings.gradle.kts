rootProject.name = "ton-kotlin"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        kotlin("multiplatform") version "1.7.0-RC"
        kotlin("plugin.serialization") version "1.6.21"
        id("org.jetbrains.kotlinx.benchmark") version "0.4.2"
    }
}

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

include(":ton-adnl")
include(":ton-api")
include(":ton-bigint")
include(":ton-bitstring")
include(":ton-block")
include(":ton-block-tlb")
include(":ton-boc")
include(":ton-cell")
include(":ton-crypto")
include(":ton-hashmap")
include(":ton-lite-api")
include(":ton-lite-client")
include(":ton-logger")
include(":ton-mnemonic")
include(":ton-smartcontract")
include(":ton-tl")
include(":ton-tlb")
include(":ton-tlb2")
