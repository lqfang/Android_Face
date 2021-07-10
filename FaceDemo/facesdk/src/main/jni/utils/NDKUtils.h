//
// Created by ThinkPad on 2019/10/17.
//

#ifndef FACERECOGNITION_NDKUTILS_H
#define FACERECOGNITION_NDKUTILS_H

#include <jni.h>
#include <string>
#include<android/log.h>
#include <opencv2/opencv.hpp>
#include <android/bitmap.h>

using namespace ::cv;

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "debug" ,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "error" ,__VA_ARGS__)

class NDKUtils {

public:

    // jstring 转C++的 string (加static就是类的方法，不加就是对象的方法)
    static std::string jstring2str(JNIEnv *env, jstring jstr) {
        char *rtn = NULL;
        jclass clsstring = env->FindClass("java/lang/String");
        // GB2312
        jstring strencode = env->NewStringUTF("GB2312");
        jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
        jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
        jsize alen = env->GetArrayLength(barr);
        jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
        if (alen > 0) {
            rtn = (char *) malloc(alen + 1);
            memcpy(rtn, ba, alen);
            rtn[alen] = 0;
        }
        env->ReleaseByteArrayElements(barr, ba, 0);
        std::string stemp(rtn);
        free(rtn);
        return stemp;
    }

    static char *jstring2char(JNIEnv *env, jstring jstr) {
        char *rtn = NULL;
        jclass clsstring = env->FindClass("java/lang/String");
        jstring strencode = env->NewStringUTF("GB2312");
        jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
        jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
        jsize alen = env->GetArrayLength(barr);
        jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
        if (alen > 0) {
            rtn = (char *) malloc(alen + 1); //new char[alen+1];
            memcpy(rtn, ba, alen);
            rtn[alen] = 0;
        }
        env->ReleaseByteArrayElements(barr, ba, 0);
        return rtn;
    }

    static jstring char2jstring(JNIEnv *env, const char *pat) {
        // 定义java String类 strClass
//        jclass strClass = (env)->FindClass("Ljava/lang/String;");
        jclass strClass = env->FindClass("java/lang/String");
        // 获取java String类方法String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
        jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");

        // 建立byte数组
        jbyteArray bytes = (env)->NewByteArray((jsize) strlen(pat));
//        LOGE("bytes====:%g", bytes);
        // 将char* 转换为byte数组
        (env)->SetByteArrayRegion(bytes, 0, (jsize) strlen(pat), (jbyte *) pat);
        //设置String, 保存语言类型,用于byte数组转换至String时的参数
        jstring encoding = (env)->NewStringUTF("GB2312");
        //将byte数组转换为java String,并输出

        jstring s = (jstring) (env)->NewObject(strClass, ctorID, bytes, encoding);
//        LOGE("jstring====:%s", s);
        return s;
    }


#define ASSERT(status, ret)     if (!(status)) { return ret; }
#define ASSERT_FALSE(status)    ASSERT(status, false)

    int bitmapToMat(JNIEnv *env, jobject obj_bitmap, cv::Mat &matrix) {
        void * bitmapPixels;                                            // 保存图片像素数据
        AndroidBitmapInfo bitmapInfo;                                   // 保存图片参数

        ASSERT_FALSE( AndroidBitmap_getInfo(env, obj_bitmap, &bitmapInfo) >= 0);        // 获取图片参数
        //LOGE("bitmapToMat====:%s","111");
        ASSERT_FALSE( bitmapInfo.format == ANDROID_BITMAP_FORMAT_RGBA_8888
                      || bitmapInfo.format == ANDROID_BITMAP_FORMAT_RGB_565 );          // 只支持 ARGB_8888 和 RGB_565
        //LOGE("bitmapToMat====:%s","222");
        ASSERT_FALSE( AndroidBitmap_lockPixels(env, obj_bitmap, &bitmapPixels) >= 0 );  // 获取图片像素（锁定内存块）
        //LOGE("bitmapToMat====:%s","333");
        ASSERT_FALSE( bitmapPixels );
        //LOGE("bitmapToMat====:%s","444");

        if (bitmapInfo.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
            cv::Mat tmp(bitmapInfo.height, bitmapInfo.width, CV_8UC4, bitmapPixels);    // 建立临时 mat
            tmp.copyTo(matrix);                                                         // 拷贝到目标 matrix
            //LOGE("bitmapToMat====:%s","555");
        } else {
            cv::Mat tmp(bitmapInfo.height, bitmapInfo.width, CV_8UC2, bitmapPixels);
            cv::cvtColor(tmp, matrix, cv::COLOR_BGR5652RGB);
            //LOGE("bitmapToMat====:%s","666");
        }

        //LOGE("bitmapToMat====:%s","777");

        AndroidBitmap_unlockPixels(env, obj_bitmap);            // 解锁

        //LOGE("bitmapToMat====:%s","888");
        return 1;
    }

    bool matToBitmap(JNIEnv *env, cv::Mat &matrix, jobject obj_bitmap) {
        void * bitmapPixels;                                            // 保存图片像素数据
        AndroidBitmapInfo bitmapInfo;                                   // 保存图片参数

        ASSERT_FALSE( AndroidBitmap_getInfo(env, obj_bitmap, &bitmapInfo) >= 0);        // 获取图片参数
        ASSERT_FALSE( bitmapInfo.format == ANDROID_BITMAP_FORMAT_RGBA_8888
                      || bitmapInfo.format == ANDROID_BITMAP_FORMAT_RGB_565 );          // 只支持 ARGB_8888 和 RGB_565
        ASSERT_FALSE( matrix.dims == 2
                      && bitmapInfo.height == (uint32_t)matrix.rows
                      && bitmapInfo.width == (uint32_t)matrix.cols );                   // 必须是 2 维矩阵，长宽一致
        ASSERT_FALSE( matrix.type() == CV_8UC1 || matrix.type() == CV_8UC3 || matrix.type() == CV_8UC4 );
        ASSERT_FALSE( AndroidBitmap_lockPixels(env, obj_bitmap, &bitmapPixels) >= 0 );  // 获取图片像素（锁定内存块）
        ASSERT_FALSE( bitmapPixels );

        if (bitmapInfo.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
            cv::Mat tmp(bitmapInfo.height, bitmapInfo.width, CV_8UC4, bitmapPixels);
            switch (matrix.type()) {
                case CV_8UC1:   cv::cvtColor(matrix, tmp, cv::COLOR_GRAY2RGBA);     break;
                case CV_8UC3:   cv::cvtColor(matrix, tmp, cv::COLOR_RGB2RGBA);      break;
                case CV_8UC4:   matrix.copyTo(tmp);                                 break;
                default:        AndroidBitmap_unlockPixels(env, obj_bitmap);        return false;
            }
        } else {
            cv::Mat tmp(bitmapInfo.height, bitmapInfo.width, CV_8UC2, bitmapPixels);
            switch (matrix.type()) {
                case CV_8UC1:   cv::cvtColor(matrix, tmp, cv::COLOR_GRAY2BGR565);   break;
                case CV_8UC3:   cv::cvtColor(matrix, tmp, cv::COLOR_RGB2BGR565);    break;
                case CV_8UC4:   cv::cvtColor(matrix, tmp, cv::COLOR_RGBA2BGR565);   break;
                default:        AndroidBitmap_unlockPixels(env, obj_bitmap);        return false;
            }
        }
        AndroidBitmap_unlockPixels(env, obj_bitmap);                // 解锁
        return true;
    }

};

#endif //FACERECOGNITION_NDKUTILS_H
