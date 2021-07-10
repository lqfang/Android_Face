#include <iostream>
#ifndef __VERIFY_H__
#define __VERIFY_H__



// 判断授权文件
// 传入一个文件目录，判断该文件目录下是否有 指定的文件存在
int existFile(std::string file_dir);

//连网检查，请求服务器，如果返回200,联网成功
int checkInternet(std::string ip, const int port,std::string path, FILE * fp);

//获取MAC地址
long mac_addr_sys(char *addr);

// 传入addr  传出 hash后的 str
int getHash(const char *addr, std::string *str);

//向服务器发送字符串（hash(产品标识）+hash(MAC)），并获得服务端返回的授权字符
int sendStr(std::string ip, const int port, std::string in_str, std::string * out_str);

//将字符串写入到指定的授权文件中，传入文件路径，传入字符串
int writeAuthorizationFile(const char * filename, std::string *str);


//读取授权文件，传入一个文件名（路径+文件名），读取其内容（字符串）写入到输出参数中
int readAuthorizationFile(const char * filename, char * data);


// 比较两个字符串值是否相等 也就是比较两个hash值是否相等
int compare(std::string hash1, std::string hash2);

//获取传入文件的创建时间和修改时间
int getFileTime(std::string file, long *creativeTime, long *modifyTime);

int activeFunc(std::string model_dir, std::string produce_code, std::string android_id, FILE * fp);

#endif

