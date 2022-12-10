plugins {
  id("amichne.kotlin-library-conventions")
}

dependencies {
  implementation(libs.bundles.moshi)
  implementation(libs.kotlin.reflect)
  testImplementation(libs.assertk)
  testImplementation(kotlin("test"))
}
