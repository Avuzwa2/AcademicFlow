# AcademicFlow ProGuard / R8 Rules for Play Store Publication

# Retain line numbers and source attributes for crash reporting (Play Console Stack Traces)
-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses,EnclosingMethod,*Annotation*

# Keep Data Entities and Models for Room & NoSQL Serialization
-keep class com.academicflow.app.data.local.entity.** { *; }
-keep class com.academicflow.app.data.remote.dto.** { *; }

# Keep Room Database DAOs and Generated Classes
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep Firebase Firestore & Blob Storage Models
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Keep Coroutines
-keepclassmembers class kotlinx.coroutines.** { *; }

# Keep Material Components and ViewBinding
-keep class com.google.android.material.** { *; }
-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
    public static *** bind(android.view.View);
    public static *** inflate(...);
}