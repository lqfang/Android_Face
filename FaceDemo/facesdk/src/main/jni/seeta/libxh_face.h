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
// �豸����
EXPORT int active(std::string model_dir, std::string produce_code, std::string android_id);

// modelPath: ģ��·����face_db_path:������·����face_db_path:�����ⱸ��·��
//EXPORT void init(std::string modelPath, const char *face_db_path, const char *face_db_path_bak);
EXPORT int init(std::string model_dir, std::string face_db_dir);

// ImageFilename,ͼ������ index: ����ע���� id, flag��ÿ��ע��Ĭ��ˢ�µ���������
//EXPORT int enroll(const cv::Mat &mat, int64_t *index, bool flag = true);
EXPORT int enroll(SeetaImageData &image, int64_t *index, bool flag = true);

// index: ���ز�ѯ�������е�id,���û�и��û����ڿ��У�indexΪ -1
//EXPORT int query(const cv::Mat &mat, int64_t *index, float *score);
EXPORT int query(SeetaImageData &image, int64_t *index, float *score);

// ɾ��ָ����index����
EXPORT int drop(int64_t index, bool flag = true);

// ���ص�indexֵ ָ �ܸ���
EXPORT int count(int64_t *index);

// ע�����ڳ�ʼ��ʱ�� new �Ķ���
EXPORT int destory();

#endif // !LIBXH_FACE_H




