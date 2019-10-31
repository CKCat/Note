#include "CSofa.h"
#include "CBed.h"

class CSofaBed :  public CSofa,  public CBed{
public:
    CSofaBed();
    virtual ~CSofaBed();					// 沙发床类的虚析构函数
    virtual int SitDown();					// 沙发可以坐下休息
    virtual int Sleep();						// 床可以用来睡觉
    virtual int GetHeight();
protected:
    int m_nHeight;							// 沙发类的成员变量
};