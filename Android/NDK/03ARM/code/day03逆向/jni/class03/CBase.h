#include<stdio.h>

class CBase {
public:
    CBase();
    virtual ~CBase();
    virtual void fun1();
    virtual void fun2();
private:
    int m_nMember1;
    int m_nMember2;
};