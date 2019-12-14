#文件名称
MODALE_NAME=hello

#ndk根目录
NDK_ROOT=D:/android-ndk-r10d

#编译器根目录
TOOLCHAINS_ROOT=$(NDK_ROOT)/toolchains/arm-linux-androideabi-4.9/prebuilt/windows-x86_64

#编译器目录
TOOLCHAINS_PREFIX=$(TOOLCHAINS_ROOT)/bin/arm-linux-androideabi

#头文件搜索路径
TOOLCHAINS_INCLUDE=$(TOOLCHAINS_ROOT)/lib/gcc/arm-linux-androideabi/4.9/include-fixed

#SDK根目录
PLATFROM_ROOT=$(NDK_ROOT)/platforms/android-19/arch-arm

#sdk头文件搜索路径
PLATFROM_INCLUDE=$(PLATFROM_ROOT)/usr/include

#sdk库文件搜索路径
PLATFROM_LIB=$(PLATFROM_ROOT)/usr/lib



#删除
RM=del

#编译选项
FLAGS=-I$(TOOLCHAINS_INCLUDE) \
      -I$(PLATFROM_INCLUDE)   \
      -L$(PLATFROM_LIB) \
      -nostdlib \
      -lgcc \
      -Bdynamic \
      -lc \

#所有obj文件
OBJS=$(MODALE_NAME).o \
     $(PLATFROM_LIB)/crtbegin_dynamic.o \
     $(PLATFROM_LIB)/crtend_android.o 


#编译器链接
all:
	$(TOOLCHAINS_PREFIX)-gcc $(FLAGS) -c chmod.c -o chmod.o
	$(TOOLCHAINS_PREFIX)-gcc $(FLAGS) -c ls.c -o ls.o
	$(TOOLCHAINS_PREFIX)-gcc $(FLAGS) -c $(MODALE_NAME).c -o $(MODALE_NAME).o
link:
	$(TOOLCHAINS_PREFIX)-gcc $(FLAGS) $(OBJS) ls_chmod.a -o $(MODALE_NAME)
lib:
	$(TOOLCHAINS_PREFIX)-ar r ls_chmod.a ls.o chmod.o
dll:
	$(TOOLCHAINS_PREFIX)-gcc $(FLAGS) -fPIC  -c chmod.c ls.c Hello.c
	$(TOOLCHAINS_PREFIX)-gcc -shared $(FLAGS) chmod.o ls.o -o libls_chmod.so 
	$(TOOLCHAINS_PREFIX)-gcc $(FLAGS) $(OBJS) libls_chmod.so  -o $(MODALE_NAME)
usedll:
	$(TOOLCHAINS_PREFIX)-gcc $(FLAGS) -c $(MODALE_NAME).c -o $(MODALE_NAME).o
	$(TOOLCHAINS_PREFIX)-gcc $(FLAGS) $(OBJS) libls_chmod.so -o $(MODALE_NAME)
listlib:
	$(TOOLCHAINS_PREFIX)-ar t ls_chmod.a
#删除所有.o文件
clean:
	$(RM) *.o
#安装程序到手机#adb push libls_chmod.so /data/local/tmp/
install:
	adb push $(MODALE_NAME) /data/local/tmp
	adb shell chmod 755 /data/local/tmp/$(MODALE_NAME)
	adb shell /data/local/tmp/$(MODALE_NAME) 
	
#运行程序
run:
	adb shell /data/local/tmp/$(MODALE_NAME)  
