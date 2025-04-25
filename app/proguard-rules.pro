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
