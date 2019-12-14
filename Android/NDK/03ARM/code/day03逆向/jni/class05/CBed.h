#include "CFurniture.h"

class CBed : virtual public CFurniture{
public:
    CBed();
    virtual ~CBed();						// 床类的虚析构函数
    virtual int GetArea();					// 获取床面积
    virtual int Sleep();						// 床可以用来睡觉
protected:
    int m_nLength;						// 床类成员变量
    int m_nWidth;
};