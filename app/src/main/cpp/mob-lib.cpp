#include <jni.h>
#include <string>

#define xstr(s) str(s) //important header
#define str(s) #s //important header
extern "C" JNIEXPORT jstring

Java_com_quanticheart_security_CppKeys_user(JNIEnv *env, jobject object) {
    std::string stringToBeReturned;
    std::string build_type = xstr(BUILD_TYPE); //get the value of the variable from grade.build
    stringToBeReturned = "MySecretKEY";
    return env->NewStringUTF(stringToBeReturned.c_str());
}
