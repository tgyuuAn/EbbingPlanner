-keepattributes SourceFile,LineNumberTable
-keepattributes Signature, InnerClasses, EnclosingMethod, KotlinMetadata
-keepattributes AnnotationDefault, *Annotation*

-keepclassmembers class android.content.Intent {
    public java.lang.String getStringExtra(java.lang.String);
}

## 파이어베이스
-keep class com.google.android.gms.** { *; }
-keep class com.google.firebase.** { *; }

# 도메인 모델 클래스 전체 보존 (역직렬화용)
-keep class com.tgyuu.domain.model.** { *; }

# com.puzzle.navigation 패키지 내의 모든 클래스 이름을 유지합니다.
-keep class com.tgyuu.navigation.** { *; }

# Gson 사용시 필요한 기본 설정
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.reflect.** { *; }

# Gson이 사용하는 TypeToken 보존
-keep class com.google.gson.internal.** { *; }
-keep class com.google.gson.TypeAdapterFactory
-keep class * extends com.google.gson.TypeAdapter

# Generic TypeToken 클래스 보존
-keep class * extends com.google.gson.reflect.TypeToken
