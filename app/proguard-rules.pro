# ProGuard rules for AI Betting Bot

# Keep BuildConfig
-keep class com.aiprognoz.betting.BuildConfig { *; }

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# OpenAI SDK
-keep class com.aallam.openai.** { *; }
-dontwarn com.aallam.openai.**

# Gemini SDK
-keep class com.google.ai.client.** { *; }
-dontwarn com.google.ai.client.**

# RuStore SDK
-keep class ru.rustore.** { *; }
-dontwarn ru.rustore.**

# YooKassa SDK
-keep class ru.yoomoney.sdk.** { *; }
-dontwarn ru.yoomoney.sdk.**

# Keep data models
-keep class com.aiprognoz.betting.data.** { *; }
-keep class com.aiprognoz.betting.domain.models.** { *; }

# Prevent stripping of API response models
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
