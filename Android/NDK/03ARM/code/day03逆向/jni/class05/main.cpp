#include "CSofaBed.h"

int main(int argc, char* argv[]){
    CSofaBed  SofaBed;
    CSofaBed *p = &SofaBed;
    p->SitDown();
    //子类指针转基类指针
    CSofa *psf = &SofaBed;
    psf->SitDown();

    CBed *pb = &SofaBed;
    pb->Sleep();

    CFurniture *pf = &SofaBed;
    pf->GetPrice();

    p = new CSofaBed;
    if(p != NULL)
        p->Sleep();
        p->SitDown();
        p->GetPrice();
    delete p;
}