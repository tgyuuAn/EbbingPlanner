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

# Glance 위젯 ActionCallback 구현체 보존 (런타임에 클래스명으로 찾음)
-keep class * extends androidx.glance.appwidget.action.ActionCallback { *; }

# Glance 위젯 GlanceAppWidget / GlanceAppWidgetReceiver 보존
-keep class * extends androidx.glance.appwidget.GlanceAppWidget { *; }
-keep class * extends androidx.glance.appwidget.GlanceAppWidgetReceiver { *; }

# Glance FontWeight 리플렉션 보존 (EbbingWidgetFontWeight.SemiBold)
-keep class androidx.glance.text.FontWeight { *; }
