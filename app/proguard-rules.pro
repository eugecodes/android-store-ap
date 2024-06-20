# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/andressegurola/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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


-dontwarn com.google.zxing.**

-dontwarn com.github.jaiimageio.**
-dontwarn java8.util.**
-dontwarn com.google.common.util.concurrent.**

-dontwarn caribouapp.caribou.com.**
-dontwarn com.opencsv.**
-dontwarn org.apache.commons.beanutils.**
-dontwarn org.apache.commons.collections.**
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-dontoptimize

-dontshrink
-dontoptimize

-keep class com.google.gson.** { *; }
-keep class java8.util.** { *; }
-keep class caribouapp.caribou.com.cariboucoffee.api.serializers.** { *; }
-keep class caribouapp.caribou.com.cariboucoffee.mvp.menu.model.** { *; }
-keep class caribouapp.caribou.com.cariboucoffee.order.OrderDropOffListener { *; }

# keep enums

-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


-keep class net.cogindo.ssl.** { *; }

-dontwarn java.lang.reflect.AnnotatedType
-dontwarn javax.lang.model.element.Modifier
