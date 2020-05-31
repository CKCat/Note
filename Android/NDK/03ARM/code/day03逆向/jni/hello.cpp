#include <stdio.h>

#include <string.h>

 

int main()

{

    int i,j;

    char buf[256]={0};

    char* pp = buf;

 

    printf("buf addr= 0x%x/r/n",buf);

    for(i=0;i<16;i++)

    {

        printf("addr = 0x%x ~ 0x%x/r/n",pp+i*16,pp+i*16+15);

        for(j=0;j<16;j++)

            *(pp+i*16+j)=i*16+j;

    }

 

    printf("ASCII table:/n");

    for(i=0;i<16;i++)

    {

        for(j=0;j<16;j++)

            printf("%c  ", *(pp+i*16+j));

        printf("/n");

    }

   

    return 0;

 

}