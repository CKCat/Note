#文件名称
MODALE_NAME=hello

#ndk根目录
NDK_ROOT=E:/Android/Sdk/ndk-bundle

#编译器根目录
TOOLCHAINS_ROOT_ARM=$(NDK_ROOT)/toolchains/arm-linux-androideabi-4.9/prebuilt/windows-x86_64
TOOLCHAINS_ROOT_AARCH64=$(NDK_ROOT)/toolchains/aarch64-linux-android-4.9/prebuilt/windows-x86_64

#编译器目录
TOOLCHAINS_PREFIX=$(TOOLCHAINS_ROOT_ARM)/bin/arm-linux-androideabi
TOOLCHAINS_PREFIX_AARCH64=$(TOOLCHAINS_ROOT_AARCH64)/bin/aarch64-linux-android

#头文件搜索路径
TOOLCHAINS_INCLUDE=$(TOOLCHAINS_ROOT_ARM)/lib/gcc/arm-linux-androideabi/4.9.x/include-fixed
TOOLCHAINS_INCLUDE_AARCH64=$(TOOLCHAINS_ROOT_AARCH64)/lib/gcc/aarch64-linux-android/4.9.x/include-fixed

#SDK根目录
PLATFROM_ROOT=$(NDK_ROOT)/platforms/android-21/arch-arm
PLATFROM_ROOT_AARCH64=$(NDK_ROOT)/platforms/android-21/arch-arm64

#sdk头文件搜索路径
PLATFROM_INCLUDE=$(PLATFROM_ROOT)/usr/include
PLATFROM_INCLUDE_AARCH64=$(PLATFROM_ROOT_AARCH64)/usr/include

#sdk库文件搜索路径
PLATFROM_LIB=$(PLATFROM_ROOT)/usr/lib
PLATFROM_LIB_AARCH64=$(PLATFROM_ROOT_AARCH64)/usr/lib



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
      -fPIE

FLAGS_AARCH64=-I$(TOOLCHAINS_INCLUDE_AARCH64) \
      -I$(PLATFROM_INCLUDE_AARCH64)   \
      -L$(PLATFROM_LIB_AARCH64) \
      -nostdlib \
      -lgcc \
      -Bdynamic \
      -lc 

#所有obj文件
OBJS=$(MODALE_NAME).o \
     $(PLATFROM_LIB)/crtbegin_dynamic.o \
     $(PLATFROM_LIB)/crtend_android.o 

OBJS_AARCH64=$(MODALE_NAME)64.o \
     $(PLATFROM_LIB_AARCH64)/crtbegin_dynamic.o \
     $(PLATFROM_LIB_AARCH64)/crtend_android.o 

#编译器链接
all:
	# $(TOOLCHAINS_PREFIX)-gcc $(FLAGS) -E $(MODALE_NAME).c -o $(MODALE_NAME).i
	# $(TOOLCHAINS_PREFIX)-gcc $(FLAGS)  -S $(MODALE_NAME).i -o $(MODALE_NAME).s
	$(TOOLCHAINS_PREFIX)-gcc $(FLAGS) -c $(MODALE_NAME).s -o $(MODALE_NAME).o
	$(TOOLCHAINS_PREFIX)-gcc $(FLAGS) $(OBJS) -fPIE -pie -o $(MODALE_NAME)

all64:
	# $(TOOLCHAINS_PREFIX_AARCH64)-gcc $(FLAGS_AARCH64) -E $(MODALE_NAME).c -o $(MODALE_NAME)64.i
	# $(TOOLCHAINS_PREFIX_AARCH64)-gcc $(FLAGS_AARCH64)  -S $(MODALE_NAME)64.i -o $(MODALE_NAME)64.s
	$(TOOLCHAINS_PREFIX_AARCH64)-gcc $(FLAGS_AARCH64) -c $(MODALE_NAME)64.s -o $(MODALE_NAME)64.o
	$(TOOLCHAINS_PREFIX_AARCH64)-gcc $(FLAGS_AARCH64) $(OBJS_AARCH64) -o $(MODALE_NAME)64
#删除所有.o文件
clean:
	$(RM) *.o
#安装程序到手机
install:
	adb push $(MODALE_NAME) /data/local/tmp
	adb shell chmod 755 /data/local/tmp/$(MODALE_NAME)
	adb shell /data/local/tmp/$(MODALE_NAME) 
debug:
	adb forward tcp:23946 tcp:23946
	adb shell /data/local/tmp/android_server

install64:
	adb push $(MODALE_NAME)64 /data/local/tmp
	adb shell chmod 755 /data/local/tmp/$(MODALE_NAME)64
	adb shell /data/local/tmp/$(MODALE_NAME)64 

debug64:
	adb forward tcp:23946 tcp:23946
	adb shell /data/local/tmp/android_server64
#运行程序
run:
	adb shell /data/local/tmp/$(MODALE_NAME)  

run64:
	adb shell /data/local/tmp/$(MODALE_NAME)64 


