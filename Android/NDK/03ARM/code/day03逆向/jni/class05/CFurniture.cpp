#include "CFurniture.h"

CFurniture::CFurniture(){
    m_nPrice = 0;
    printf("CFurniture()\r\n");
}

CFurniture::~CFurniture(){				// 家具类的虚析构函数
    printf("virtual ~CFurniture()\r\n");
}

int CFurniture::GetPrice(){				// 获取家具价格
    printf("virtual CFurniture::GetPrice()\r\n");
    return m_nPrice;
}