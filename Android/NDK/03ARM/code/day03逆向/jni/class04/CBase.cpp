#include "CBase.h"

CBase::CBase() {
    m_nMember1 = 1;
    m_nMember2 = 2;
    printf("CBase()\r\n");
}
CBase::~CBase() {
    printf("~CBase()\r\n");
}
void CBase::fun1() {
    printf("CBase::fun1()\r\n");
}
void CBase::fun2() {
    printf("CBase::fun2()\r\n");
}