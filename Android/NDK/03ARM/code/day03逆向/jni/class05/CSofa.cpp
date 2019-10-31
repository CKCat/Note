#include "CSofa.h"

CSofa::CSofa(){
    m_nPrice = 1;
    m_nColor = 2;
    printf("CSofa()\r\n");
}
CSofa::~CSofa(){					// 沙发类虚析构函数
    printf("virtual ~CSofa()\r\n");
}
int CSofa::GetColor(){				// 获取沙发颜色
    printf("virtual CSofa::GetColor()\r\n");
    return m_nColor;
}
int CSofa::SitDown(){				// 沙发可以坐下休息
    printf("CSofa::SitDown Sit down and rest your legs\r\n");
    return 0;
}