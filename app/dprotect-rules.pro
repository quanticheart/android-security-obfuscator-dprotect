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
-ignorewarnings
#-dontwarn androidx.**
#-dontwarn com.google.android.**
#-dontwarn  kotlinx.coroutines.**
-dontwarn java.lang.invoke.**
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.atomicfu.** # https://github.com/Kotlin/kotlinx.coroutines/issues/1155
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

-dontnote kotlin.**
-dontnote kotlinx.**
-dontnote androidx.**
-dontnote com.google.**
-dontnote android.support.v4.**

-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**

-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

# for cpp libs
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}
#Ofuscação aritmética
#-obfuscate-arithmetic,low class re.dprotect.** { *; }
#
#-obfuscate-arithmetic class re.dprotect.MyClass {
#  java.lang.String decode();
#}

#Ofuscação de Constantes
#-obfuscate-constants class re.dprotect.** { *; }
#
#-obfuscate-constants class re.dprotect.MyClass {
#  private static void init();
#}

#Ofuscação de fluxo de controle
#-obfuscate-control-flow class class com.password4j.Argon2Function { *; }
#-obfuscate-control-flow class class re.dprotect.** { *; }

#Criptografia de strings
-obfuscate-strings "https://api.dprotect.re/v1/*",
                   "AES/CBC/PKCS5PADDING",
                   "android_id"

#-obfuscate-strings class re.dprotect.MyClass {
#  private static java.lang.String API_KEY;
#  public static java.lang.String getToken();
#}
