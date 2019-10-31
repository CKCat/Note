#ifndef CFURNITURE_H
#define CFURNITURE_H
#include<stdio.h>

class CFurniture{
public:
    CFurniture();
    virtual ~CFurniture();				// 家具类的虚析构函数
    virtual int GetPrice();				// 获取家具价格
protected:
    int m_nPrice;						// 家具类的成员变量
};
#endif