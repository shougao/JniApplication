#include <android/log.h>

#ifndef _MYLOG_H_
#define _MYLOG_H_


#define   LOG_TAG    "JSON_BUILDER"

#define DEBUG_LOG  // 通过注释该行代码关闭log开关

#ifdef DEBUG_LOG
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#else
#define LOGE(...) //__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, LOG_TAG)
#define LOGD(...) //__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, LOG_TAG)
#define LOGI(...) //__android_log_print(ANDROID_LOG_INFO, LOG_TAG, LOG_TAG)
#endif


#endif
