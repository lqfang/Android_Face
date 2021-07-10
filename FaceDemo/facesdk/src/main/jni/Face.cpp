//
// Created by ThinkPad on 2019/11/25.
//

#ifndef _ERROR_CODE_H
#define _ERROR_CODE_H
#endif

#include <jni.h>
#include <string>
#include<android/log.h>
#include <opencv2/opencv.hpp>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#include "seeta/Struct_cv.h"
#include "seeta/CStruct.h"
#include "seeta/libxh_face.h"
#include "seeta/verify.h"
#include "utils/NDKUtils.h"

#define TAG "FaceDetector====:%s" // 这个是自定义的LOG的标识(输出string类型)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG ,__VA_ARGS__)

/**
 * load_success 2, success 1, (当没有db的时候，init成功返回1，当存在db时，成功返回2)
 * error_dir -1, error_no_init -2,
 * enroll_no_fail -3, query_fail -4,
 * model_not_exit -5, drop_fatal -6
 * internet_error -7, verify_fatal -8
 * no_active -9, active_fatal -10
 */

// 返回状态码
int code = 0;

NDKUtils ndkUtils;

extern "C"
JNIEXPORT jint JNICALL
Java_com_xhkj_facesdk_FaceNative_active(JNIEnv *env, jobject thiz, jstring model_dir,
                                          jstring product_code, jstring andoid_id) {
    std::string model = ndkUtils.jstring2str(env, model_dir);
    std::string productCode = ndkUtils.jstring2str(env, product_code);
    std::string androidId = ndkUtils.jstring2str(env, andoid_id);
//    std::string id = "PBV0217208002680";
    //int log1 = 0;
    try {
        //log1 = log(model);
        code = active(model, productCode, androidId);
    } catch (Exception e) {

    }

//    LOGE("code==active====:%d", code);
    return code;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_xhkj_facesdk_FaceNative_init(JNIEnv *env, jobject thiz, jstring modelDir,
                                        jstring dbDir) {
    std::string model = ndkUtils.jstring2str(env, modelDir);
    std::string db = ndkUtils.jstring2str(env, dbDir);

    try {
        code = init(model, db);
    } catch (Exception e) {

    }
    return code;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_xhkj_facesdk_FaceNative_registerFace(JNIEnv *env, jobject thiz, jobject bitmap) {
    // TODO: implement registerFace()
    cv::Mat mat;
    // Bitmap 转 cv::Mat
    int ret = ndkUtils.bitmapToMat(env, bitmap, mat);
    // LOGE("ret====:%d", ret);
    if (ret == 0) {
        return NULL;
    }
    auto frame = mat;
    seeta::cv::ImageData imageData = frame;
    jint index;
    int64_t i = index;

    int code = enroll(imageData, &i);
    //LOGE("code==registerFace====:%d", code);
    index = i;

    jclass jcs = env->FindClass("com/xhkj/facesdk/module/RegisterInfo");
    jfieldID indexId = env->GetFieldID(jcs, "index", "I");
    jfieldID codeId = env->GetFieldID(jcs, "code", "I");

    jobject obj = env->AllocObject(jcs);
    env->SetIntField(obj, indexId, index);
    env->SetIntField(obj, codeId, code);
    return obj;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_xhkj_facesdk_FaceNative_query(JNIEnv *env, jobject thiz, jobject bitmap) {
    // TODO: implement query()
    cv::Mat mat;
    // Bitmap 转 cv::Mat
    int ret = ndkUtils.bitmapToMat(env, bitmap, mat);
    if (ret == 0) {
        return NULL;
    }
    auto frame = mat;
    seeta::cv::ImageData imageData = frame;

    jint index;
    jfloat score;
    int64_t i = index;
    int code = query(imageData, &i, &score);
//    LOGE("code==query====:%d", code);
    index = i;

    jclass jcs = env->FindClass("com/xhkj/facesdk/module/QueryInfo");
    jfieldID scoreId = env->GetFieldID(jcs, "score", "F");
    jfieldID indexId = env->GetFieldID(jcs, "index", "I");
    jfieldID codeId = env->GetFieldID(jcs, "code", "I");

    jobject obj = env->AllocObject(jcs);
    env->SetFloatField(obj, scoreId, score);
    env->SetIntField(obj, indexId, index);
    env->SetIntField(obj, codeId, code);
    return obj;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_xhkj_facesdk_FaceNative_queryFace(JNIEnv *env, jobject thiz, jbyteArray nv21Data,
                                             jint width, jint height, jint degree) {
    // TODO: implement queryFace()
    jbyte *bytes = env->GetByteArrayElements(nv21Data, JNI_FALSE);
    cv::Mat yuvImage(height * 3 / 2, width, CV_8UC1, bytes);
    cv::Mat bgrImage;
    try {
        cv::cvtColor(yuvImage, bgrImage, COLOR_YUV2BGR_NV21);
    } catch (Exception e) {

    }
    switch (degree) {
        case 90:
            transpose(bgrImage, bgrImage);
            flip(bgrImage, bgrImage, 1);
            break;
        case 180:
            flip(bgrImage, bgrImage, 0);
            flip(bgrImage, bgrImage, 1);
            break;
        case 270:
        case -90:
            transpose(bgrImage, bgrImage);
            flip(bgrImage, bgrImage, 0);
            break;
        default:
            break;
    }
    auto frame = bgrImage;
    seeta::cv::ImageData data = frame;
    jfloat score;
    jint index;
    int64_t i = index;

    int code = query(data, &i, &score);
//    LOGE("code==query====:%d", code);
    index = i;

    jclass jcs = env->FindClass("com/xhkj/facesdk/module/QueryInfo");
    jfieldID scoreId = env->GetFieldID(jcs, "score", "F");
    jfieldID indexId = env->GetFieldID(jcs, "index", "I");
    jfieldID codeId = env->GetFieldID(jcs, "code", "I");

    jobject obj = env->AllocObject(jcs);
    env->SetFloatField(obj, scoreId, score);
    env->SetIntField(obj, indexId, index);
    env->SetIntField(obj, codeId, code);
    return obj;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_xhkj_facesdk_FaceNative_destroy(JNIEnv *env, jobject thiz) {
    // TODO: implement destroy()
    //注销模型
    int code = destory();
    return code;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_xhkj_facesdk_FaceNative_deleteInfo(JNIEnv *env, jobject thiz, jint index) {
    // TODO: implement deleteInfo()
    int64_t i = index;
    int code = drop(i);
//    LOGE("code==drop====:%d", code);
    return code;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_xhkj_facesdk_FaceNative_count(JNIEnv *env, jobject thiz) {
    // TODO: implement count()
    jint index;
    int64_t i = index;
    int code = count(&i);
    index = i;

//    LOGE("code==count====:%d", code);

    jclass jcs = env->FindClass("com/xhkj/facesdk/module/IndexCount");
    jfieldID indexId = env->GetFieldID(jcs, "indexCount", "I");
    jfieldID codeId = env->GetFieldID(jcs, "code", "I");

    jobject obj = env->AllocObject(jcs);
    env->SetIntField(obj, indexId, index);
    env->SetIntField(obj, codeId, code);
    return obj;
}