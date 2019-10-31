
#include "CBase.h"
class CDerived :public CBase{
public:
    CDerived();
    ~CDerived();
    void fun1();
    void fun3();
private:
    int m_nMember;
};