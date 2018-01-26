#include <stdlib.h>
#include <stdio.h>
#include <jni.h>
#include <assert.h>
#include <iostream>
#include <string>
#include<android/log.h>

#include "rapidjson/document.h"
#include "rapidjson/writer.h"
#include "rapidjson/stringbuffer.h"

using namespace rapidjson;
using namespace std;

string getCommandFormJson(string);
int getFirstParaFormJson(string);
int getSecondParaFormJson(string);
string setResult(string, int);

static const char *jniClassName = "com/mycompany/computetools/jniapplication/MainActivity";
static const char *COMMAND = "command";
static const char *FIRST = "parameter1";
static const char *SECOND = "parameter2";
static const char *RESULT = "result";


/*********     动态注册方法 dynamicRegFromJni   ***********/

static jstring nativeDynamicRegFromJni(JNIEnv *env, jobject obj, jstring json) {
    const char *str;
    str = env->GetStringUTFChars(json, false);
    if (str == NULL) {
        return NULL;
    }
    __android_log_print(ANDROID_LOG_DEBUG, "zqc in jni", "input is %s", str);

    string json2 = str;
    string command = getCommandFormJson(json2);
    __android_log_print(ANDROID_LOG_DEBUG, "zqc", "input command is %s", command.c_str());

    int firstParameter = getFirstParaFormJson(json2);
    __android_log_print(ANDROID_LOG_DEBUG, "zqc", "input command is %d", firstParameter);

    int secondParameter = getSecondParaFormJson(json2);
    __android_log_print(ANDROID_LOG_DEBUG, "zqc", "input command is %d", secondParameter);

    int result = 0;
    if(command == "add"){
        result = firstParameter + secondParameter;
    } else if (command == "sub"){
        result = firstParameter - secondParameter;
    } else if(command == "multi"){
        result = firstParameter * secondParameter;
    }
    string returnJson = setResult(json2, result);
    __android_log_print(ANDROID_LOG_DEBUG, "zqc in jni, ", "result is %d", result) ;


    // 返回一个字符串`
    jstring rtstr = env->NewStringUTF(returnJson.c_str());

    //释放资源
    env->ReleaseStringUTFChars(json, str);
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



//=============================json


string getCommandFormJson(string json) {

    // 1. 把 JSON 解析至 DOM。
    Document document;
    if (document.Parse<0>(json.c_str()).HasParseError()) {
        return "";
    }
    document.Parse<0>(json.c_str());

    //2,取出自己想要的值
    if (!document.HasMember(COMMAND)) {
        return "";
    }
    Value &node1 = document[COMMAND];

    std::string command = node1.GetString();
    __android_log_print(ANDROID_LOG_DEBUG, "zqc", "zqc hello = %s\n", command.c_str());

    return command;
}


int getFirstParaFormJson(string json) {

    // 1. 把 JSON 解析至 DOM。
    Document document;
    if (document.Parse<0>(json.c_str()).HasParseError()) {
        return 0;
    }
    document.Parse<0>(json.c_str());

    //2,取出自己想要的值
    if (!document.HasMember(FIRST)) {
        __android_log_print(ANDROID_LOG_DEBUG, "zqc", "parse error5");
        return 0;
    }
    Value &node1 = document[FIRST];
    __android_log_print(ANDROID_LOG_DEBUG, "zqc", "parse error5");

    if (!node1.IsInt()) {
        __android_log_print(ANDROID_LOG_DEBUG, "zqc", "parameter error");
    }
    int first = node1.GetInt();
    __android_log_print(ANDROID_LOG_DEBUG, "zqc", "zqc hello = %d\n", first);

    return first;
}

int getSecondParaFormJson(string json) {

    // 1. 把 JSON 解析至 DOM。
    Document document;
    if (document.Parse<0>(json.c_str()).HasParseError()) {
        return 0;
    }
    document.Parse<0>(json.c_str());

    //2,取出自己想要的值
    if (!document.HasMember(SECOND)) {
        return 0;
    }
    Value &node1 = document[SECOND];

    if (!node1.IsInt()) {
        return 0;
    }
    int second = node1.GetInt();
    __android_log_print(ANDROID_LOG_DEBUG, "zqc", "zqc hello = %d\n", second);

    return second;
}

string setResult(string json, int result){

    // 1. 把 JSON 解析至 DOM。
    Document document;
    if (document.Parse<0>(json.c_str()).HasParseError()) {
        return "";
    }
    document.Parse<0>(json.c_str());

    //2,取出自己想要的值
    if (!document.HasMember(RESULT)) {
        return "";
    }
    Value &resultJson = document[RESULT];

    if (!resultJson.IsInt()) {
        return "";
    }
    resultJson.SetInt(result);

    rapidjson::StringBuffer buffer;
    rapidjson::Writer<rapidjson::StringBuffer> writer(buffer);
    document.Accept(writer);
    buffer.GetString();

    __android_log_print(ANDROID_LOG_DEBUG, "zqc", "zqc return string = %s\n", buffer.GetString());

    return buffer.GetString();

//    rapidjson::Value& name_json = document["name"];
//    rapidjson::Value& age_json = document["age"];
//    std::string new_name = "wangwu";
//    int new_age = 22;
//
//    // 注意第三个参数是document.GetAllocator()，相当于深拷贝，rapidjson会分配一块内存，然后复制new_name.c_str()，
//    // 如果不指定第三个参数，则是浅拷贝，也就是rapidjson不会分配一块内存，而是直接指向new_name.c_str()，省去复制提升了性能
//    // 官方说明：
//    // http://rapidjson.org/zh-cn/md_doc_tutorial_8zh-cn.html#CreateString
//    name_json.SetString(new_name.c_str(), new_name.size(), document.GetAllocator());
}