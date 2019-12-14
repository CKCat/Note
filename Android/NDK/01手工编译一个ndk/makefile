#文件名称
MODALE_NAME=hello

#ndk根目录
NDK_ROOT=E:\Android\Sdk\ndk-bundle

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
      -fPIE       # 设置pie
      
# 在mk文件中设置pie的方法如下：
# LOCAL_CFLAGS += -fPIE
# LOCAL_LDFLAGS += -fPIE -pie

#所有obj文件
OBJS=$(MODALE_NAME).o \
     $(PLATFROM_LIB)/crtbegin_dynamic.o \
     $(PLATFROM_LIB)/crtend_android.o 


#编译器链接
all:
	$(TOOLCHAINS_PREFIX)-gcc.exe $(FLAGS) -c $(MODALE_NAME).c -o $(MODALE_NAME).o
	$(TOOLCHAINS_PREFIX)-gcc.exe $(FLAGS) $(OBJS) -fPIE -pie -o $(MODALE_NAME)    # 设置pie
#删除所有.o文件
clean:
	$(RM) *.o
#安装程序到手机
install:
	adb push $(MODALE_NAME) /data/local/tmp
	adb shell chmod 755 /data/local/tmp/$(MODALE_NAME)
	adb shell /data/local/tmp/$(MODALE_NAME) 
#运行程序
run:
	adb shell /data/local/tmp/$(MODALE_NAME)  
