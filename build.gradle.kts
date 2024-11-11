// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    //ext.kotlin_version = '1.7.0' // or the latest version
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        //classpath ("'com.android.tools.build:gradle:8.4.1' ")// or the latest version
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.24")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
        classpath("com.google.gms:google-services:4.3.10")
    }



}
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false

    //alias(libs.plugins.android.application) apply false
    //alias(libs.plugins.jetbrains.kotlin.android) apply false
    //classpath ("com.google.dagger:hilt-android-gradle-plugin:2.48.1")
    //id("com.google.gms.google-services") version "4.4.2" apply false
}
