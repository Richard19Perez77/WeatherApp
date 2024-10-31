# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Retrofit
# -keep class retrofit2.** { *; }
# -keepattributes Signature
# -keepattributes *Annotation*

# Gson
# -keep class com.google.gson.** { *; }
# -keep class * {
#    @com.google.gson.annotations.SerializedName <fields>;
#}

# Keep specific classes
# -keep class com.rperez.weatherapp.network.model.* { *; }

# Keep methods with specific annotations
# -keep @com.google.gson.annotations.SerializedName class * { *; }

# -dontwarn javax.lang.model.element.Modifier

# Keep all fields in the Config class to prevent removal
-keepclassmembers class com.rperez.weatherapp.BuildConfig {
    <fields>;
}

# Obfuscate the Config class to hide its name while keeping its members
-keep class com.rperez.weatherapp.BuildConfig {
    <init>();   # Keep the default constructor
}

-keepattributes Signature
-keepattributes *Annotation*
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }

# Keep Koin module classes
-keep class com.rperez.weatherapp.** { *; }  # Replace with your actual package name

# Keep Koin's internal classes
-keep class org.koin.** { *; }
-dontwarn org.koin.**