#include <iostream>
#ifndef __VERIFY_H__
#define __VERIFY_H__



// �ж���Ȩ�ļ�
// ����һ���ļ�Ŀ¼���жϸ��ļ�Ŀ¼���Ƿ��� ָ�����ļ�����
int existFile(std::string file_dir);

//������飬������������������200,�����ɹ�
int checkInternet(std::string ip, const int port,std::string path, FILE * fp);

//��ȡMAC��ַ
long mac_addr_sys(char *addr);

// ����addr  ���� hash��� str
int getHash(const char *addr, std::string *str);

//������������ַ�����hash(��Ʒ��ʶ��+hash(MAC)��������÷���˷��ص���Ȩ�ַ�
int sendStr(std::string ip, const int port, std::string in_str, std::string * out_str);

//���ַ���д�뵽ָ������Ȩ�ļ��У������ļ�·���������ַ���
int writeAuthorizationFile(const char * filename, std::string *str);


//��ȡ��Ȩ�ļ�������һ���ļ�����·��+�ļ���������ȡ�����ݣ��ַ�����д�뵽���������
int readAuthorizationFile(const char * filename, char * data);


// �Ƚ������ַ���ֵ�Ƿ���� Ҳ���ǱȽ�����hashֵ�Ƿ����
int compare(std::string hash1, std::string hash2);

//��ȡ�����ļ��Ĵ���ʱ����޸�ʱ��
int getFileTime(std::string file, long *creativeTime, long *modifyTime);

int activeFunc(std::string model_dir, std::string produce_code, std::string android_id, FILE * fp);

#endif

