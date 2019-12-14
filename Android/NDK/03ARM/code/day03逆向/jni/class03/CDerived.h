
#include "CBase.h"
class CDerived :public CBase{
public:
    CDerived();
    virtual ~CDerived();
    virtual void fun1();
    virtual void fun3();
private:
    int m_nMember;
};