
plugins {
    id("org.jetbrains.kotlin.jvm") // <1>
}

repositories {
    mavenCentral() // <2>
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
    constraints {
        implementation("org.apache.commons:commons-text:1.9") // <3>
    }
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1") // <4>
}

tasks.named<Test>("test") {
    useJUnitPlatform() // <5>
}
