// Write C++ code here.
//


#include <string>
#include <regex>
#include <sstream>

#include <jni.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#include <android/log.h>

#define TAG "kifio-parser"

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,    TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,     TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,     TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,    TAG, __VA_ARGS__)

bool isCancelled = false;

std::string jstring2string(JNIEnv *env, jstring jStr) {
    if (!jStr)
        return "";
    const jclass stringClass = env->GetObjectClass(jStr);
    const jmethodID getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
    const jbyteArray stringJbytes = (jbyteArray) env->CallObjectMethod(jStr, getBytes, env->NewStringUTF("UTF-8"));
    size_t length = (size_t) env->GetArrayLength(stringJbytes);
    jbyte *pBytes = env->GetByteArrayElements(stringJbytes, NULL);
    std::string ret = std::string((char *) pBytes, length);
    env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);
    env->DeleteLocalRef(stringJbytes);
    env->DeleteLocalRef(stringClass);
    return ret;
}

JNIEXPORT jobject JNICALL getMatches(JNIEnv *env, std::string text, std::string mask) {
    LOGD("Try to get matches for mask = %s", mask.c_str());
    isCancelled = false;

    jclass java_util_ArrayList = static_cast<jclass>(env->NewGlobalRef(
            env->FindClass("java/util/ArrayList")));
    jmethodID java_util_ArrayList_ = env->GetMethodID(java_util_ArrayList, "<init>", "(I)V");
    jmethodID java_util_ArrayList_size = env->GetMethodID(java_util_ArrayList, "size", "()I");
    jmethodID java_util_ArrayList_get = env->GetMethodID(java_util_ArrayList, "get", "(I)Ljava/lang/Object;");
    jmethodID java_util_ArrayList_add = env->GetMethodID(java_util_ArrayList, "add", "(Ljava/lang/Object;)Z");
    jobject results = env->NewObject(java_util_ArrayList, java_util_ArrayList_, 16);

    std::stringstream ss(text);
    std::string line;
    std::regex str_regex(mask);

    while (std::getline(ss, line, '\n')) {
        if (isCancelled) {
            LOGD("Operation is cancelled!");
            return results;
        }

        if (std::regex_match (line, str_regex)) {
            jstring element = env->NewStringUTF(line.c_str());
            env->CallBooleanMethod(results, java_util_ArrayList_add, element);
            env->DeleteLocalRef(element);
        }
    }

    LOGD("Return results for mask = %s", mask.c_str());
    return results;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_me_kifio_stringsparser_data_LocalRepository_getFilteredStrings(JNIEnv *env, jobject thiz,
                                                                    jstring file, jstring mask,
                                                                    jobject assetManager) {

    AAssetManager *mgr = AAssetManager_fromJava(env, assetManager);
    AAsset *asset = AAssetManager_open(mgr, jstring2string(env, file).c_str(), AASSET_MODE_UNKNOWN);

    if (NULL == asset) {
        LOGD("Asset %s not found", jstring2string(env, file).c_str());
        return JNI_FALSE;
    }
    long size = AAsset_getLength(asset);
    char *buffer = (char *) malloc(sizeof(char) * size);

    AAsset_read(asset, buffer, size);
    AAsset_close(asset);

    jobject results = getMatches(env, buffer, jstring2string(env, mask));
    free(buffer);
    return results;
}
extern "C"
JNIEXPORT void JNICALL
Java_me_kifio_stringsparser_data_LocalRepository_cancelCurrentOperation(JNIEnv *env, jobject thiz) {
    LOGD("Cancel current operation!");
    isCancelled = true;
}