#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <jni.h>
#include <assert.h>
#include <iostream>

//extern "C"
//JNIEXPORT jstring JNICALL
//Java_com_mycompany_computetools_jniapplication_MainActivity_stringFromJNI(
//        JNIEnv *env,
//        jobject /* this */) {
//    std::string hello = "Hello from C++";
//    return env->NewStringUTF(hello.c_str());
//}

static const char *jniClassName = "com/mycompany/computetools/jniapplication/MainActivity";


/*********     动态注册方法 dynamicRegFromJni   ***********/

static jstring nativeDynamicRegFromJni(JNIEnv *env, jobject obj, jstring json) {
    const char* str;
    str = env->GetStringUTFChars(json, false);
    if(str == NULL) {
        return NULL;
    }

    std::cout << str  << std::endl;

    //释放资源
    env->ReleaseStringUTFChars(json, str);

    // 返回一个字符串
    char* tmpstr = "动态注册调用成功";
    jstring rtstr = env->NewStringUTF(tmpstr);
    return rtstr;
//    return env->NewStringUTF("动态注册调用成功");
}


//(Ljava/lang/String;Ljava/lang/String;)V"
//
//实际上这些字符是与函数的参数类型一一对应的。
//
//"()" 中的字符表示参数，后面的则代表返回值。例如"()V" 就表示void Func();
//
//"(II)V" 表示 void Func(int, int);
static JNINativeMethod methods[] = {
        {"dynamicRegFromJni", "(Ljava/lang/String;)Ljava/lang/String;", (void *) nativeDynamicRegFromJni},
};

static int registerNatives(JNIEnv *env) {
    jclass clazz = env->FindClass(jniClassName);
    if (clazz == NULL)
        return JNI_FALSE;

    jint methodSize = sizeof(methods) / sizeof(methods[0]);
    if (env->RegisterNatives(clazz, methods, methodSize) < 0)
        return JNI_FALSE;

    return JNI_TRUE;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    jint result = -1;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK)
        return JNI_ERR;

    //注册方法
    if (!registerNatives(env))
        return JNI_ERR;

    result = JNI_VERSION_1_6;
    return result;
}

//JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
//    JNIEnv *env = nullptr;
//    jint ret = vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6);
//    if (ret != JNI_OK) {
//        return ;
//    }
//}
