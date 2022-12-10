
plugins {
    id("demo.kotlin-library-conventions")
}

dependencies {
    implementation(libs.bundles.moshi)
    testImplementation(libs.assertk)
    testImplementation(kotlin("test"))
}
