#include "CFurniture.h"

class CSofa : virtual public CFurniture{
public:
    CSofa();
    virtual ~CSofa();					// 沙发类虚析构函数
    virtual int GetColor();				// 获取沙发颜色
    virtual int SitDown();				// 沙发可以坐下休息
protected:
    int m_nColor;						// 沙发类成员变量
};