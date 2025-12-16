# ----------------------------------------------------------------------------
# 1. General Settings (Crucial for Debugging)
# ----------------------------------------------------------------------------
# Keep file names and line numbers so crash logs (Logcat/Crashlytics) make sense.
-keepattributes SourceFile,LineNumberTable

# Keep annotations needed by Hilt, Room, and Retrofit
-keepattributes *Annotation*

# Keep generic types (e.g., List<CoinDto>). Retrofit NEEDS this to know what to parse.
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses, EnclosingMethod

# ----------------------------------------------------------------------------
# 2.Data Models
# ----------------------------------------------------------------------------
# We MUST tell R8 not to rename the classes you use for API responses and Database entities.

# Keep all Remote DTOs (Retrofit)
-keep class com.engfred.repoexplorer.data.remote.** { *; }

# Keep all Local Entities (Room)
-keep class com.engfred.repoexplorer.data.local.** { *; }

# Keep Domain Models (if used in UI/Reflection)
-keep class com.engfred.repoexplorer.domain.model.** { *; }

# ----------------------------------------------------------------------------
# 3. Retrofit & Gson
# ----------------------------------------------------------------------------
# If the API returns 500 or crashes during parsing, it's usually because
# R8 stripped the SerializedName annotations.
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-keep class com.google.gson.** { *; }
-keep interface com.google.gson.** { *; }

# ----------------------------------------------------------------------------
# 4. Room Database
# ----------------------------------------------------------------------------
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.SharedSQLiteStatement

# Keep the Application class entry point
-keep class com.engfred.repoexplorer.RepoExplorerApplication { *; }