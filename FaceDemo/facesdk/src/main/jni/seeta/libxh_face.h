#include <iostream>
#include "CStruct.h"
#ifndef LIBXH_FACE_H
#define LIBXH_FACE_H

#ifdef DLL_EXPORT
#define EXPORT __declspec(dllexport)
#else
#define EXPORT 
#endif // EXPORT


EXPORT int log(std::string log_path);
// 设备激活
EXPORT int active(std::string model_dir, std::string produce_code, std::string android_id);

// modelPath: 模型路径，face_db_path:人脸库路径，face_db_path:人脸库备份路径
//EXPORT void init(std::string modelPath, const char *face_db_path, const char *face_db_path_bak);
EXPORT int init(std::string model_dir, std::string face_db_dir);

// ImageFilename,图像名， index: 返回注册后的 id, flag：每次注册默认刷新到人脸库中
//EXPORT int enroll(const cv::Mat &mat, int64_t *index, bool flag = true);
EXPORT int enroll(SeetaImageData &image, int64_t *index, bool flag = true);

// index: 返回查询人脸库中的id,如果没有该用户不在库中，index为 -1
//EXPORT int query(const cv::Mat &mat, int64_t *index, float *score);
EXPORT int query(SeetaImageData &image, int64_t *index, float *score);

// 删除指定的index特征
EXPORT int drop(int64_t index, bool flag = true);

// 返回的index值 指 总个数
EXPORT int count(int64_t *index);

// 注销掉在初始化时候 new 的对象
EXPORT int destory();

#endif // !LIBXH_FACE_H




