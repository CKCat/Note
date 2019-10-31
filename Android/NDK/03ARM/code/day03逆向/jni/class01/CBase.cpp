#include<stdio.h>
#include "CBase.h"

CBase::CBase(int n1, int n2) {
    m_nMember1 = n1;
    m_nMember2 = n2;
    printf("CBase()\r\n");
}
CBase::~CBase() {
    m_nMember1 = 0;
    m_nMember2 = 0;
    printf("~CBase()\r\n");
}
void CBase::fun1(int n) {
    printf("CBase::fun1(), %d\r\n", n*m_nMember1*m_nMember2);
}
void CBase::fun2(int n) {
    printf("CBase::fun2(), %d\r\n", n*m_nMember1*m_nMember2);
}


